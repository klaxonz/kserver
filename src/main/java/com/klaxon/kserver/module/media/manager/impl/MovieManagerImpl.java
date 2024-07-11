package com.klaxon.kserver.module.media.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.klaxon.kserver.module.media.manager.MovieManager;
import com.klaxon.kserver.module.media.mapper.GenreMapper;
import com.klaxon.kserver.module.media.mapper.MovieCastMapper;
import com.klaxon.kserver.module.media.mapper.MovieGenreMapper;
import com.klaxon.kserver.module.media.mapper.MovieMapper;
import com.klaxon.kserver.module.media.model.entity.Movie;
import com.klaxon.kserver.module.media.model.entity.MovieCast;
import com.klaxon.kserver.module.media.model.entity.MovieGenre;
import com.uwetrottmann.tmdb2.entities.CastMember;
import com.uwetrottmann.tmdb2.entities.Genre;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class MovieManagerImpl implements MovieManager {

    @Resource
    private MovieMapper movieMapper;
    @Resource
    private GenreMapper genreMapper;
    @Resource
    private MovieGenreMapper movieGenreMapper;
    @Resource
    private MovieCastMapper movieCreditMapper;

    @Override
    @Transactional
    public Movie saveMovie(com.uwetrottmann.tmdb2.entities.Movie tmdbMovie) {
        Movie movie = new Movie();
        movie.setTmdbId(Long.valueOf(tmdbMovie.id));
        movie.setTitle(tmdbMovie.title);
        movie.setOriginTitle(tmdbMovie.original_title);
        movie.setOriginLanguage(tmdbMovie.original_language);
        movie.setBackdropPath(tmdbMovie.backdrop_path);
        movie.setPosterPath(tmdbMovie.poster_path);
        movie.setOverview(tmdbMovie.overview);
        movie.setIfAdult(tmdbMovie.adult);
        movie.setRuntime(tmdbMovie.runtime);
        if (Objects.nonNull(tmdbMovie.release_date)) {
            movie.setReleaseDate(tmdbMovie.release_date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        try {
            Movie movieInDb = movieMapper.selectOne(new LambdaQueryWrapper<Movie>().eq(Movie::getTmdbId, tmdbMovie.id));
            if (Objects.isNull(movieInDb)) {
                movieMapper.insert(movie);
            } else {
                movie = movieInDb;
            }
        } catch (DuplicateKeyException e) {
            movie = movieMapper.selectOne(new LambdaQueryWrapper<Movie>().eq(Movie::getTmdbId, tmdbMovie.id));
        }

        // movie cast info
        List<CastMember> cast = tmdbMovie.credits != null ? tmdbMovie.credits.cast : null;
        if (cast != null) {
            for (CastMember castMember : cast){
                MovieCast movieCast = new MovieCast();
                movieCast.setMovieId(movie.getId());
                movieCast.setTmdbId(movie.getTmdbId());
                movieCast.setCreditId(castMember.credit_id);
                movieCast.setName(castMember.name);
                movieCast.setCharacterName(castMember.character);
                movieCast.setProfilePath(castMember.profile_path);
                movieCast.setOrderNo(castMember.order);
                try {
                    MovieCast creditInDb = movieCreditMapper.selectOne(new LambdaQueryWrapper<MovieCast>()
                            .eq(MovieCast::getCreditId, movieCast.getCreditId()));
                    if (Objects.isNull(creditInDb)) {
                        movieCreditMapper.insert(movieCast);
                    }
                } catch (DuplicateKeyException e) {
                    log.warn("duplicate credit id: {}", movieCast.getCreditId(), e);
                }
            }
        }

        // movie genre info
        if (tmdbMovie.genres != null) {
            for (Genre tmdbGenre : tmdbMovie.genres) {
                if (StringUtils.isBlank(tmdbGenre.name)) {
                    continue;
                }

                com.klaxon.kserver.module.media.model.entity.Genre genre = new com.klaxon.kserver.module.media.model.entity.Genre();
                genre.setTmdbId(tmdbGenre.id);
                genre.setName(tmdbGenre.name);
                try {
                    com.klaxon.kserver.module.media.model.entity.Genre genreInDb = genreMapper.selectOne(
                            new LambdaQueryWrapper<com.klaxon.kserver.module.media.model.entity.Genre>()
                            .eq(com.klaxon.kserver.module.media.model.entity.Genre::getTmdbId, tmdbGenre.id));
                    if (Objects.isNull(genreInDb)) {
                        genreMapper.insert(genre);
                    } else {
                        genre = genreInDb;
                    }
                } catch (DuplicateKeyException e) {
                    genre = genreMapper.selectOne(new LambdaQueryWrapper<com.klaxon.kserver.module.media.model.entity.Genre>()
                            .eq(com.klaxon.kserver.module.media.model.entity.Genre::getTmdbId, tmdbGenre.id));
                    log.warn("duplicate genre id: {}", genre.getTmdbId(), e);
                }

                MovieGenre movieGenre = new MovieGenre();
                movieGenre.setMovieId(movie.getId());
                movieGenre.setGenreId(genre.getId());
                movieGenre.setName(tmdbGenre.name);
                try {
                    MovieGenre movieGenreInDb = movieGenreMapper.selectOne(new LambdaQueryWrapper<MovieGenre>()
                            .eq(MovieGenre::getMovieId, movie.getId())
                            .eq(MovieGenre::getGenreId, genre.getId()));
                    if (Objects.isNull(movieGenreInDb)) {
                        movieGenreMapper.insert(movieGenre);
                    }
                } catch (DuplicateKeyException e) {
                    log.warn("duplicate movie genre id: {}", movieGenre.getGenreId(), e);
                }
            }
        }

        return movie;
    }

}
