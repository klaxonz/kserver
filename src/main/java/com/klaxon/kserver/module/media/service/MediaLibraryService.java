package com.klaxon.kserver.module.media.service;

import com.klaxon.kserver.bean.PageInfo;
import com.klaxon.kserver.module.media.model.req.MediaLibraryAddReq;
import com.klaxon.kserver.module.media.model.req.MediaLibraryDeleteReq;
import com.klaxon.kserver.module.media.model.req.MediaLibraryListReq;
import com.klaxon.kserver.module.media.model.req.MediaLibraryUpdateReq;
import com.klaxon.kserver.module.media.model.rsp.MediaLibraryPageRsp;

public interface MediaLibraryService {

    void add(MediaLibraryAddReq req);

    void delete(MediaLibraryDeleteReq req);

    void update(MediaLibraryUpdateReq req);

    PageInfo<MediaLibraryPageRsp> list(MediaLibraryListReq req);

}
