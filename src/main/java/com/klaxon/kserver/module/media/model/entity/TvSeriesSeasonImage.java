package com.klaxon.kserver.module.media.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 电视剧季背景图片表
 * </p>
 *
 * @author klaxonz
 * @since 2024-04-25
 */
@Getter
@Setter
@TableName("tv_series_season_image")
public class TvSeriesSeasonImage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 电视剧 id
     */
    @TableField("tv_series_id")
    private Long tvSeriesId;

    /**
     * 电视剧季度 id
     */
    @TableField("tv_series_season_id")
    private Long tvSeriesSeasonId;

    /**
     * 图片类型: 0: backdrop, 1: poster, 2: still
     */
    @TableField("image_type")
    private Integer imageType;

    /**
     * 图片 id
     */
    @TableField("image_id")
    private Long imageId;

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
