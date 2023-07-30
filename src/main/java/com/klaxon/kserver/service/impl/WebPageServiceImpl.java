package com.klaxon.kserver.service.impl;

import cn.hutool.core.net.url.UrlBuilder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.klaxon.kserver.bean.BasePage;
import com.klaxon.kserver.constants.CommonConstants;
import com.klaxon.kserver.constants.WebPageConstants;
import com.klaxon.kserver.controller.vo.WebPageDetail;
import com.klaxon.kserver.converter.WebPageMapperStruct;
import com.klaxon.kserver.downloader.Config;
import com.klaxon.kserver.downloader.DownloadCallback;
import com.klaxon.kserver.downloader.DownloadTask;
import com.klaxon.kserver.downloader.YtDlpExecutorNew;
import com.klaxon.kserver.mapper.WebPageMapper;
import com.klaxon.kserver.mapper.model.WebPage;
import com.klaxon.kserver.property.YtDlpProperty;
import com.klaxon.kserver.service.WebPageService;
import com.klaxon.kserver.service.dto.WebPageDto;
import com.klaxon.kserver.util.ThreadLocalHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class WebPageServiceImpl implements WebPageService {

	private final Logger log = LoggerFactory.getLogger(WebPageServiceImpl.class);

	@Resource
	private Config config;
	@Resource
	private YtDlpProperty property;
	@Resource
	private DownloadCallback callback;

	@Resource
	private WebPageMapper webPageMapper;
	@Resource
	private WebPageMapperStruct webPageMapperStruct;

	@Resource
	@Qualifier("videoDownloadTaskExecutor")
	private ThreadPoolTaskExecutor videoDownloadTaskExecutor;


	@Override
	@Transactional
	public WebPageDto createWebPage(WebPageDto webPageDTO) {

		WebPage webPage = parseWebPage(webPageDTO.getUrl());
		webPageMapper.insert(webPage);

		config.setProperty(property);
		YtDlpExecutorNew executor = new YtDlpExecutorNew(config);
		boolean isSupported = executor.isSupported(webPageDTO.getUrl());
		if (isSupported) {
			DownloadTask downloadTask = new DownloadTask(ThreadLocalHolder.getUser(), config, webPage, callback);
			videoDownloadTaskExecutor.submit(downloadTask);
		}

		return webPageMapperStruct.entityToDto(webPage);
	}


	private WebPage parseWebPage(String url) {

		Document document = null;
		String title = Strings.EMPTY;
		String content = Strings.EMPTY;
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
			content = document.body().text();
		}

		UrlBuilder urlBuilder = UrlBuilder.of(url);
		String source = String.join("://", urlBuilder.getScheme(), urlBuilder.getHost());

		WebPage webPage = new WebPage();
		webPage.setUserId(ThreadLocalHolder.getUser().getId());
		webPage.setUrl(url);
		webPage.setIsStar(CommonConstants.NO);
		webPage.setTitle(title);
		webPage.setContent(content);
		webPage.setSource(source);
		webPage.setFavicon(WebPageConstants.FAVICON_BASE_URL + url);
		webPage.setDescription(description);

		return webPage;
	}

	@Override
	public WebPageDto getWebPage(Long id) {
		WebPage webPage = webPageMapper.selectOne(new LambdaQueryWrapper<WebPage>()
				.eq(WebPage::getIsDelete, CommonConstants.NO)
				.eq(WebPage::getUserId, ThreadLocalHolder.getUser().getId())
				.eq(WebPage::getId, id));
		return webPageMapperStruct.entityToDto(webPage);
	}

	@Override
	public List<WebPageDetail> countWebPage() {
		List<WebPageDetail> webPageDetailList = Lists.newLinkedList();

		// all count
		LambdaQueryWrapper<WebPage> allQueryWrapper = new LambdaQueryWrapper<>();
		allQueryWrapper
				.eq(WebPage::getIsDelete, CommonConstants.NO)
				.eq(WebPage::getUserId, ThreadLocalHolder.getUser().getId())
				.orderByDesc(WebPage::getCreateTime);
		Integer allTotal = Math.toIntExact(webPageMapper.selectCount(allQueryWrapper));
		webPageDetailList.add(new WebPageDetail(WebPageConstants.WEB_PAGE_TYPE_ALL, allTotal));

		// star count
		LambdaQueryWrapper<WebPage> starQueryWrapper = new LambdaQueryWrapper<>();
		starQueryWrapper
				.eq(WebPage::getIsDelete, CommonConstants.NO)
				.eq(WebPage::getUserId, ThreadLocalHolder.getUser().getId())
				.eq(WebPage::getIsStar, CommonConstants.YES)
				.orderByDesc(WebPage::getCreateTime);
		Integer starTotal = Math.toIntExact(webPageMapper.selectCount(starQueryWrapper));
		webPageDetailList.add(new WebPageDetail(WebPageConstants.WEB_PAGE_TYPE_STAR, starTotal));

		// today count
		LambdaQueryWrapper<WebPage> todayQueryWrapper = new LambdaQueryWrapper<>();
		todayQueryWrapper
				.eq(WebPage::getIsDelete, CommonConstants.NO)
				.eq(WebPage::getUserId, ThreadLocalHolder.getUser().getId())
				.apply("to_days(create_time) = to_days(now())")
				.orderByDesc(WebPage::getCreateTime);
		Integer todayTotal = Math.toIntExact(webPageMapper.selectCount(todayQueryWrapper));
		webPageDetailList.add(new WebPageDetail(WebPageConstants.WEB_PAGE_TYPE_TODAY, todayTotal));

		return webPageDetailList;
	}

	@Override
	public IPage<WebPageDto> listByPage(WebPageDto webPageDto) {
		BasePage<WebPage> searchPage = new BasePage<>(webPageDto.getPage(), webPageDto.getPageSize());
		LambdaQueryWrapper<WebPage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper
				.eq(WebPage::getIsDelete, CommonConstants.NO)
				.eq(WebPage::getUserId, ThreadLocalHolder.getUser().getId())
				.orderByDesc(WebPage::getCreateTime);

		if (Objects.equals(webPageDto.getType(), WebPageConstants.WEB_PAGE_TYPE_STAR)) {
			lambdaQueryWrapper.eq(WebPage::getIsStar, CommonConstants.YES);
		}
		if (Objects.equals(webPageDto.getType(), WebPageConstants.WEB_PAGE_TYPE_TODAY)) {
			lambdaQueryWrapper.apply("to_days(create_time) = to_days(now())");
		}

		if (StringUtils.isNotBlank(webPageDto.getQuery())) {
			lambdaQueryWrapper.and(webPageLambdaQueryWrapper -> webPageLambdaQueryWrapper.like(WebPage::getTitle, webPageDto.getQuery())
					.or()
					.like(WebPage::getDescription, webPageDto.getQuery())
					.or()
					.like(WebPage::getContent, webPageDto.getQuery())
			);
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
		webPage.setIsDelete(CommonConstants.YES);
		webPageMapper.update(webPage, queryWrapper);
	}

	@Override
	public void batchDeleteWebPage(List<Long> webPageIds) {
		LambdaQueryWrapper<WebPage> queryWrapper = new LambdaQueryWrapper<WebPage>()
				.eq(WebPage::getUserId, ThreadLocalHolder.getUser().getId())
				.in(WebPage::getId, webPageIds);
		WebPage webPage = new WebPage();
		webPage.setIsDelete(CommonConstants.YES);
		webPageMapper.update(webPage, queryWrapper);
	}
}
