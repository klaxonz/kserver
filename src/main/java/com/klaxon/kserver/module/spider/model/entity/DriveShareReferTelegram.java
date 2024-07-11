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
 * telegram云盘资源分项表
 * </p>
 *
 * @author klaxonz
 * @since 2024-04-25
 */
@Getter
@Setter
@TableName("drive_share_refer_telegram")
public class DriveShareReferTelegram implements Serializable {

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
     * 1: 阿里云盘
     */
    @TableField("drive_type")
    private Integer driveType;

    /**
     * chat id
     */
    @TableField("chat_id")
    private Long chatId;

    /**
     * message id
     */
    @TableField("message_id")
    private Long messageId;

    /**
     * message date
     */
    @TableField("message_date")
    private Integer messageDate;

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
