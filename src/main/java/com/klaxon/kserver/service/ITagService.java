package com.klaxon.kserver.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klaxon.kserver.entity.dao.Tag;

public interface ITagService {

    void add(String tagName);

    Tag getOne(Long id);

    Page<Tag> page(Integer page);

    void remove(Long id);

}
