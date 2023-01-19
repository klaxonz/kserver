package com.klaxon.kserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klaxon.kserver.entity.dao.Group;
import com.klaxon.kserver.entity.vo.GroupVO;
import com.klaxon.kserver.exception.BizCodeEnum;
import com.klaxon.kserver.pojo.Response;
import com.klaxon.kserver.service.IGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private IGroupService groupService;

    @PostMapping("/add")
    public <R> Response<R> add(@RequestBody @Validated GroupVO groupVO) {
        groupService.add(groupVO.getGroupName());
        return Response.success();
    }

    @GetMapping("/get/{id}")
    public Response<Object> getOne(@PathVariable("id") Long id) {
        Group group = groupService.getOne(id);
        return group == null ? Response.error(BizCodeEnum.GROUP_0020002) : Response.success(group);
    }

    @GetMapping("/page/{page}")
    public Page<Group> page(@PathVariable("page") Integer page) {
        return groupService.page(page);
    }

    @PostMapping("/remove")
    public <R> Response<R> remove(@RequestBody GroupVO params) {
        groupService.remove(params.getId());
        return Response.success();
    }

}
