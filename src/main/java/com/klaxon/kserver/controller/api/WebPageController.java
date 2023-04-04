package com.klaxon.kserver.controller.api;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.klaxon.kserver.bean.Response;
import com.klaxon.kserver.controller.vo.WebPageDetail;
import com.klaxon.kserver.controller.vo.WebPageVo;
import com.klaxon.kserver.converter.WebPageMapperStruct;
import com.klaxon.kserver.service.WebPageService;
import com.klaxon.kserver.service.dto.WebPageDto;

@RestController
@RequestMapping("/web-page")
public class WebPageController {

	@Resource
	private WebPageService webPageService;
	@Resource
	private WebPageMapperStruct webPageMapperStruct;

	@PostMapping("/create")
	public Response<Object> create(@RequestBody @Validated WebPageVo webPageVo) {
		WebPageDto webPageDto = webPageMapperStruct.voToDto(webPageVo);
		return Response.success(webPageService.createWebPage(webPageDto));
	}

	@GetMapping("/count")
	public WebPageDetail count() {
		return webPageService.countWebPage();
	}

	@PostMapping("/list")
	public Response<Object> list(@RequestBody WebPageVo webPageVo,
			@RequestParam(value = "q", required = false) String query) {
		WebPageDto webPageDTO = webPageMapperStruct.voToDto(webPageVo);
		IPage<WebPageVo> webPageVoIPage = webPageService
				.listByPage(webPageDTO, query)
				.convert(item -> webPageMapperStruct.dtoToVo(item));

		return Response.success(webPageVoIPage);
	}

	@PostMapping("/delete")
	public <R> Response<R> delete(@RequestBody WebPageVo webPageVo) {
		webPageService.deleteWebPage(webPageVo.getId());
		return Response.success();
	}

	@PostMapping("/batch-delete")
	public <R> Response<R> batchDelete(@RequestBody List<Long> webpageIds) {
		webPageService.batchDeleteWebPage(webpageIds);
		return Response.success();
	}

}
