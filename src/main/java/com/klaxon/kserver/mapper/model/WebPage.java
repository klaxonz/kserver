package com.klaxon.kserver.mapper.model;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.klaxon.kserver.bean.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_web_page")
public class WebPage extends PageParam {

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;
	private Long userId;
	private String url;
	private String title;
	private String source;
	private String isStar;
	private String favicon;
	private String description;
	private LocalDateTime createTime;
	private LocalDateTime updateTime;

}
