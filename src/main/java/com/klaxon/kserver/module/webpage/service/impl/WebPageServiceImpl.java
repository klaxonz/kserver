package com.klaxon.kserver.module.webpage.service.impl;

import com.klaxon.kserver.constants.WebPageConstants;
import com.klaxon.kserver.module.webpage.mapper.WebPageMapper;
import com.klaxon.kserver.module.webpage.model.entity.WebPage;
import com.klaxon.kserver.module.webpage.model.req.WebPageAddReq;
import com.klaxon.kserver.module.webpage.service.WebPageService;
import com.klaxon.kserver.util.ThreadLocalHolder;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;

/**
 * @author klaxon
 */
@Service
public class WebPageServiceImpl implements WebPageService {

	private final Logger log = LoggerFactory.getLogger(WebPageServiceImpl.class);

	@Resource
	private WebPageMapper webPageMapper;

	@Override
	public void add(WebPageAddReq req) {
		WebPage webPage = extractWebPage(req.getUrl());
		webPageMapper.insert(webPage);
	}

	/**
	 * 获取网页数据，数据包括一下内容：
	 * 1. title: 网页标题，可为空
	 * 2. content: 网页正文，可为空
	 * 3. description: 网页描述，可为空
	 * @param url	保存的网页
	 * @return	解释的网页数据
	 */
	private WebPage extractWebPage(String url) {
		Document document = requestWebPage(url);

		String title = getTitle(document);
		String content = getContent(document);
		String description = getDescription(document);
		String favicon = getFavicon(url);

		WebPage webPage = new WebPage();
		webPage.setUserId(ThreadLocalHolder.getUser().getId());
		webPage.setUrl(url);
		webPage.setTitle(title);
		webPage.setContent(content);
		webPage.setFavicon(favicon);
		webPage.setDescription(description);

		return webPage;
	}

	private Document requestWebPage(String url) {
		Document document = null;
		try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			log.warn("请求异常, url: {}", url, e);
		}
		return document;
	}

	private String getTitle(Document document) {
		if (Objects.isNull(document)) {
			return Strings.EMPTY;
		}
		return document.title();
	}

	private String getDescription(Document document) {
		if (Objects.isNull(document)) {
			return Strings.EMPTY;
		}
		Elements elements = document.select("meta[name=description]");
		if (!elements.isEmpty()) {
			return elements.get(0).attr("content");
		}

		return Strings.EMPTY;
	}

	private String getContent(Document document) {
		if (Objects.isNull(document)) {
			return Strings.EMPTY;
		}
		return document.body().text();
	}

	private String getFavicon(String url) {
		return WebPageConstants.FAVICON_BASE_URL + url;
	}

}
