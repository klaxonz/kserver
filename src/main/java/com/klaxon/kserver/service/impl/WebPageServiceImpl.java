package com.klaxon.kserver.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.klaxon.kserver.bean.BasePage;
import com.klaxon.kserver.constants.WebPageConstants;
import com.klaxon.kserver.controller.vo.WebPageDetail;
import com.klaxon.kserver.converter.WebPageMapperStruct;
import com.klaxon.kserver.downloader.DownloadTask;
import com.klaxon.kserver.mapper.WebPageMapper;
import com.klaxon.kserver.mapper.model.WebPage;
import com.klaxon.kserver.mapper.model.WebPageTask;
import com.klaxon.kserver.service.WebPageService;
import com.klaxon.kserver.service.dto.WebPageDto;
import com.klaxon.kserver.util.ThreadLocalHolder;

import cn.hutool.core.net.url.UrlBuilder;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WebPageServiceImpl implements WebPageService {

	@Resource
	private WebPageMapper webPageMapper;
	@Resource
	private WebPageMapperStruct webPageMapperStruct;
	@Resource
	private ApplicationContext context;
	@Resource
	@Qualifier("videoDownloadTaskExecutor")
	private ThreadPoolTaskExecutor videoDownloadTaskExecutor;

	public static boolean isYtDlpSupported(String url) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("yt-dlp", "-F", url);
			Process process = processBuilder.start();
			InputStream inputStream = process.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			boolean showSeparator = false;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("---------------")) {
					showSeparator = true;
				} else {
					if (showSeparator) {
						return true;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	@Transactional
	public WebPageDto createWebPage(WebPageDto webPageDTO) {

		String url = webPageDTO.getUrl();
		Document document = null;
		String title = Strings.EMPTY;
		String description = Strings.EMPTY;
		try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			log.error("请求异常", e);
			title = url;
		}

		if (!Objects.isNull(document)) {
			title = document.title();
			Elements elements = document.select("meta[name=description]");
			if (!elements.isEmpty()) {
				description = elements.get(0).attr("content");
			}
		}
		UrlBuilder urlBuilder = UrlBuilder.of(url);
		String source = String.join("://", urlBuilder.getScheme(), urlBuilder.getHost());

		WebPage webPage = new WebPage();
		webPage.setUserId(ThreadLocalHolder.getUser().getId());
		webPage.setUrl(url);
		webPage.setIsStar("0");
		webPage.setTitle(title);
		webPage.setSource(source);
		webPage.setFavicon(WebPageConstants.FAVICON_BASE_URL + url);
		webPage.setDescription(description);
		webPageMapper.insert(webPage);

		boolean isSupported = isYtDlpSupported(url);
		if (isSupported) {
			DownloadTask downloadTask = context.getBean(DownloadTask.class, ThreadLocalHolder.getUser(),
					new WebPageTask(),
					webPage);
			videoDownloadTaskExecutor.submit(downloadTask);
		}

		return webPageMapperStruct.entityToDto(webPage);
	}

	@Override
	public WebPageDto getWebPage(Long id) {
		WebPage webPage = webPageMapper.selectOne(new LambdaQueryWrapper<WebPage>()
				.eq(WebPage::getIsDelete, 0)
				.eq(WebPage::getUserId, ThreadLocalHolder.getUser().getId())
				.eq(WebPage::getId, id));
		return webPageMapperStruct.entityToDto(webPage);
	}

	@Override
	public WebPageDetail countWebPage() {
		// all count
		LambdaQueryWrapper<WebPage> allQueryWrapper = new LambdaQueryWrapper<>();
		allQueryWrapper
				.eq(WebPage::getIsDelete, 0)
				.eq(WebPage::getUserId, ThreadLocalHolder.getUser().getId())
				.orderByDesc(WebPage::getCreateTime);
		Integer allTotal = Math.toIntExact(webPageMapper.selectCount(allQueryWrapper));

		// star count
		LambdaQueryWrapper<WebPage> starQueryWrapper = new LambdaQueryWrapper<>();
		starQueryWrapper
				.eq(WebPage::getIsDelete, 0)
				.eq(WebPage::getUserId, ThreadLocalHolder.getUser().getId())
				.eq(WebPage::getIsStar, "1")
				.orderByDesc(WebPage::getCreateTime);
		Integer starTotal = Math.toIntExact(webPageMapper.selectCount(starQueryWrapper));

		// today count
		LambdaQueryWrapper<WebPage> todayQueryWrapper = new LambdaQueryWrapper<>();
		todayQueryWrapper
				.eq(WebPage::getIsDelete, 0)
				.eq(WebPage::getUserId, ThreadLocalHolder.getUser().getId())
				.apply("to_days(create_time) = to_days(now())")
				.orderByDesc(WebPage::getCreateTime);
		Integer todayTotal = Math.toIntExact(webPageMapper.selectCount(todayQueryWrapper));

		WebPageDetail webPageDetail = new WebPageDetail();
		webPageDetail.setAll(allTotal);
		webPageDetail.setStar(starTotal);
		webPageDetail.setToday(todayTotal);

		return webPageDetail;
	}

	@Override
	public IPage<WebPageDto> listByPage(WebPageDto webPageDto) {
		BasePage<WebPage> searchPage = new BasePage<>(webPageDto.getPage(), webPageDto.getPageSize());
		LambdaQueryWrapper<WebPage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper
				.eq(WebPage::getIsDelete, 0)
				.eq(WebPage::getUserId, ThreadLocalHolder.getUser().getId())
				.orderByDesc(WebPage::getCreateTime);

		if (StringUtils.equals(webPageDto.getType(), "star")) {
			lambdaQueryWrapper.eq(WebPage::getIsStar, "1");
		}
		if (StringUtils.equals(webPageDto.getType(), "today")) {
			lambdaQueryWrapper.apply("to_days(create_time) = to_days(now())");
		}

		if (StringUtils.isNotBlank(webPageDto.getQuery())) {
			lambdaQueryWrapper.and(webPageLambdaQueryWrapper -> webPageLambdaQueryWrapper.like(WebPage::getTitle, webPageDto.getQuery())
					.or()
					.like(WebPage::getDescription, webPageDto.getQuery()));
		}
		BasePage<WebPage> webPageIPage = webPageMapper.selectPage(searchPage, lambdaQueryWrapper);
		return webPageIPage.convert(item -> webPageMapperStruct.entityToDto(item));
	}

	@Override
	public void deleteWebPage(Long webPageId) {
		LambdaQueryWrapper<WebPage> queryWrapper = new LambdaQueryWrapper<WebPage>()
				.eq(WebPage::getUserId, ThreadLocalHolder.getUser().getId())
				.eq(WebPage::getId, webPageId);
		WebPage webPage = new WebPage();
		webPage.setId(webPageId);
		webPage.setIsDelete(1);
		webPageMapper.update(webPage, queryWrapper);
	}

	@Override
	public void batchDeleteWebPage(List<Long> webPageIds) {
		LambdaQueryWrapper<WebPage> queryWrapper = new LambdaQueryWrapper<WebPage>()
				.eq(WebPage::getUserId, ThreadLocalHolder.getUser().getId())
				.in(WebPage::getId, webPageIds);
		WebPage webPage = new WebPage();
		webPage.setIsDelete(1);
		webPageMapper.update(webPage, queryWrapper);
	}
}
