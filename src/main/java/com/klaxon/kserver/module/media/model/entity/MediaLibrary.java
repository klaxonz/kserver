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
 * 媒体库表
 * </p>
 *
 * @author klaxonz
 * @since 2024-04-25
 */
@Getter
@Setter
@TableName("media_library")
public class MediaLibrary implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 媒体库地址
     */
    @TableField("url")
    private String url;

    /**
     * 媒体库名称
     */
    @TableField("name")
    private String name;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 删除时间
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
