package com.klaxon.kserver.module.media.mapper;

import com.klaxon.kserver.module.media.model.entity.Movie;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 电影表 Mapper 接口
 * </p>
 *
 * @author klaxonz
 * @since 2024-04-25
 */
@Mapper
public interface MovieMapper extends BaseMapper<Movie> {

}
