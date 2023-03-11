package com.klaxon.kserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.klaxon.kserver.entity.dao.Task;
import com.klaxon.kserver.entity.vo.TaskVo;

import java.util.List;

/**
* @author MSN
* @description 针对表【t_task(网页视频存储任务表)】的数据库操作Service
* @createDate 2023-02-18 23:10:39
*/
public interface TaskService extends IService<Task> {


    List<TaskVo> queryTaskList();


}
