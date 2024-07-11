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
 * 电视剧季度表
 * </p>
 *
 * @author klaxonz
 * @since 2024-04-25
 */
@Getter
@Setter
@TableName("tv_series_season")
public class TvSeriesSeason implements Serializable {

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
     * 电视剧 id
     */
    @TableField("tv_series_id")
    private Long tvSeriesId;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 季数
     */
    @TableField("season")
    private Integer season;

    /**
     * 总集数
     */
    @TableField("episode_count")
    private Integer episodeCount;

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
