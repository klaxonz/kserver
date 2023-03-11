package com.klaxon.kserver.controller;

import com.klaxon.kserver.pojo.Response;
import com.klaxon.kserver.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/queryTaskList")
    public <R> Response<R> addTag() {

        return Response.success();
    }





}
