package com.klaxon.kserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klaxon.kserver.entity.dao.Tag;
import com.klaxon.kserver.mapper.TagMapper;
import com.klaxon.kserver.service.ITagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("tagService")
public class TagServiceImpl implements ITagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public void add(String tagName) {
        Tag tag = tagMapper.selectOne(new LambdaQueryWrapper<Tag>().eq(Tag::getTagName, tagName));
        if (tag == null) {
            Tag newTag = new Tag();
            newTag.setTagName(tagName);
            tagMapper.insert(newTag);
        }
    }

    @Override
    public Tag getOne(Long id) {
        return tagMapper.selectById(id);
    }

    @Override
    public Page<Tag> page(Integer page) {
        Page<Tag> pageCondition = new Page<>(page, page + 1);
        return tagMapper.selectPage(pageCondition, new QueryWrapper<>());
    }

    @Override
    public void remove(Long id) {
        tagMapper.deleteById(id);
    }
}
