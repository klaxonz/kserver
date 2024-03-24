package com.klaxon.kserver.module.media.extractor;


import com.klaxon.kserver.module.media.model.entity.MediaLibrary;

public abstract class MediaMetaExtractor {

    public abstract Integer getType();

    public abstract void extract(MediaLibrary mediaLibrary, FileExtractInfo extractInfo) throws Exception;

}
