package com.klaxon.kserver.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.klaxon.kserver.controller.vo.WebPageDetail;
import com.klaxon.kserver.service.dto.WebPageDto;

public interface WebPageService {

	WebPageDto createWebPage(WebPageDto webPageDTO);

	WebPageDto getWebPage(Long webPageId);

	WebPageDetail countWebPage();

	IPage<WebPageDto> listByPage(WebPageDto webPageDTO, String query);

	void deleteWebPage(Long webPageId);

	void batchDeleteWebPage(List<Long> webPageIds);

}
