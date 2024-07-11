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
 * 电影分享表
 * </p>
 *
 * @author klaxonz
 * @since 2024-04-25
 */
@Getter
@Setter
@TableName("movie_share")
public class MovieShare implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 电影 id
     */
    @TableField("movie_id")
    private Long movieId;

    /**
     * 分享链接
     */
    @TableField("share_url")
    private String shareUrl;

    /**
     * 来源链接
     */
    @TableField("referer_url")
    private String refererUrl;

    /**
     * 帧率
     */
    @TableField("frame_rate")
    private Integer frameRate;

    /**
     * 比特率
     */
    @TableField("bit_rate")
    private Integer bitRate;

    /**
     * 分辨率
     */
    @TableField("resolution")
    private String resolution;

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
