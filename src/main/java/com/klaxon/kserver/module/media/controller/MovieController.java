package com.klaxon.kserver.module.media.controller;

import com.klaxon.kserver.bean.PageInfo;
import com.klaxon.kserver.bean.Response;
import com.klaxon.kserver.module.media.model.req.MovieDeleteReq;
import com.klaxon.kserver.module.media.model.req.MovieDetailReq;
import com.klaxon.kserver.module.media.model.req.MovieListReq;
import com.klaxon.kserver.module.media.model.rsp.MovieDetailRsp;
import com.klaxon.kserver.module.media.model.rsp.MovieListRsp;
import com.klaxon.kserver.module.media.service.MovieService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;


@Api(tags = "媒体库")
@RestController
@RequestMapping("/api/movie")
public class MovieController {

    @Resource
    private MovieService movieService;

    @GetMapping("/list")
    @ApiOperation(value = "电影列表")
    public Response<PageInfo<MovieListRsp>> list(MovieListReq req) {
        return Response.success(movieService.list(req));
    }

    @GetMapping("/detail")
    @ApiOperation(value = "电影详情")
    public Response<MovieDetailRsp> detail(MovieDetailReq req) {
        return Response.success(movieService.detail(req));
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除电影")
    public Response<MovieDetailRsp> delete(@Valid @RequestBody MovieDeleteReq req) {
        movieService.delete(req);
        return Response.success();
    }

}
