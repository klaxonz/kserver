package com.klaxon.kserver.module.media.extractor;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.klaxon.kserver.module.media.mapper.ActorImageMapper;
import com.klaxon.kserver.module.media.mapper.ActorMapper;
import com.klaxon.kserver.module.media.mapper.ImageMapper;
import com.klaxon.kserver.module.media.mapper.MovieActorMapper;
import com.klaxon.kserver.module.media.mapper.MovieMapper;
import com.klaxon.kserver.module.media.model.entity.Actor;
import com.klaxon.kserver.module.media.model.entity.ActorImage;
import com.klaxon.kserver.module.media.model.entity.Image;
import com.klaxon.kserver.module.media.model.entity.MediaLibrary;
import com.klaxon.kserver.module.media.model.entity.Movie;
import com.klaxon.kserver.module.media.model.entity.MovieActor;
import com.klaxon.kserver.module.media.service.TmdbSearchService;
import com.uwetrottmann.tmdb2.entities.BaseMovie;
import com.uwetrottmann.tmdb2.entities.BasePerson;
import com.uwetrottmann.tmdb2.entities.CastMember;
import com.uwetrottmann.tmdb2.entities.Credit;
import com.uwetrottmann.tmdb2.entities.Credits;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;
import com.uwetrottmann.tmdb2.entities.Person;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class MovieMetaExtractor extends MediaMetaExtractor {

    @Resource
    private ActorMapper actorMapper;
    @Resource
    private ImageMapper imageMapper;
    @Resource
    private MovieMapper movieMapper;
    @Resource
    private ActorImageMapper actorImageMapper;
    @Resource
    private MovieActorMapper movieActorMapper;
    @Resource
    private TmdbSearchService tmdbSearchService;

    @Override
    public Integer getType() {
        return FileExtractInfo.TYPE_MOVIE;
    }

    @Override
    public void extract(MediaLibrary mediaLibrary, FileExtractInfo extractInfo) throws Exception {
        Response<MovieResultsPage> movieResultsPageResponse = tmdbSearchService.searchMovie(extractInfo.getName());
        MovieResultsPage body = movieResultsPageResponse.body();
        if (Objects.isNull(body)) {
            log.info("Get movie info from tmdb failed, filepath: {}", extractInfo.getFilepath());
            return;
        }

        List<BaseMovie> results = body.results;
        if (Objects.isNull(results) || results.isEmpty()) {
            log.info("Get movie info from tmdb failed, filepath: {}", extractInfo.getFilepath());
            return;
        }

        BaseMovie baseMovie = results.get(0);
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

        // 查询数据库是否存在，不存在入库
        Movie movie = buildMovie(mediaLibrary, extractInfo.getFilepath(), baseMovie);

        // 获取演员信息
        buildActor(extractInfo.getFilepath(), baseMovie, movie);
    }

    private void buildActor(String filePath, BaseMovie baseMovie, Movie movie) throws Exception {
        Response<Credits> creditsResponse = tmdbSearchService.searchMovieCredits(baseMovie.id);
        Credits credits = creditsResponse.body();
        log.info("Get movie credits from tmdb, filepath: {}, credits: {}", filePath, JSONUtil.toJsonStr(credits));

        if (Objects.isNull(credits)) {
            log.info("Get movie credits from tmdb failed, filepath: {}", filePath);
            return;
        }

        List<CastMember> cast = credits.cast;
        if (Objects.isNull(cast)) {
            log.info("Get movie credits from tmdb, movieId:{}, filepath: {}", movie.getId(), filePath);
            return;
        }

        for (CastMember castMember : cast) {
            if (StringUtils.isBlank(castMember.name)) {
                log.info("cast member name unknown, skip, movieId: {}, filepath: {}", movie.getId(), filePath);
                continue;
            }

            Response<Credit> creditResponse = tmdbSearchService.searchCredit(castMember.credit_id);
            Credit credit = creditResponse.body();
            if (Objects.isNull(credit)) {
                log.info("Get movie credit from tmdb failed, cast name: {}, filepath: {}", castMember.name, filePath);
                continue;
            }
            BasePerson basePerson = credit.person;
            if (Objects.isNull(basePerson) || Objects.isNull(basePerson.id)) {
                log.info("Get movie credit from tmdb failed, cast name: {}, filepath: {}", castMember.name, filePath);
                continue;
            }

            Response<Person> personResponse = tmdbSearchService.searchPersonSummary(basePerson.id);
            Person person = personResponse.body();

            // save actor base info
            Actor actor = actorMapper.selectOne(new LambdaQueryWrapper<Actor>().eq(Actor::getName, castMember.name));
            if (Objects.isNull(actor) && Objects.nonNull(person)) {
                actor = new Actor();
                actor.setName(castMember.name);
                actor.setGender(person.gender);
                if (Objects.nonNull(person.birthday)) {
                    actor.setBirthdate(person.birthday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                }
                actorMapper.insert(actor);
            }

            // save actor and movie relation
            MovieActor movieActor = movieActorMapper.selectOne(new LambdaQueryWrapper<MovieActor>()
                    .eq(MovieActor::getMovieId, movie.getId())
                    .eq(MovieActor::getActorId, actor.getId()));

            if (Objects.isNull(movieActor)) {
                movieActor = new MovieActor();
                movieActor.setMovieId(movie.getId());
                movieActor.setActorId(actor.getId());
                movieActor.setActorCharacter(castMember.character);
                movieActorMapper.insert(movieActor);
            }

            if ((StringUtils.isBlank(castMember.profile_path))) {
                log.info("Get movie credit profile path from tmdb failed, cast name: {}, filepath: {}", castMember.name, filePath);
                continue;
            }

            // save image
            Image image = imageMapper.selectOne(new LambdaQueryWrapper<Image>()
                    .eq(Image::getUrl, castMember.profile_path)
                    .eq(Image::getSource, 1));

            if (Objects.isNull(image)) {
                image = new Image();
                image.setUrl(castMember.profile_path);
                image.setType(3);
                image.setSource(1);
                imageMapper.insert(image);
            }

            // save actor image relation
            ActorImage actorImage = actorImageMapper.selectOne(new LambdaQueryWrapper<ActorImage>()
                    .eq(ActorImage::getActorId, actor.getId())
                    .eq(ActorImage::getImageId, image.getId()));

            if (Objects.isNull(actorImage)) {
                actorImage = new ActorImage();
                actorImage.setActorId(actor.getId());
                actorImage.setImageId(image.getId());
                actorImageMapper.insert(actorImage);
            }

        }
    }

    private Movie buildMovie(MediaLibrary mediaLibrary, String filePath, BaseMovie baseMovie) {
        Movie movie = movieMapper.selectOne(new LambdaQueryWrapper<Movie>().eq(Movie::getLibraryId, mediaLibrary.getId()).eq(Movie::getPath, filePath));
        if (Objects.isNull(movie)) {
            movie = new Movie();
            movie.setLibraryId(mediaLibrary.getId());
            movie.setPath(filePath);
            movie.setBackdropPath(baseMovie.backdrop_path);
            movie.setIfAdult(baseMovie.adult);
            movie.setTitle(baseMovie.title);
            movie.setOriginTitle(baseMovie.original_title);
            movie.setOriginLanguage(baseMovie.original_language);
            if (Objects.nonNull(baseMovie.release_date)) {
                movie.setReleaseDate(baseMovie.release_date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            movie.setPosterPath(baseMovie.poster_path);
            movie.setOverview(baseMovie.overview);
            movieMapper.insert(movie);
        }

        return movie;
    }


}
