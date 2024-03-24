package com.klaxon.kserver.module.media.extractor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.klaxon.kserver.module.media.mapper.ActorImageMapper;
import com.klaxon.kserver.module.media.mapper.ActorMapper;
import com.klaxon.kserver.module.media.mapper.ImageMapper;
import com.klaxon.kserver.module.media.mapper.TvSeriesActorMapper;
import com.klaxon.kserver.module.media.mapper.TvSeriesEpisodeImageMapper;
import com.klaxon.kserver.module.media.mapper.TvSeriesEpisodeMapper;
import com.klaxon.kserver.module.media.mapper.TvSeriesMapper;
import com.klaxon.kserver.module.media.mapper.TvSeriesSeasonImageMapper;
import com.klaxon.kserver.module.media.mapper.TvSeriesSeasonMapper;
import com.klaxon.kserver.module.media.model.entity.Actor;
import com.klaxon.kserver.module.media.model.entity.ActorImage;
import com.klaxon.kserver.module.media.model.entity.Image;
import com.klaxon.kserver.module.media.model.entity.MediaLibrary;
import com.klaxon.kserver.module.media.model.entity.TvSeries;
import com.klaxon.kserver.module.media.model.entity.TvSeriesActor;
import com.klaxon.kserver.module.media.model.entity.TvSeriesEpisode;
import com.klaxon.kserver.module.media.model.entity.TvSeriesEpisodeImage;
import com.klaxon.kserver.module.media.model.entity.TvSeriesSeason;
import com.klaxon.kserver.module.media.model.entity.TvSeriesSeasonImage;
import com.klaxon.kserver.module.media.service.TmdbSearchService;
import com.uwetrottmann.tmdb2.entities.BasePerson;
import com.uwetrottmann.tmdb2.entities.BaseTvShow;
import com.uwetrottmann.tmdb2.entities.CastMember;
import com.uwetrottmann.tmdb2.entities.Credit;
import com.uwetrottmann.tmdb2.entities.Credits;
import com.uwetrottmann.tmdb2.entities.Images;
import com.uwetrottmann.tmdb2.entities.Person;
import com.uwetrottmann.tmdb2.entities.TvEpisode;
import com.uwetrottmann.tmdb2.entities.TvSeason;
import com.uwetrottmann.tmdb2.entities.TvShow;
import com.uwetrottmann.tmdb2.entities.TvShowResultsPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import retrofit2.Response;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class TvMetaExtractor extends MediaMetaExtractor {

    @Resource
    private ImageMapper imageMapper;
    @Resource
    private ActorMapper actorMapper;
    @Resource
    private TvSeriesMapper tvSeriesMapper;
    @Resource
    private ActorImageMapper actorImageMapper;
    @Resource
    private TvSeriesActorMapper tvSeriesActorMapper;
    @Resource
    private TvSeriesSeasonMapper tvSeriesSeasonMapper;
    @Resource
    private TvSeriesEpisodeMapper tvSeriesEpisodeMapper;
    @Resource
    private TvSeriesSeasonImageMapper tvSeriesSeasonImageMapper;
    @Resource
    private TvSeriesEpisodeImageMapper tvSeriesEpisodeImageMapper;
    @Resource
    private TmdbSearchService tmdbSearchService;

    @Override
    public Integer getType() {
        return FileExtractInfo.TYPE_TV;
    }

    @Override
    public void extract(MediaLibrary mediaLibrary, FileExtractInfo extractInfo) throws Exception {
        Response<TvShowResultsPage> showResultsResponse = tmdbSearchService.searchBaseTvShow(extractInfo.getName());

        TvShowResultsPage body = showResultsResponse.body();
        if (Objects.isNull(body)) {
            log.info("Get tv base info from tmdb failed, filepath: {}", extractInfo.getFilepath());
            return;
        }
        List<BaseTvShow> results = body.results;
        BaseTvShow baseTvShow = findBaseTvShow(extractInfo, results);
        if (Objects.isNull(baseTvShow) || Objects.isNull(baseTvShow.id)) {
            log.info("Get tv base info from tmdb failed, filepath: {}", extractInfo.getFilepath());
            return;
        }

        Response<TvShow> tvShowResponse = tmdbSearchService.searchTvShow(baseTvShow.id);
        TvShow tvShow = tvShowResponse.body();
        if (Objects.isNull(tvShow) || Objects.isNull(tvShow.id)) {
            log.info("Get tv full info from tmdb failed, filepath: {}", extractInfo.getFilepath());
            return;
        }

        // build tv series
        TvSeries tvSeries = buildTvSeries(mediaLibrary, tvShow);

        // build tv series season
        int extractSeasonNumber = extractInfo.getSeason();
        Response<TvSeason> tvSeasonResponse = tmdbSearchService.searchTvSeason(tvShow.id, extractSeasonNumber);
        TvSeason tvSeason = tvSeasonResponse.body();
        if (Objects.isNull(tvSeason)) {
            log.info("Get tv season info from tmdb failed, tvShowId: {}, seasonNumber: {}, filepath: {}",
                    tvShow.id, extractSeasonNumber, extractInfo.getFilepath());
            return;
        }
        TvSeriesSeason tvSeriesSeason = buildTvSeriesSeason(mediaLibrary, tvSeries, tvSeason);

        // build tv series season image
        Response<Images> tvSeasonImageResponse = tmdbSearchService.searchTvSeasonImages(tvShow.id, extractSeasonNumber);
        Images images = tvSeasonImageResponse.body();
        if (Objects.nonNull(images)) {
            List<com.uwetrottmann.tmdb2.entities.Image> backdrops = images.backdrops;
            saveTvSeriesSeasonImages(tvSeriesSeason, backdrops, 3);

            List<com.uwetrottmann.tmdb2.entities.Image> posters = images.posters;
            saveTvSeriesSeasonImages(tvSeriesSeason, posters, 2);

            List<com.uwetrottmann.tmdb2.entities.Image> stills = images.stills;
            saveTvSeriesSeasonImages(tvSeriesSeason, stills, 4);
        }

        // build actor info
        Response<Credits> creditsResponse = tmdbSearchService.searchTvSeasonCredits(tvShow.id, extractSeasonNumber);
        Credits credits = creditsResponse.body();
        if (Objects.isNull(credits) || Objects.isNull(credits.cast) || credits.cast.isEmpty()) {
            log.info("Get tv season credits from tmdb failed, tvShowId: {}, seasonNumber: {}, filepath: {}",
                    tvShow.id, extractSeasonNumber, extractInfo.getFilepath());
            return;
        }
        List<CastMember> castMembers = credits.cast;
        for (CastMember castMember : castMembers) {
            if (StringUtils.isBlank(castMember.name)) {
                log.info("cast member name unknown, skip, tvSeriesId: {}, tvSeriesSeasonId: {} ,filepath: {}",
                        tvSeries.getId(), tvSeriesSeason.getId(), extractInfo.getFilepath());
                continue;
            }

            Response<Credit> creditResponse = tmdbSearchService.searchCredit(castMember.credit_id);
            Credit credit = creditResponse.body();
            if (Objects.isNull(credit)) {
                log.info("Get movie credit from tmdb failed, cast name: {}, filepath: {}",
                        castMember.name, extractInfo.getFilepath());
                continue;
            }
            BasePerson basePerson = credit.person;
            if (Objects.isNull(basePerson) || Objects.isNull(basePerson.id)) {
                log.info("Get movie credit from tmdb failed, cast name: {}, filepath: {}",
                        castMember.name, extractInfo.getFilepath());
                continue;
            }

            Response<Person> personResponse = tmdbSearchService.searchPersonSummary(basePerson.id);
            Person person = personResponse.body();
            if (Objects.isNull(person)) {
                log.info("Get person summary from tmdb failed, cast name: {}, filepath: {}",
                        castMember.name, extractInfo.getFilepath());
                continue;
            }

            // save actor base info
            Actor actor = actorMapper.selectOne(new LambdaQueryWrapper<Actor>().eq(Actor::getName, castMember.name));
            if (Objects.isNull(actor)) {
                actor = new Actor();
                actor.setName(castMember.name);
                actor.setGender(person.gender);
                if (Objects.nonNull(person.birthday)) {
                    actor.setBirthdate(person.birthday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                }
                actorMapper.insert(actor);
            }

            // save actor and movie relation
            TvSeriesActor tvSeriesActor = tvSeriesActorMapper.selectOne(new LambdaQueryWrapper<TvSeriesActor>()
                    .eq(TvSeriesActor::getTvSeriesId, tvSeries.getId())
                    .eq(TvSeriesActor::getTvSeriesId, tvSeries.getId()));

            if (Objects.isNull(tvSeriesActor)) {
                tvSeriesActor = new TvSeriesActor();
                tvSeriesActor.setTvSeriesId(tvSeries.getId());
                tvSeriesActor.setTvSeriesSeasonId(tvSeriesSeason.getId());
                tvSeriesActor.setActorId(actor.getId());
                tvSeriesActor.setActorCharacter(castMember.character);
                tvSeriesActorMapper.insert(tvSeriesActor);
            }

            if ((StringUtils.isBlank(castMember.profile_path))) {
                log.info("Get movie credit profile path from tmdb failed, cast name: {}, filepath: {}",
                        castMember.name, extractInfo.getFilepath());
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

        // build tv series episode
        List<TvEpisode> episodes = tvSeason.episodes;
        int extractEpisodeNumber = extractInfo.getEpisode();

        TvEpisode tvEpisode = null;
        if (Objects.isNull(episodes) || episodes.isEmpty()) {
            Response<TvEpisode> tvEpisodeResponse = tmdbSearchService.searchTvEpisode(tvShow.id, extractSeasonNumber, extractEpisodeNumber);
            tvEpisode = tvEpisodeResponse.body();
        } else {
            for (TvEpisode episode : episodes) {
                if (episode.episode_number != null && episode.episode_number == extractEpisodeNumber) {
                    tvEpisode= episode;
                    break;
                }
            }
        }

        if (Objects.isNull(tvEpisode)) {
            log.info("Get tv episode info from tmdb failed, tvShowId: {}, seasonNumber: {}, episodeNumber: {}, filepath: {}",
                    tvShow.id, extractSeasonNumber, extractEpisodeNumber, extractInfo.getFilepath());
            return;
        }

        TvSeriesEpisode tvSeriesEpisode = buildTvSeriesEpisode(mediaLibrary, extractInfo.getFilepath(), tvSeries, tvSeriesSeason, tvEpisode);
        // save tv series episode image
        Response<Images> imagesResponse = tmdbSearchService.searchTvEpisodeImages(tvShow.id, extractSeasonNumber, extractEpisodeNumber);
        Images episodeImages = imagesResponse.body();
        if (Objects.nonNull(episodeImages)) {
            List<com.uwetrottmann.tmdb2.entities.Image> backdrops = episodeImages.backdrops;
            saveTvSeriesEpisodeImages(tvSeriesEpisode, backdrops, 3);

            List<com.uwetrottmann.tmdb2.entities.Image> posters = episodeImages.posters;
            saveTvSeriesEpisodeImages(tvSeriesEpisode, posters, 2);

            List<com.uwetrottmann.tmdb2.entities.Image> stills = episodeImages.stills;
            saveTvSeriesEpisodeImages(tvSeriesEpisode, stills, 4);
        }
    }


    private void saveTvSeriesSeasonImages(TvSeriesSeason tvSeriesSeason, List<com.uwetrottmann.tmdb2.entities.Image> backdrops, Integer type) {
        if (Objects.nonNull(backdrops) && !backdrops.isEmpty()) {
            for (com.uwetrottmann.tmdb2.entities.Image backdrop : backdrops) {
                Image image = imageMapper.selectOne(new LambdaQueryWrapper<Image>()
                        .eq(Image::getUrl, backdrop.file_path)
                        .eq(Image::getType, type)
                        .eq(Image::getSource, 1));

                if (Objects.isNull(image)) {
                    image = new Image();
                    image.setUrl(backdrop.file_path);
                    // 背景图
                    image.setType(type);
                    image.setSource(1);
                    imageMapper.insert(image);

                    TvSeriesSeasonImage tvSeriesSeasonImage = new TvSeriesSeasonImage();
                    tvSeriesSeasonImage.setTvSeriesId(tvSeriesSeason.getTvSeriesId());
                    tvSeriesSeasonImage.setTvSeriesSeasonId(tvSeriesSeason.getId());
                    tvSeriesSeasonImage.setImageType(type);
                    tvSeriesSeasonImage.setImageId(image.getId());
                    tvSeriesSeasonImageMapper.insert(tvSeriesSeasonImage);
                }
            }
        }
    }

    private void saveTvSeriesEpisodeImages(TvSeriesEpisode tvSeriesEpisode, List<com.uwetrottmann.tmdb2.entities.Image> backdrops, Integer type) {
        if (Objects.nonNull(backdrops) && !backdrops.isEmpty()) {
            for (com.uwetrottmann.tmdb2.entities.Image backdrop : backdrops) {
                Image image = imageMapper.selectOne(new LambdaQueryWrapper<Image>()
                        .eq(Image::getUrl, backdrop.file_path)
                        .eq(Image::getType, type)
                        .eq(Image::getSource, 1));

                if (Objects.isNull(image)) {
                    image = new Image();
                    image.setUrl(backdrop.file_path);
                    // 背景图
                    image.setType(type);
                    image.setSource(1);
                    imageMapper.insert(image);

                    TvSeriesEpisodeImage tvSeriesEpisodeImage = new TvSeriesEpisodeImage();
                    tvSeriesEpisodeImage.setTvSeriesId(tvSeriesEpisode.getId());
                    tvSeriesEpisodeImage.setTvSeriesSeasonId(tvSeriesEpisode.getTvSeriesSeasonId());
                    tvSeriesEpisodeImage.setTvSeriesEpisodeId(tvSeriesEpisode.getId());
                    tvSeriesEpisodeImage.setImageType(type);
                    tvSeriesEpisodeImage.setImageId(image.getId());
                    tvSeriesEpisodeImageMapper.insert(tvSeriesEpisodeImage);
                }
            }
        }
    }

    private TvSeriesEpisode buildTvSeriesEpisode(MediaLibrary mediaLibrary, String filePath, TvSeries tvSeries, TvSeriesSeason tvSeriesSeason, TvEpisode tvEpisode) {
        TvSeriesEpisode tvSeriesEpisode = tvSeriesEpisodeMapper.selectOne(new LambdaQueryWrapper<TvSeriesEpisode>()
                .eq(TvSeriesEpisode::getLibraryId, mediaLibrary.getId())
                .eq(TvSeriesEpisode::getTvSeriesId, tvSeries.getId())
                .eq(TvSeriesEpisode::getTvSeriesSeasonId, tvSeriesSeason.getId())
                .eq(TvSeriesEpisode::getSeason, tvEpisode.season_number)
                .eq(TvSeriesEpisode::getEpisode, tvEpisode.episode_number));

        if (Objects.isNull(tvSeriesEpisode)) {
            tvSeriesEpisode = new TvSeriesEpisode();
            tvSeriesEpisode.setLibraryId(mediaLibrary.getId());
            tvSeriesEpisode.setTvSeriesId(tvSeries.getId());
            tvSeriesEpisode.setTvSeriesSeasonId(tvSeriesSeason.getId());
            tvSeriesEpisode.setTitle(tvEpisode.name);
            tvSeriesEpisode.setPath(filePath);
            tvSeriesEpisode.setSeason(tvEpisode.season_number);
            tvSeriesEpisode.setEpisode(tvEpisode.episode_number);
            tvSeriesEpisode.setOverview(tvEpisode.overview);
            if (Objects.nonNull(tvEpisode.air_date)) {
                tvSeriesEpisode.setReleaseDate(tvEpisode.air_date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            tvSeriesEpisodeMapper.insert(tvSeriesEpisode);
        }

        return tvSeriesEpisode;
    }

    private TvSeriesSeason buildTvSeriesSeason(MediaLibrary mediaLibrary, TvSeries tvSeries, TvSeason tvSeason) {
        TvSeriesSeason tvSeriesSeason = tvSeriesSeasonMapper.selectOne(new LambdaQueryWrapper<TvSeriesSeason>()
                .eq(TvSeriesSeason::getLibraryId, mediaLibrary.getId())
                .eq(TvSeriesSeason::getTvSeriesId, tvSeries.getId()));

        if (Objects.isNull(tvSeriesSeason)) {
            tvSeriesSeason = new TvSeriesSeason();
            tvSeriesSeason.setLibraryId(mediaLibrary.getId());
            tvSeriesSeason.setTvSeriesId(tvSeries.getId());
            tvSeriesSeason.setTitle(tvSeason.name);
            tvSeriesSeason.setSeason(tvSeason.season_number);
            assert tvSeason.episodes != null;
            tvSeriesSeason.setEpisodeCount(tvSeason.episodes.size());
            tvSeriesSeason.setOverview(tvSeason.overview);
            if (Objects.nonNull(tvSeason.air_date)) {
                tvSeriesSeason.setReleaseDate(tvSeason.air_date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            tvSeriesSeasonMapper.insert(tvSeriesSeason);
        }
        return tvSeriesSeason;
    }

    private TvSeries buildTvSeries(MediaLibrary mediaLibrary, TvShow tvShow) {
        TvSeries tvSeries = tvSeriesMapper.selectOne(new LambdaQueryWrapper<TvSeries>()
                .eq(TvSeries::getLibraryId, mediaLibrary.getId()).eq(TvSeries::getTmdbId, tvShow.id));

        if (Objects.isNull(tvSeries)) {
            tvSeries = new TvSeries();
            tvSeries.setLibraryId(mediaLibrary.getId());
            tvSeries.setBackdropPath(tvShow.backdrop_path);
            tvSeries.setOverview(tvShow.overview);
            tvSeries.setTmdbId(Long.valueOf(tvShow.id));
            tvSeries.setTitle(tvShow.name);
            tvSeries.setOriginTitle(tvShow.original_name);
            tvSeries.setOriginLanguage(tvShow.original_language);
            if (Objects.nonNull(tvShow.first_air_date)) {
                tvSeries.setReleaseDate(tvShow.first_air_date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            tvSeries.setPosterPath(tvShow.poster_path);
            tvSeries.setTotalSeason(tvShow.number_of_seasons);
            tvSeries.setTotalEpisode(tvShow.number_of_episodes);
            tvSeriesMapper.insert(tvSeries);
        }
        return tvSeries;
    }

    private BaseTvShow findBaseTvShow(FileExtractInfo extractInfo, List<BaseTvShow> results) {
        BaseTvShow baseTvShow = null;
        if (results != null && !results.isEmpty()) {
            baseTvShow = results.get(0);
            if (StringUtils.isNotBlank(extractInfo.getName())) {
                baseTvShow = findMatchingTvShow(results, extractInfo.getName());
            }
        }
        return baseTvShow;
    }

    private BaseTvShow findMatchingTvShow(List<BaseTvShow> results, String name) {
        for (BaseTvShow result : results) {
            if (Objects.nonNull(result)) {
                if (StringUtils.equals(result.name, name) || StringUtils.equals(result.original_name, name)) {
                    return result;
                }
            }
        }
        return null;
    }

}
