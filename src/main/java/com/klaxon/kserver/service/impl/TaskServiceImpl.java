package com.klaxon.kserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.klaxon.kserver.entity.dao.Task;
import com.klaxon.kserver.entity.vo.TaskVo;
import com.klaxon.kserver.mapperstruct.TaskMapperStruct;
import com.klaxon.kserver.service.TaskService;
import com.klaxon.kserver.mapper.TaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author klaxon
* @description 针对表【t_task(网页视频存储任务表)】的数据库操作Service实现
* @createDate 2023-02-18 23:10:39
*/
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
    implements TaskService{

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskMapperStruct taskMapperStruct;

    @Override
    public List<TaskVo> queryTaskList() {

        LambdaQueryWrapper<Task> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(Task::getCreateTime);
        List<Task> tasks = taskMapper.selectList(lambdaQueryWrapper);
        List<TaskVo> taskVos = taskMapperStruct.entitiesToVos(tasks);
        return taskVos;
    }
}




