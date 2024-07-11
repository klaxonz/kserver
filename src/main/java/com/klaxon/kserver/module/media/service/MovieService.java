package com.klaxon.kserver.module.media.service;

import com.klaxon.kserver.bean.PageInfo;
import com.klaxon.kserver.module.media.model.req.MovieDeleteReq;
import com.klaxon.kserver.module.media.model.req.MovieDetailReq;
import com.klaxon.kserver.module.media.model.req.MovieListReq;
import com.klaxon.kserver.module.media.model.rsp.MovieDetailRsp;
import com.klaxon.kserver.module.media.model.rsp.MovieListRsp;


public interface MovieService {

    PageInfo<MovieListRsp> list(MovieListReq req);

    MovieDetailRsp detail(MovieDetailReq req);

    void delete(MovieDeleteReq req);

    void scrape(String name, Integer release);

}
