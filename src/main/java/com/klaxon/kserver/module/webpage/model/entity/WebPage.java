package com.klaxon.kserver.module.webpage.model.entity;

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
 * 网页表
 * </p>
 *
 * @author klaxonz
 * @since 2024-03-23
 */
@Getter
@Setter
@TableName("web_page")
public class WebPage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 链接
     */
    @TableField("url")
    private String url;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 网页内容
     */
    @TableField("content")
    private String content;

    /**
     * 来源
     */
    @TableField("source")
    private String source;

    /**
     * 网站icon
     */
    @TableField("favicon")
    private String favicon;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

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
