package com.klaxon.kserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.klaxon.kserver.entity.dao.WebPage;
import com.klaxon.kserver.entity.dto.WebPageDto;
import com.klaxon.kserver.entity.vo.WebPageDetail;
import com.klaxon.kserver.entity.vo.WebPageTagVo;
import com.klaxon.kserver.entity.vo.WebPageVo;

import java.util.List;

public interface IWebPageService {

    WebPageVo add(WebPageDto webPageDto);

    WebPage getOne(Long id);

    WebPageDetail detail();

    IPage<WebPage> list(WebPageDto webPageDto, String question);

    void remove(Long id);

    void batchRemove(List<Long> webpageIds);

    void addTags(WebPageTagVo webPageTagVO);

    void removeTags(WebPageTagVo webPageTagVO);

    void changeGroup(Long webpageId, Long groupId);

}
