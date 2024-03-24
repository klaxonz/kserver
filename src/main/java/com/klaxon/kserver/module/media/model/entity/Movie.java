package com.klaxon.kserver.module.media.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 电影表
 * </p>
 *
 * @author klaxonz
 * @since 2024-03-23
 */
@Getter
@Setter
@TableName("movie")
public class Movie implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 媒体库 id
     */
    @TableField("library_id")
    private Long libraryId;

    /**
     * 路径
     */
    @TableField("path")
    private String path;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 原始标题
     */
    @TableField("origin_title")
    private String originTitle;

    /**
     * 原始语言
     */
    @TableField("origin_language")
    private String originLanguage;

    /**
     * 背景图路径
     */
    @TableField("backdrop_path")
    private String backdropPath;

    /**
     * 海报路径
     */
    @TableField("poster_path")
    private String posterPath;

    /**
     * 简要介绍
     */
    @TableField("overview")
    private String overview;

    /**
     * 是否限制级
     */
    @TableField("if_adult")
    private Boolean ifAdult;

    /**
     * 发行日期
     */
    @TableField("release_date")
    private LocalDate releaseDate;

    /**
     * 删除时间，0 为未删除
     */
    @TableField("deleted")
    @TableLogic
    private Long deleted;

    /**
     * 版本号
     */
    @TableField("object_version")
    private Long objectVersion;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
