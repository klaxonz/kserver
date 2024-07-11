package com.klaxon.kserver.module.media.service.impl;

import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.klaxon.kserver.bean.PageInfo;
import com.klaxon.kserver.constants.RedisKeyPrefixConstants;
import com.klaxon.kserver.exception.BizCodeEnum;
import com.klaxon.kserver.exception.BizException;
import com.klaxon.kserver.module.media.mapper.MediaLibraryMapper;
import com.klaxon.kserver.module.media.mapper.MovieCastMapper;
import com.klaxon.kserver.module.media.mapper.MovieGenreMapper;
import com.klaxon.kserver.module.media.mapper.MovieMapper;
import com.klaxon.kserver.module.media.mapper.MoviePathMapper;
import com.klaxon.kserver.module.media.model.entity.MediaLibrary;
import com.klaxon.kserver.module.media.model.entity.Movie;
import com.klaxon.kserver.module.media.model.entity.MovieCast;
import com.klaxon.kserver.module.media.model.entity.MovieGenre;
import com.klaxon.kserver.module.media.model.entity.MoviePath;
import com.klaxon.kserver.module.media.model.req.MovieDeleteReq;
import com.klaxon.kserver.module.media.model.req.MovieDetailReq;
import com.klaxon.kserver.module.media.model.req.MovieListReq;
import com.klaxon.kserver.module.media.model.rsp.MovieDetailRsp;
import com.klaxon.kserver.module.media.model.rsp.MovieListRsp;
import com.klaxon.kserver.module.media.service.MovieService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    @Resource
    private MovieMapper movieMapper;
    @Resource
    private MoviePathMapper moviePathMapper;
    @Resource
    private MovieGenreMapper movieGenreMapper;
    @Resource
    private MovieCastMapper movieCastMapper;
    @Resource
    private MediaLibraryMapper mediaLibraryMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public PageInfo<MovieListRsp> list(MovieListReq req) {

        LambdaQueryWrapper<Movie> condition = new LambdaQueryWrapper<>();
        condition.orderByDesc(Movie::getCreateTime);

        if (StringUtils.isNotBlank(req.getName())) {
            condition.like(Movie::getTitle, "%" + req.getName() + "%");
        }

        Page<Movie> page = movieMapper.selectPage(Page.of(req.getPage(), req.getPageSize()), condition);
        List<Movie> records = page.getRecords();

        // 封装数据
        List<MovieListRsp> movies = Lists.newArrayList();
        records.forEach(movie -> {
            MovieListRsp movieListRsp = new MovieListRsp();
            movieListRsp.setId(movie.getId());
            movieListRsp.setName(movie.getTitle());
            movieListRsp.setRelease(movie.getReleaseDate());
            movieListRsp.setPoster(movie.getPosterPath());
            movies.add(movieListRsp);
        });

        return new PageInfo<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getPages(), movies);
    }

    @Override
    public MovieDetailRsp detail(MovieDetailReq req) {
        Movie movie = movieMapper.selectOne(new LambdaQueryWrapper<Movie>().eq(Movie::getId, req.getId()));
        if (Objects.isNull(movie)) {
            throw new BizException(BizCodeEnum.RESOURCE_NOT_EXIST, "查询电影不存在");
        }
        MovieDetailRsp movieDetailRsp = new MovieDetailRsp();
        movieDetailRsp.setId(movie.getId());
        movieDetailRsp.setName(movie.getTitle());
        movieDetailRsp.setRelease(movie.getReleaseDate());
        movieDetailRsp.setBackdropPath(movie.getBackdropPath());
        movieDetailRsp.setPosterPath(movie.getPosterPath());
        movieDetailRsp.setOverview(movie.getOverview());
        List<MovieDetailRsp.MovieCastRsp> casts = Lists.newArrayList();
        movieDetailRsp.setCasts(casts);

        // 获取电影演员
        List<MovieCast> movieCasts = movieCastMapper.selectList(new LambdaQueryWrapper<MovieCast>()
                .eq(MovieCast::getMovieId, movie.getId()));
        for (MovieCast cast : movieCasts) {
            MovieDetailRsp.MovieCastRsp castMember = new MovieDetailRsp.MovieCastRsp();
            castMember.setProfilePath(cast.getProfilePath());
            castMember.setName(cast.getName());
            castMember.setCharacter(cast.getCharacterName());
            casts.add(castMember);
        }

        // 电影类型
        List<MovieGenre> movieGenres = movieGenreMapper.selectList(new LambdaQueryWrapper<MovieGenre>()
                .eq(MovieGenre::getMovieId, movie.getId()));
        List<String> genreNames = movieGenres.stream().map(MovieGenre::getName).distinct().collect(Collectors.toList());
        movieDetailRsp.setGenres(genreNames);

        // 获取文件路径
        List<MoviePath> moviePaths = moviePathMapper.selectList(new LambdaQueryWrapper<MoviePath>()
                .eq(MoviePath::getMovieId, movie.getId()));
        if (!moviePaths.isEmpty()) {
            MediaLibrary mediaLibrary = mediaLibraryMapper.selectOne(new LambdaQueryWrapper<MediaLibrary>()
                    .eq(MediaLibrary::getId, moviePaths.get(0).getLibraryId()));
            String url = mediaLibrary.getUrl() + "/d" + moviePaths.get(0).getPath();
            movieDetailRsp.setUrl(url);
        }

        return movieDetailRsp;
    }

    @Override
    @Transactional
    public void delete(MovieDeleteReq req) {
        redisTemplate.opsForValue().set(RedisKeyPrefixConstants.MOVIE_PREFIX + req.getId(), req.getId());
        movieMapper.deleteById(req.getId());
        movieCastMapper.delete(new LambdaQueryWrapper<MovieCast>().eq(MovieCast::getMovieId, req.getId()));

        List<MoviePath> moviePaths = moviePathMapper.selectList(new LambdaQueryWrapper<MoviePath>()
                .eq(MoviePath::getMovieId, req.getId()));
        moviePaths.forEach(moviePath -> {
            String filePathMD5 = MD5.create().digestHex(moviePath.getPath(), StandardCharsets.UTF_8);
            String key = RedisKeyPrefixConstants.MOVIE_PREFIX + moviePath.getLibraryId() + ":" + filePathMD5;
            redisTemplate.opsForValue().getAndExpire(key, 1, TimeUnit.MILLISECONDS);
        });

        moviePathMapper.delete(new LambdaQueryWrapper<MoviePath>().eq(MoviePath::getMovieId, req.getId()));
    }

    @Override
    public void scrape(String name, Integer release) {

    }


}
