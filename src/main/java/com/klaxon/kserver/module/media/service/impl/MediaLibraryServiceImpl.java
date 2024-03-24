package com.klaxon.kserver.module.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.klaxon.kserver.bean.PageInfo;
import com.klaxon.kserver.exception.BizCodeEnum;
import com.klaxon.kserver.exception.BizException;
import com.klaxon.kserver.module.media.mapper.MediaLibraryDirectoryMapper;
import com.klaxon.kserver.module.media.mapper.MediaLibraryMapper;
import com.klaxon.kserver.module.media.model.entity.MediaLibrary;
import com.klaxon.kserver.module.media.model.entity.MediaLibraryDirectory;
import com.klaxon.kserver.module.media.model.req.MediaLibraryAddReq;
import com.klaxon.kserver.module.media.model.req.MediaLibraryDeleteReq;
import com.klaxon.kserver.module.media.model.req.MediaLibraryListReq;
import com.klaxon.kserver.module.media.model.req.MediaLibraryMountReq;
import com.klaxon.kserver.module.media.model.req.MediaLibrarySyncReq;
import com.klaxon.kserver.module.media.model.req.MediaLibraryUpdateReq;
import com.klaxon.kserver.module.media.model.rsp.MediaLibraryPageRsp;
import com.klaxon.kserver.module.media.service.AlistService;
import com.klaxon.kserver.module.media.service.MediaLibraryService;
import com.klaxon.kserver.module.media.task.MediaMetaAsyncTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MediaLibraryServiceImpl implements MediaLibraryService {

    @Resource
    private MediaLibraryMapper mediaLibraryMapper;
    @Resource
    private MediaLibraryDirectoryMapper mediaLibraryDirectoryMapper;
    @Resource
    private MediaMetaAsyncTask mediaMetaAsyncTask;
    @Resource
    private AlistService alistService;

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
            throw new BizException(BizCodeEnum.RESOURCE_EXIST, "媒体库名称重复");
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

    @Override
    public void mount(MediaLibraryMountReq req) {
        checkMediaLibraryByIdExistOrFail(req.getLibraryId());
        List<String> paths = req.getPaths();
        List<MediaLibraryDirectory> directories = mediaLibraryDirectoryMapper.selectList(
                new LambdaQueryWrapper<MediaLibraryDirectory>()
                        .eq(MediaLibraryDirectory::getLibraryId, req.getLibraryId()));
        Set<String> libraryPaths = directories.stream()
                .map(MediaLibraryDirectory::getPath).collect(Collectors.toSet());
        paths.forEach(path -> {
            if (!libraryPaths.contains(path)) {
                MediaLibraryDirectory mediaLibraryDirectory = new MediaLibraryDirectory();
                mediaLibraryDirectory.setLibraryId(req.getLibraryId());
                mediaLibraryDirectory.setPath(path);
                mediaLibraryDirectoryMapper.insert(mediaLibraryDirectory);
            }
        });
    }

    private void checkMediaLibraryByIdExistOrFail(Long libraryId) {
        MediaLibrary mediaLibrary = mediaLibraryMapper.selectById(libraryId);
        if (Objects.isNull(mediaLibrary)) {
            throw new BizException(BizCodeEnum.RESOURCE_NOT_EXIST, "媒体库不存在");
        }
    }

    @Override
    public void sync(MediaLibrarySyncReq req) {
        log.info("Start to sync media library");

        List<Long> libraryIds = req.getLibraryIds();
        List<MediaLibrary> mediaLibraries = mediaLibraryMapper.selectList(
                new LambdaQueryWrapper<MediaLibrary>().in(MediaLibrary::getId, libraryIds));
        if (mediaLibraries.size() != libraryIds.size()) {
            throw new BizException(BizCodeEnum.RESOURCE_NOT_EXIST, "媒体库不存在");
        }

        for (MediaLibrary mediaLibrary : mediaLibraries) {
            // 查询挂载目录
            List<MediaLibraryDirectory> mediaLibraryDirectories = mediaLibraryDirectoryMapper.selectList(
                    new LambdaQueryWrapper<MediaLibraryDirectory>()
                            .eq(MediaLibraryDirectory::getLibraryId, mediaLibrary.getId()));

            List<String> filePaths = Lists.newArrayList();
            for (MediaLibraryDirectory mediaLibraryDirectory : mediaLibraryDirectories) {
                String path = mediaLibraryDirectory.getPath();
                alistService.getFileList(mediaLibrary, path, filePaths);
            }

            for (String filePath : filePaths) {
                mediaMetaAsyncTask.doTask(filePath, mediaLibrary);
            }
        }

        log.info("Finnish to sync media library");
    }

}
