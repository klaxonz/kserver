package com.klaxon.kserver.module.media.extractor;

import cn.hutool.json.JSONUtil;
import com.klaxon.kserver.module.media.manager.MovieManager;
import com.klaxon.kserver.module.media.manager.MoviePathManager;
import com.klaxon.kserver.module.media.model.entity.MediaLibrary;
import com.klaxon.kserver.module.media.service.TmdbSearchService;
import com.uwetrottmann.tmdb2.entities.BaseMovie;
import com.uwetrottmann.tmdb2.entities.Movie;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class MovieMetaExtractor extends MediaMetaExtractor {

    @Resource
    private TmdbSearchService tmdbSearchService;
    @Resource
    private MovieManager movieManager;
    @Resource
    private MoviePathManager moviePathManager;

    @Override
    public Integer getType() {
        return FileExtractInfo.TYPE_MOVIE;
    }

    @Override
    public void extract(MediaLibrary mediaLibrary, FileExtractInfo extractInfo) throws Exception {

        BaseMovie baseMovie = null;
        for (int page = 1; page < 5; page++) {
            Response<MovieResultsPage> movieResultsPageResponse = tmdbSearchService.searchMovie(extractInfo.getName(), page);
            MovieResultsPage body = movieResultsPageResponse.body();
            if (Objects.isNull(body)) {
                log.info("Get movie info from tmdb failed, filepath: {}", extractInfo.getFilepath());
                break;
            }

            List<BaseMovie> results = body.results;
            if (Objects.isNull(results) || results.isEmpty()) {
                log.info("Get movie info from tmdb failed, filepath: {}", extractInfo.getFilepath());
                break;
            }
            if (Objects.nonNull(extractInfo.getYear())) {
                for (BaseMovie result : results) {
                    Calendar calendar = Calendar.getInstance();
                    if (Objects.isNull(result) || Objects.isNull(result.release_date)) {
                        log.info("Get movie release date failed, filepath: {}, baseMovie: {}", extractInfo.getFilepath(), JSONUtil.toJsonStr(result));
                        continue;
                    }
                    calendar.setTime(result.release_date);
                    Integer year = calendar.get(Calendar.YEAR);
                    if (StringUtils.equals(result.title, extractInfo.getName()) && Objects.equals(year, extractInfo.getYear())) {
                        baseMovie = result;
                        break;
                    }
                    if (StringUtils.equals(result.original_title, extractInfo.getName()) && Objects.equals(year, extractInfo.getYear())) {
                        baseMovie = result;
                        break;
                    }
                }
            }
            if (Objects.nonNull(baseMovie)) {
                break;
            }
        }
        if (Objects.isNull(baseMovie)) {
            log.info("Find movie failed, name: {}, filepath: {}", extractInfo.getName(), extractInfo.getFilepath());
            return;
        }

        Response<com.uwetrottmann.tmdb2.entities.Movie> tmdbMovieResponse = tmdbSearchService.searchMovieSummary(baseMovie.id);
        Movie tmdbMovie = tmdbMovieResponse.body();
        com.klaxon.kserver.module.media.model.entity.Movie movie = movieManager.saveMovie(tmdbMovie);
        if (Objects.isNull(movie)) {
            log.info("Save movie failed, filepath: {}", extractInfo.getFilepath());
            return;
        }
        moviePathManager.saveMoviePath(movie, mediaLibrary.getId(), extractInfo.getFilepath());
    }

    public Movie extract(String name, Integer release) throws Exception {
        Response<MovieResultsPage> movieResultsPageResponse = tmdbSearchService.searchMovie(name, 1);
        MovieResultsPage body = movieResultsPageResponse.body();
        if (Objects.isNull(body)) {
            log.info("Get movie info from tmdb failed, movie name: {}", name);
            return null;
        }

        List<BaseMovie> results = body.results;
        if (Objects.isNull(results) || results.isEmpty()) {
            log.info("Get movie info from tmdb failed, movie name: {}", name);
            return null;
        }

        BaseMovie baseMovie = results.get(0);
        if (Objects.nonNull(release)) {
            for (BaseMovie result : results) {
                Calendar calendar = Calendar.getInstance();
                if (Objects.isNull(result) || Objects.isNull(result.release_date)) {
                    log.info("Get movie release date failed, movie name: {}, baseMovie: {}", name, JSONUtil.toJsonStr(result));
                    continue;
                }
                calendar.setTime(result.release_date);
                Integer year = calendar.get(Calendar.YEAR);
                if (StringUtils.equals(result.title, name) && Objects.equals(year, release)) {
                    baseMovie = result;
                    break;
                }
                if (StringUtils.equals(result.original_title, name) && Objects.equals(year, release)) {
                    baseMovie = result;
                    break;
                }
            }
        }
        Response<com.uwetrottmann.tmdb2.entities.Movie> tmdbMovieResponse = tmdbSearchService.searchMovieSummary(baseMovie.id);
        return tmdbMovieResponse.body();
    }

}
