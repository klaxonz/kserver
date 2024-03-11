package com.klaxon.kserver.module.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klaxon.kserver.bean.PageInfo;
import com.klaxon.kserver.exception.BizCodeEnum;
import com.klaxon.kserver.exception.BizException;
import com.klaxon.kserver.module.media.mapper.MediaLibraryMapper;
import com.klaxon.kserver.module.media.model.entity.MediaLibrary;
import com.klaxon.kserver.module.media.model.req.MediaLibraryAddReq;
import com.klaxon.kserver.module.media.model.req.MediaLibraryDeleteReq;
import com.klaxon.kserver.module.media.model.req.MediaLibraryListReq;
import com.klaxon.kserver.module.media.model.req.MediaLibraryUpdateReq;
import com.klaxon.kserver.module.media.model.rsp.MediaLibraryPageRsp;
import com.klaxon.kserver.module.media.service.MediaLibraryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class MediaLibraryServiceImpl implements MediaLibraryService {

    private final MediaLibraryMapper mediaLibraryMapper;

    @Override
    public void add(MediaLibraryAddReq req) {
        checkMediaLibraryExistOrFail(req.getName());

        MediaLibrary mediaLibrary = new MediaLibrary();
        mediaLibrary.setName(req.getName());
        mediaLibrary.setUrl(req.getUrl());
        mediaLibrary.setUsername(req.getUsername());
        mediaLibrary.setPassword(req.getPassword());

        mediaLibraryMapper.insert(mediaLibrary);
    }

    public void checkMediaLibraryExistOrFail(String name) {
        LambdaQueryWrapper<MediaLibrary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MediaLibrary::getName, name);
        MediaLibrary mediaLibrary = mediaLibraryMapper.selectOne(queryWrapper);
        if (Objects.nonNull(mediaLibrary)) {
            throw new BizException(BizCodeEnum.RESOURCE_EXIST, "媒体库名称已存在");
        }
    }

    @Override
    public void delete(MediaLibraryDeleteReq req) {
        mediaLibraryMapper.deleteById(req.getLibraryId());
    }

    @Override
    public void update(MediaLibraryUpdateReq req) {
        MediaLibrary mediaLibrary = new MediaLibrary();
        mediaLibrary.setId(req.getLibraryId());
        mediaLibrary.setName(req.getName());
        mediaLibrary.setUrl(req.getUrl());
        mediaLibrary.setUsername(req.getUsername());
        mediaLibrary.setPassword(req.getPassword());
        mediaLibraryMapper.updateById(mediaLibrary);
    }

    @Override
    public PageInfo<MediaLibraryPageRsp> list(MediaLibraryListReq req) {
        LambdaQueryWrapper<MediaLibrary> queryWrapper = new LambdaQueryWrapper<>();
        Page<MediaLibrary> page = new Page<>(req.getPage(), req.getPageSize());
        Page<MediaLibrary> mediaLibraryPage = mediaLibraryMapper.selectPage(page, queryWrapper);

        List<MediaLibrary> records = mediaLibraryPage.getRecords();
        List<MediaLibraryPageRsp> mediaLibraryList = records.stream().map(mediaLibrary -> {
            MediaLibraryPageRsp mediaLibraryPageRsp = new MediaLibraryPageRsp();
            mediaLibraryPageRsp.setLibraryId(mediaLibrary.getId());
            mediaLibraryPageRsp.setName(mediaLibrary.getName());
            mediaLibraryPageRsp.setHost(mediaLibrary.getUrl());
            mediaLibraryPageRsp.setUsername(mediaLibrary.getUsername());
            return mediaLibraryPageRsp;
        }).collect(java.util.stream.Collectors.toList());

        return new PageInfo<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getPages(), mediaLibraryList);
    }
}
