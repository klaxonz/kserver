package com.klaxon.kserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klaxon.kserver.entity.dao.Group;
import com.klaxon.kserver.mapper.GroupMapper;
import com.klaxon.kserver.service.IGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("groupService")
public class GroupServiceImpl implements IGroupService {

    @Autowired
    private GroupMapper groupMapper;

    @Override
    public void add(String groupName) {
        Group group = groupMapper.selectOne(new LambdaQueryWrapper<Group>().eq(Group::getGroupName, groupName));
        if (group == null) {
            Group newGroup = new Group();
            newGroup.setGroupName(groupName);
            groupMapper.insert(newGroup);
        }
    }

    @Override
    public Group getOne(Long id) {
        return groupMapper.selectById(id);
    }

    @Override
    public Page<Group> page(Integer page) {
        Page<Group> pageCondition = new Page<>(page, page + 1);
        return groupMapper.selectPage(pageCondition, new QueryWrapper<>());
    }

    @Override
    public void remove(Long id) {
        groupMapper.deleteById(id);
    }
}
