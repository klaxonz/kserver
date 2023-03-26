package com.klaxon.kserver.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.klaxon.kserver.entity.dao.WebPage;
import com.klaxon.kserver.entity.dto.WebPageDto;
import com.klaxon.kserver.entity.vo.WebPageDetail;
import com.klaxon.kserver.entity.vo.WebPageTagVo;
import com.klaxon.kserver.entity.vo.WebPageVo;
import com.klaxon.kserver.exception.BizCodeEnum;
import com.klaxon.kserver.mapperstruct.WebPageMapperStruct;
import com.klaxon.kserver.pojo.Response;
import com.klaxon.kserver.service.IWebPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/webpage")
public class WebPageController {

    @Autowired
    private IWebPageService webPageService;

    @Autowired
    private WebPageMapperStruct webPageMapperStruct;

    @PostMapping("/add")
    public Response<Object> add(@RequestBody @Validated WebPageVo params) {
        WebPageDto webPageDto = webPageMapperStruct.voToDto(params);
        return Response.success(webPageService.add(webPageDto));
    }

    @PostMapping("/addTag")
    public <R> Response<R> addTag(@RequestBody WebPageTagVo webPageTagVO) {
        webPageService.addTags(webPageTagVO);
        return Response.success();
    }

    @PostMapping("/removeTag")
    public <R> Response<R> removeTag(@RequestBody WebPageTagVo webPageTagVO) {
        webPageService.removeTags(webPageTagVO);
        return Response.success();
    }

    @PostMapping("/changeGroup")
    public <R> Response<R> changeGroup(@RequestBody WebPageVo params) {
        webPageService.changeGroup(params.getId(), params.getGroupId());
        return Response.success();
    }

    @GetMapping("/get/{id}")
    public Response<Object> getOne(@PathVariable("id") Long id) {
        WebPage resource = webPageService.getOne(id);
        return resource == null ? Response.error(BizCodeEnum.WEBPAGE_0010001) : Response.success(resource);
    }

    @GetMapping("/detail")
    public WebPageDetail detail() {
        return webPageService.detail();
    }

    @PostMapping("/list")
    public IPage<WebPage> list(@RequestBody WebPageDto webPageDto,  @RequestParam(value = "q", required = false) String question) {
        return webPageService.list(webPageDto, question);
    }

    @PostMapping("/remove")
    public <R> Response<R> remove(@RequestBody WebPage params) {
        webPageService.remove(params.getId());
        return Response.success();
    }

    @PostMapping("/batchRemove")
    public <R> Response<R> batchRemove(@RequestBody List<Long> webpageIds) {
        webPageService.batchRemove(webpageIds);
        return Response.success();
    }

}
