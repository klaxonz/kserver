package com.klaxon.kserver.module.media.service.impl;

import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.klaxon.kserver.bean.PageInfo;
import com.klaxon.kserver.constants.RedisKeyPrefixConstants;
import com.klaxon.kserver.exception.BizCodeEnum;
import com.klaxon.kserver.exception.BizException;
import com.klaxon.kserver.module.media.manager.MoviePathManager;
import com.klaxon.kserver.module.media.mapper.MediaLibraryDirectoryMapper;
import com.klaxon.kserver.module.media.mapper.MediaLibraryMapper;
import com.klaxon.kserver.module.media.mapper.MoviePathMapper;
import com.klaxon.kserver.module.media.model.entity.MediaLibrary;
import com.klaxon.kserver.module.media.model.entity.MediaLibraryDirectory;
import com.klaxon.kserver.module.media.model.entity.MoviePath;
import com.klaxon.kserver.module.media.model.req.MediaLibraryAddReq;
import com.klaxon.kserver.module.media.model.req.MediaLibraryDeleteReq;
import com.klaxon.kserver.module.media.model.req.MediaLibraryListReq;
import com.klaxon.kserver.module.media.model.req.MediaLibraryMountReq;
import com.klaxon.kserver.module.media.model.req.MediaLibrarySyncReq;
import com.klaxon.kserver.module.media.model.req.MediaLibraryUpdateReq;
import com.klaxon.kserver.module.media.model.rsp.MediaLibraryDetailRsp;
import com.klaxon.kserver.module.media.model.rsp.MediaLibraryPageRsp;
import com.klaxon.kserver.module.media.service.AlistService;
import com.klaxon.kserver.module.media.service.MediaLibraryService;
import com.klaxon.kserver.module.media.task.MediaMetaAsyncTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MediaLibraryServiceImpl implements MediaLibraryService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private MediaLibraryMapper mediaLibraryMapper;
    @Resource
    private MediaLibraryDirectoryMapper mediaLibraryDirectoryMapper;
    @Resource
    private MediaMetaAsyncTask mediaMetaAsyncTask;
    @Resource
    private AlistService alistService;
    @Resource
    private MoviePathManager moviePathManager;
    @Resource
    private MoviePathMapper moviePathMapper;

    @Override
    @Transactional
    public void add(MediaLibraryAddReq req) {
        findMediaLibraryNameExistOrFail(req.getName());

        MediaLibrary mediaLibrary = new MediaLibrary();
        mediaLibrary.setName(req.getName());
        mediaLibrary.setUrl(req.getUrl());
        mediaLibrary.setUsername(req.getUsername());
        mediaLibrary.setPassword(req.getPassword());
        mediaLibraryMapper.insert(mediaLibrary);

        Set<String> pathsSet = new HashSet<>(req.getPaths());
        for (String path : pathsSet) {
            MediaLibraryDirectory mediaLibraryDirectory = new MediaLibraryDirectory();
            mediaLibraryDirectory.setLibraryId(mediaLibrary.getId());
            mediaLibraryDirectory.setPath(path);
            mediaLibraryDirectoryMapper.insert(mediaLibraryDirectory);
        }
    }

    public MediaLibrary findMediaLibraryExistOrFail(Long id) {
        LambdaQueryWrapper<MediaLibrary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MediaLibrary::getId, id);
        MediaLibrary mediaLibrary = mediaLibraryMapper.selectOne(queryWrapper);
        if (Objects.isNull(mediaLibrary)) {
            throw new BizException(BizCodeEnum.RESOURCE_NOT_EXIST, "媒体库不存在");
        }
        return mediaLibrary;
    }

    public MediaLibrary findMediaLibraryNameExistOrFail(String name) {
        LambdaQueryWrapper<MediaLibrary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MediaLibrary::getName, name);
        MediaLibrary mediaLibrary = mediaLibraryMapper.selectOne(queryWrapper);
        if (Objects.nonNull(mediaLibrary)) {
            throw new BizException(BizCodeEnum.RESOURCE_EXIST, "媒体库名称重复");
        }
        return mediaLibrary;
    }

    @Override
    @Transactional
    public void delete(MediaLibraryDeleteReq req) {
        findMediaLibraryExistOrFail(req.getId());
        mediaLibraryMapper.deleteById(req.getId());
        mediaLibraryDirectoryMapper.delete(new LambdaQueryWrapper<MediaLibraryDirectory>()
                .eq(MediaLibraryDirectory::getLibraryId, req.getId()));
    }

    @Override
    @Transactional
    public void update(MediaLibraryUpdateReq req) {
        validateUpdateMedaLibrary(req);

        // 更新媒体库基本信息
        MediaLibrary updateMediaLibrary = new MediaLibrary();
        updateMediaLibrary.setId(req.getId());
        updateMediaLibrary.setName(req.getName());
        updateMediaLibrary.setUrl(req.getUrl());
        updateMediaLibrary.setUsername(req.getUsername());
        updateMediaLibrary.setPassword(req.getPassword());
        mediaLibraryMapper.updateById(updateMediaLibrary);

        // 获取新的路径列表
        List<String> newPaths = req.getPaths();

        // 查询当前媒体库下已有的所有路径
        List<MediaLibraryDirectory> existingDirectories = mediaLibraryDirectoryMapper.selectList(
                new LambdaQueryWrapper<MediaLibraryDirectory>()
                    .eq(MediaLibraryDirectory::getLibraryId, req.getId()));

        // 创建一个HashSet存储新路径，方便检查和删除旧路径
        Set<String> updatedPathsSet = new HashSet<>(newPaths);

        // 遍历现有目录，如果不在新路径列表中则删除
        for (MediaLibraryDirectory directory : existingDirectories) {
            if (!updatedPathsSet.contains(directory.getPath())) {
                mediaLibraryDirectoryMapper.deleteById(directory.getId());
            } else {
                // 如果路径还在，从Set中移除，避免后续新增
                updatedPathsSet.remove(directory.getPath());
            }
        }

        // 遍历剩余的新路径，添加到数据库中
        for (String newPath : updatedPathsSet) {
            MediaLibraryDirectory mediaLibraryDirectory = new MediaLibraryDirectory();
            mediaLibraryDirectory.setLibraryId(req.getId());
            mediaLibraryDirectory.setPath(newPath);
            mediaLibraryDirectoryMapper.insert(mediaLibraryDirectory);
        }
    }

    public void validateUpdateMedaLibrary(MediaLibraryUpdateReq req) {
        MediaLibrary mediaLibrary = findMediaLibraryExistOrFail(req.getId());
        if (!StringUtils.equals(mediaLibrary.getName(), req.getName())) {
            findMediaLibraryNameExistOrFail(req.getName());
        }
    }

    @Override
    public MediaLibraryDetailRsp detail(Long id) {
        MediaLibrary mediaLibrary = findMediaLibraryExistOrFail(id);

        List<MediaLibraryDirectory> directoryList = mediaLibraryDirectoryMapper.selectList(new LambdaQueryWrapper<MediaLibraryDirectory>()
                .eq(MediaLibraryDirectory::getLibraryId, id));
        List<String> paths = directoryList.stream().map(MediaLibraryDirectory::getPath).collect(Collectors.toList());

        MediaLibraryDetailRsp detail = new MediaLibraryDetailRsp();
        detail.setId(mediaLibrary.getId());
        detail.setName(mediaLibrary.getName());
        detail.setUrl(mediaLibrary.getUrl());
        detail.setUsername(mediaLibrary.getUsername());
        detail.setPassword(mediaLibrary.getPassword());
        detail.setPaths(paths);

        return detail;
    }

    @Override
    public PageInfo<MediaLibraryPageRsp> list(MediaLibraryListReq req) {
        LambdaQueryWrapper<MediaLibrary> queryWrapper = new LambdaQueryWrapper<>();
        Page<MediaLibrary> page = new Page<>(req.getPage(), req.getPageSize());
        Page<MediaLibrary> mediaLibraryPage = mediaLibraryMapper.selectPage(page, queryWrapper);

        List<MediaLibrary> records = mediaLibraryPage.getRecords();
        List<MediaLibraryPageRsp> mediaLibraryList = records.stream().map(mediaLibrary -> {
            MediaLibraryPageRsp mediaLibraryPageRsp = new MediaLibraryPageRsp();
            mediaLibraryPageRsp.setId(mediaLibrary.getId());
            mediaLibraryPageRsp.setName(mediaLibrary.getName());
            mediaLibraryPageRsp.setUrl(mediaLibrary.getUrl());
            mediaLibraryPageRsp.setUsername(mediaLibrary.getUsername());
            mediaLibraryPageRsp.setPassword(mediaLibrary.getPassword());
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

            // 查询所有的电影文件
            List<MoviePath> moviePaths = moviePathMapper.selectList(new LambdaQueryWrapper<MoviePath>()
                    .eq(MoviePath::getLibraryId, mediaLibrary.getId()));
            for (MoviePath moviePath : moviePaths) {
                if (filePaths.contains(moviePath.getPath())) {
                    continue;
                }
                moviePathManager.deleteMoviePath(moviePath);
            }

            for (String filePath : filePaths) {
                String filePathMD5 = MD5.create().digestHex(filePath, StandardCharsets.UTF_8);
                String key = RedisKeyPrefixConstants.MOVIE_PREFIX + mediaLibrary.getId() + ":" + filePathMD5;
                Object value = redisTemplate.opsForValue().get(key);
                if (Objects.nonNull(value)) {
                    continue;
                }
                mediaMetaAsyncTask.doTask(filePath, mediaLibrary);
            }
        }

        log.info("Finnish to sync media library");
    }

}
