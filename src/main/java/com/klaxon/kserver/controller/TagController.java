package com.klaxon.kserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klaxon.kserver.entity.dao.Tag;
import com.klaxon.kserver.entity.vo.TagVO;
import com.klaxon.kserver.exception.BizCodeEnum;
import com.klaxon.kserver.pojo.Response;
import com.klaxon.kserver.service.ITagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private ITagService tagService;

    @PostMapping("/add")
    public <R> Response<R> add(@RequestBody @Validated TagVO tagVO) {
        tagService.add(tagVO.getTagName());
        return Response.success();
    }

    @GetMapping("/get/{id}")
    public Response<Object> getOne(@PathVariable("id") Long id) {
        Tag tag = tagService.getOne(id);
        return tag == null ? Response.error(BizCodeEnum.GROUP_0020002) : Response.success(tag);
    }

    @GetMapping("/page/{page}")
    public Page<Tag> page(@PathVariable("page") Integer page) {
        return tagService.page(page);
    }

    @PostMapping("/remove")
    public <R> Response<R> remove(@RequestBody TagVO params) {
        tagService.remove(params.getId());
        return Response.success();
    }

}
