package com.klaxon.kserver.generator;

import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class MybatisPlusGenerator {

    public static void main(String[] args) throws IOException {
        CodeGenerator.generate("account", "account");
        CodeGenerator.generate("webpage", "web_page");
        CodeGenerator.generate("media", "media_library");
        CodeGenerator.generate("media", "media_library_directory");
        CodeGenerator.generate("media", "movie");
        CodeGenerator.generate("media", "movie_actor");
        CodeGenerator.generate("media", "actor", (globalConfig, typeRegistry, metaInfo) ->
                StringUtils.equals(metaInfo.getColumnName(), "gender")? DbColumnType.INTEGER : typeRegistry.getColumnType(metaInfo));
        CodeGenerator.generate("media", "actor_image");
        CodeGenerator.generate("media", "image", (globalConfig, typeRegistry, metaInfo) ->
                StringUtils.equalsAny(metaInfo.getColumnName(), "type", "source")? DbColumnType.INTEGER : typeRegistry.getColumnType(metaInfo));
        CodeGenerator.generate("media", "director");
        CodeGenerator.generate("media", "movie_director");
        CodeGenerator.generate("media", "director_image");
        CodeGenerator.generate("media", "tv_series");
        CodeGenerator.generate("media", "tv_series_season");
        CodeGenerator.generate("media", "tv_series_season_image", (globalConfig, typeRegistry, metaInfo) ->
                StringUtils.equalsAny(metaInfo.getColumnName(), "image_type")? DbColumnType.INTEGER : typeRegistry.getColumnType(metaInfo));
        CodeGenerator.generate("media", "tv_series_episode");
        CodeGenerator.generate("media", "tv_series_episode_image", (globalConfig, typeRegistry, metaInfo) ->
                StringUtils.equalsAny(metaInfo.getColumnName(), "image_type")? DbColumnType.INTEGER : typeRegistry.getColumnType(metaInfo));
        CodeGenerator.generate("media", "tv_series_actor");
        CodeGenerator.generate("media", "tv_series_director");
        CodeGenerator.generate("media", "music");
        CodeGenerator.generate("media", "singer");
        CodeGenerator.generate("media", "music_singer");
        CodeGenerator.generate("media", "book");
        CodeGenerator.generate("media", "book_author");
        CodeGenerator.generate("media", "book_publisher");
    }

}
