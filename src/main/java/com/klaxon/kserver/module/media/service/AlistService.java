package com.klaxon.kserver.module.media.service;

import com.klaxon.kserver.module.media.model.entity.MediaLibrary;

import java.util.List;

public interface AlistService {

    void getFileList(MediaLibrary mediaLibrary, String parentPath, List<String> filePaths);

}
