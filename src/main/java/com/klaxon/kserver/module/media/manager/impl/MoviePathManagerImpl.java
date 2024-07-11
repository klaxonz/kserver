package com.klaxon.kserver.module.media.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.klaxon.kserver.module.media.manager.MoviePathManager;
import com.klaxon.kserver.module.media.mapper.MoviePathMapper;
import com.klaxon.kserver.module.media.model.entity.Movie;
import com.klaxon.kserver.module.media.model.entity.MoviePath;
import com.klaxon.kserver.module.media.model.req.MovieDeleteReq;
import com.klaxon.kserver.module.media.service.MovieService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MoviePathManagerImpl implements MoviePathManager {

    @Resource
    private MoviePathMapper moviePathMapper;
    @Resource
    private MovieService movieService;

    @Override
    @Transactional
    public void deleteMoviePath(MoviePath moviePath) {
        moviePathMapper.delete(new LambdaQueryWrapper<MoviePath>()
                .eq(MoviePath::getMovieId, moviePath.getMovieId())
                .eq(MoviePath::getPath, moviePath.getPath()));

        List<MoviePath> paths = moviePathMapper.selectList(new LambdaQueryWrapper<MoviePath>()
                .eq(MoviePath::getPath, moviePath.getPath()));
        if (paths.isEmpty()) {
            // 删除电影
            MovieDeleteReq deleteReq = new MovieDeleteReq();
            deleteReq.setId(moviePath.getMovieId());
            movieService.delete(deleteReq);
        }
    }

    @Override
    @Transactional
    public void saveMoviePath(Movie movie, Long libraryId, String path) {
        List<MoviePath> paths = moviePathMapper.selectList(new LambdaQueryWrapper<MoviePath>()
                .eq(MoviePath::getPath, path)
                .eq(MoviePath::getMovieId, movie.getId()));
        if (paths.isEmpty()) {
            MoviePath moviePath = new MoviePath();
            moviePath.setMovieId(movie.getId());
            moviePath.setPath(path);
            moviePath.setLibraryId(libraryId);
            moviePathMapper.insert(moviePath);
        }
    }

}
