package com.klaxon.kserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.klaxon.kserver.entity.dao.WebPage;
import com.klaxon.kserver.entity.dto.WebPageDTO;
import com.klaxon.kserver.entity.vo.WebPageDetail;
import com.klaxon.kserver.entity.vo.WebPageTagVO;

import java.util.List;

public interface IWebPageService {

    void add(WebPageDTO webPageResourceDTO);

    WebPage getOne(Long id);

    WebPageDetail detail();

    IPage<WebPage> getAll(Integer page);

    IPage<WebPage> getStar(Integer page);

    IPage<WebPage> getToday(Integer page);

    void remove(Long id);

    void batchRemove(List<Long> webpageIds);

    void addTags(WebPageTagVO webPageTagVO);

    void removeTags(WebPageTagVO webPageTagVO);

    void changeGroup(Long webpageId, Long groupId);

    IPage<WebPage> search(String type, String question, Integer page);
}
