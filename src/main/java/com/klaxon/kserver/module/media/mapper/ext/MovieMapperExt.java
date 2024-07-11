package com.klaxon.kserver.module.media.mapper.ext;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klaxon.kserver.module.media.model.entity.Movie;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;


@Mapper
public interface MovieMapperExt {

    @SelectProvider(type = MovieSqlProvider.class, method = "selectMovie")
    Page<Movie> selectMovie(Page<Movie> iPage);

    class MovieSqlProvider {
        public String selectMovie() {
            return new SQL(){{
                SELECT("m.id,m.title, m.origin_title, m.origin_language, m.backdrop_path, " +
                       "m.poster_path, m.overview, m.if_adult, m.release_date, m.deleted, m.object_version, m.create_time, " +
                       "m.update_time");
                FROM("media_library ml");
                LEFT_OUTER_JOIN("movie m ON m.library_id = ml.id");
                WHERE("ml.deleted = 0 AND m.deleted = 0 AND m.poster_path IS NOT NULL");
            }}.toString();
        }

    }
}
