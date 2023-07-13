package com.klaxon.kserver.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.klaxon.kserver.bean.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName(value = "t_video")
@EqualsAndHashCode(callSuper = true)
public class Video extends PageParam implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String title;
    private String videoPath;
    private Integer videoIndex;
    private Integer isMulti;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
