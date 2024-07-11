package com.klaxon.kserver.module.spider;

import com.klaxon.kserver.module.spider.mapper.ArticleMapper;
import com.klaxon.kserver.module.spider.model.entity.Article;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.PageModelPipeline;

import javax.annotation.Resource;

@Component
public class OschinaBlogPageModelPipeline implements PageModelPipeline<OschinaBlog> {

    @Resource
    private ArticleMapper articleMapper;

    @Override
    public void process(OschinaBlog oschinaBlog, Task task) {
        Article article = new Article();
        article.setTitle(oschinaBlog.getTitle());
        article.setUrl(oschinaBlog.getUrl());
        try {
            articleMapper.insert(article);
        } catch (DuplicateKeyException ignored){
        }
    }
}
