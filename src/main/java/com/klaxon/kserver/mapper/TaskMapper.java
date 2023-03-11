package com.klaxon.kserver.mapper;

import com.klaxon.kserver.entity.dao.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
* @author MSN
* @description 针对表【t_task(网页视频存储任务表)】的数据库操作Mapper
* @createDate 2023-02-18 23:10:39
* @Entity com.klaxon.kserver.entity.dao.Task
*/
@Repository
public interface TaskMapper extends BaseMapper<Task> {

}




