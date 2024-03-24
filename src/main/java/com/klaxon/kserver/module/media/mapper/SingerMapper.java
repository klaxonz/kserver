package com.klaxon.kserver.module.media.mapper;

import com.klaxon.kserver.module.media.model.entity.Singer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 歌手表 Mapper 接口
 * </p>
 *
 * @author klaxonz
 * @since 2024-03-23
 */
@Mapper
public interface SingerMapper extends BaseMapper<Singer> {

}
