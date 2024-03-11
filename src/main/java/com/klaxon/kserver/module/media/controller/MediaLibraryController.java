package com.klaxon.kserver.module.media.controller;

import com.klaxon.kserver.bean.PageInfo;
import com.klaxon.kserver.bean.Response;
import com.klaxon.kserver.module.media.model.req.MediaLibraryAddReq;
import com.klaxon.kserver.module.media.model.req.MediaLibraryDeleteReq;
import com.klaxon.kserver.module.media.model.req.MediaLibraryListReq;
import com.klaxon.kserver.module.media.model.req.MediaLibraryUpdateReq;
import com.klaxon.kserver.module.media.model.rsp.MediaLibraryPageRsp;
import com.klaxon.kserver.module.media.service.MediaLibraryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "媒体库")
@RestController
@RequestMapping("/media-library")
public class MediaLibraryController {

    @Resource
    private MediaLibraryService mediaLibraryService;

    @PostMapping("/add")
    @ApiOperation(value = "添加媒体库")
    public Response<Void> add(@RequestBody MediaLibraryAddReq req) {
        mediaLibraryService.add(req);
        return Response.success();
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除媒体库")
    public Response<Void> delete(@RequestBody MediaLibraryDeleteReq req) {
        mediaLibraryService.delete(req);
        return Response.success();
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新媒体库")
    public Response<Void> update(@RequestBody MediaLibraryUpdateReq req) {
        mediaLibraryService.update(req);
        return Response.success();
    }

    @PostMapping("/list")
    @ApiOperation(value = "查询媒体库")
    public Response<PageInfo<MediaLibraryPageRsp>> list(@RequestBody MediaLibraryListReq req) {
        return Response.success(mediaLibraryService.list(req));
    }

}
