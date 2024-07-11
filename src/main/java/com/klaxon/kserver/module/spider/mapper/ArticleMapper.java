package com.klaxon.kserver.module.spider.mapper;

import com.klaxon.kserver.module.spider.model.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 文章表 Mapper 接口
 * </p>
 *
 * @author klaxonz
 * @since 2024-04-25
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}
