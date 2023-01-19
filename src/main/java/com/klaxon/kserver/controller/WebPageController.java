package com.klaxon.kserver.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.klaxon.kserver.entity.dao.WebPage;
import com.klaxon.kserver.entity.dto.WebPageDTO;
import com.klaxon.kserver.entity.vo.WebPageDetail;
import com.klaxon.kserver.entity.vo.WebPageTagVO;
import com.klaxon.kserver.entity.vo.WebPageVO;
import com.klaxon.kserver.exception.BizCodeEnum;
import com.klaxon.kserver.pojo.Response;
import com.klaxon.kserver.service.IWebPageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/webpage")
public class WebPageController {

    @Autowired
    private IWebPageService webPageService;

    @PostMapping("/add")
    public <R> Response<R> add(@RequestBody @Validated WebPageVO params) {
        WebPageDTO webPageDTO = new WebPageDTO();
        BeanUtils.copyProperties(params, webPageDTO);
        webPageService.add(webPageDTO);
        return Response.success();
    }

    @PostMapping("/addTag")
    public <R> Response<R> addTag(@RequestBody WebPageTagVO webPageTagVO) {
        webPageService.addTags(webPageTagVO);
        return Response.success();
    }

    @PostMapping("/removeTag")
    public <R> Response<R> removeTag(@RequestBody WebPageTagVO webPageTagVO) {
        webPageService.removeTags(webPageTagVO);
        return Response.success();
    }

    @PostMapping("/changeGroup")
    public <R> Response<R> changeGroup(@RequestBody WebPageVO params) {
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

    @GetMapping("/list/all/{page}")
    public IPage<WebPage> listAll(@PathVariable("page") Integer page) {
        return webPageService.getAll(page);
    }

    @GetMapping("/list/star/{page}")
    public IPage<WebPage> listStar(@PathVariable("page") Integer page) {
        return webPageService.getStar(page);
    }

    @GetMapping("/list/today/{page}")
    public IPage<WebPage> listToday(@PathVariable("page") Integer page) {
        return webPageService.getToday(page);
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

    @GetMapping("/search")
    public <R> IPage<WebPage> search(@RequestParam("type") String type, @RequestParam("q") String question, @RequestParam("page") Integer page) {
        return webPageService.search(type, question, page);
    }

}
