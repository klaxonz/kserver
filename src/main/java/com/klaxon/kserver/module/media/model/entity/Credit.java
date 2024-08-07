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
 * 职员表
 * </p>
 *
 * @author klaxonz
 * @since 2024-04-25
 */
@Getter
@Setter
@TableName("credit")
public class Credit implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * tmdb的credit_id
     */
    @TableField("credit_id")
    private String creditId;

    /**
     * tmdb的person_id
     */
    @TableField("person_id")
    private Integer personId;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    @TableField("department")
    private String department;

    @TableField("job")
    private String job;

    /**
     * 头像
     */
    @TableField("profile_path")
    private String profilePath;

    /**
     * 是否成人，0：否，1：是
     */
    @TableField("is_adult")
    private Boolean isAdult;

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
