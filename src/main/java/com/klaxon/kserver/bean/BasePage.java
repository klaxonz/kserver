package com.klaxon.kserver.bean;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.google.common.collect.Lists;

@JsonIncludeProperties(value = {"page", "size", "list", "total"})
public class BasePage<T> extends Page<T> {

    private static final long serialVersionUID = 6432902942609839917L;

    private long page;
    private long size;
    private List<T> list;

    public BasePage() {
    }

    public BasePage(Integer page, Integer size) {
        super(page, size);
        this.page = page;
        this.size = size;
        this.list = Lists.newArrayList();
    }


    @Override
    public Page<T> setRecords(List<T> records) {
        super.setRecords(records);
        list = Lists.newArrayList(records);
        return this;
    }

    @Override
    public Page<T> setCurrent(long current) {
        super.setCurrent(current);
        this.page = current;
        return this;
    }

    public long getPage() {
        return page;
    }

    public List<T> getList() {
        return list;
    }
}
