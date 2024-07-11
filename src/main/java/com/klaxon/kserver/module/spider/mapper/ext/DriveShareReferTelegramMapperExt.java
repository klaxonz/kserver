package com.klaxon.kserver.module.spider.mapper.ext;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klaxon.kserver.module.media.model.entity.Movie;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;


@Mapper
public interface DriveShareReferTelegramMapperExt {

    @Select("select max(message_date) from drive_share_refer_telegram where chat_id = #{chatId}")
    Long selectMaxMessageDateByChatId(@Param("chatId") Long chatId);


}
