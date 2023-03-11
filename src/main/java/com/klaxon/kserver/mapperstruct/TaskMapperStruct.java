package com.klaxon.kserver.mapperstruct;

import com.klaxon.kserver.entity.dao.Task;
import com.klaxon.kserver.entity.vo.TaskVo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapperStruct {

    List<TaskVo> entitiesToVos(List<Task> tasks);

}
