package com.klaxon.kserver.module.spider.model.entity;

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
 * 阿里云盘分享资源详情
 * </p>
 *
 * @author klaxonz
 * @since 2024-04-25
 */
@Getter
@Setter
@TableName("drive_share_adrive_detail")
public class DriveShareAdriveDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * drive_share.id
     */
    @TableField("share_id")
    private Long shareId;

    /**
     * 链接
     */
    @TableField("share_url")
    private String shareUrl;

    /**
     * 文件名称
     */
    @TableField("name")
    private String name;

    /**
     * 1: 文件，2：目录
     */
    @TableField("file_type")
    private Integer fileType;

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
