package com.klaxon.kserver.mapper;

import com.klaxon.kserver.mapper.model.WebPageVideoTask;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Repository
public interface WebPageVideoTaskMapper extends BaseMapper<WebPageVideoTask> {

    Long queryVideoDownloadedSize(Long taskId);

}
