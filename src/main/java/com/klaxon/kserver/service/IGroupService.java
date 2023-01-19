package com.klaxon.kserver.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klaxon.kserver.entity.dao.Group;

public interface IGroupService {

    void add(String groupName);

    Group getOne(Long id);

    Page<Group> page(Integer page);

    void remove(Long id);

}
