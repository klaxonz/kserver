package com.klaxon.kserver.module.webpage.controller;

import com.klaxon.kserver.bean.Response;
import com.klaxon.kserver.module.webpage.model.req.WebPageAddReq;
import com.klaxon.kserver.module.webpage.model.req.WebPageListReq;
import com.klaxon.kserver.module.webpage.model.rsp.WebPageAddRsp;
import com.klaxon.kserver.module.webpage.service.WebPageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author klaxon
 */
@Api(tags = "网页收藏")
@RestController
@RequestMapping("/web-page")
public class WebPageController {

	@Resource
	private WebPageService webPageService;

	@ApiOperation(value = "保存网页")
	@PostMapping("/add")
	public Response<WebPageAddRsp> add(@RequestBody @Validated WebPageAddReq req) {
		webPageService.add(req);
		return Response.success();
	}

	@ApiOperation(value = "查询网页")
	@GetMapping("/list")
	public Response<WebPageAddRsp> list(@RequestBody @Validated WebPageListReq req) {
		return Response.success();
	}

}
