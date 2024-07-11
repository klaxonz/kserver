package com.klaxon.kserver.module.media.service.impl;

import com.klaxon.kserver.module.media.service.TmdbSearchService;
import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.AppendToResponse;
import com.uwetrottmann.tmdb2.entities.Credit;
import com.uwetrottmann.tmdb2.entities.Credits;
import com.uwetrottmann.tmdb2.entities.Images;
import com.uwetrottmann.tmdb2.entities.Movie;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;
import com.uwetrottmann.tmdb2.entities.Person;
import com.uwetrottmann.tmdb2.entities.TvEpisode;
import com.uwetrottmann.tmdb2.entities.TvSeason;
import com.uwetrottmann.tmdb2.entities.TvShow;
import com.uwetrottmann.tmdb2.entities.TvShowResultsPage;
import com.uwetrottmann.tmdb2.enumerations.AppendToResponseItem;
import com.uwetrottmann.tmdb2.services.SearchService;
import com.uwetrottmann.tmdb2.services.TvService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import javax.annotation.Resource;

@Slf4j
@Service
public class TmdbSearchServiceImpl implements TmdbSearchService {

    @Resource
    private Tmdb tmdb;

    @Override
    @Retryable(value = Exception.class, recover = "recover" ,backoff = @Backoff)
    public Response<MovieResultsPage> searchMovie(String name, Integer page) throws Exception {
        SearchService searchService = tmdb.searchService();
        return searchService
                .movie(name, page, "zh", null, null, null, null)
                .execute();
    }

    @Override
    @Retryable(value = Exception.class, recover = "recover" ,backoff = @Backoff)
    public Response<Movie> searchMovieSummary(Integer movieId) throws Exception {
        return tmdb.moviesService().summary(movieId, "zh", new AppendToResponse(AppendToResponseItem.values())).execute();
    }

    @Override
    @Retryable(value = Exception.class, recover = "recover" ,backoff = @Backoff)
    public Response<TvShowResultsPage> searchBaseTvShow(String name) throws Exception {
        SearchService searchService = tmdb.searchService();
        return searchService
                .tv(name, null, "zh", null, true)
                .execute();
    }

    @Override
    @Retryable(value = Exception.class, recover = "recover" ,backoff = @Backoff)
    public Response<TvShow> searchTvShow(Integer tvShowId) throws Exception {
        TvService tvService = tmdb.tvService();
        return tvService.tv(tvShowId, "zh").execute();
    }

    @Override
    @Retryable(value = Exception.class, recover = "recover" ,backoff = @Backoff)
    public Response<TvSeason> searchTvSeason(Integer tvShowId, Integer seasonNumber) throws Exception {
        return tmdb.tvSeasonsService().season(tvShowId, seasonNumber, "zh").execute();
    }

    @Override
    @Retryable(value = Exception.class, recover = "recover" ,backoff = @Backoff)
    public Response<Images> searchTvSeasonImages(Integer tvShowId, Integer seasonNumber) throws Exception {
        return tmdb.tvSeasonsService().images(tvShowId, seasonNumber, "zh").execute();
    }

    @Override
    @Retryable(value = Exception.class, recover = "recover" ,backoff = @Backoff)
    public Response<Credits> searchTvSeasonCredits(Integer tvShowId, Integer seasonNumber) throws Exception {
        return tmdb.tvSeasonsService().credits(tvShowId, seasonNumber).execute();
    }

    @Override
    @Retryable(value = Exception.class, recover = "recover" ,backoff = @Backoff)
    public Response<TvEpisode> searchTvEpisode(Integer tvShowId, Integer seasonNumber, Integer episodeNumber) throws Exception {
        return tmdb.tvEpisodesService().episode(tvShowId, seasonNumber, episodeNumber, "zh").execute();
    }

    @Override
    @Retryable(value = Exception.class, recover = "recover" ,backoff = @Backoff)
    public Response<Images> searchTvEpisodeImages(Integer tvShowId, Integer seasonNumber, Integer episodeNumber) throws Exception {
        return tmdb.tvEpisodesService().images(tvShowId, seasonNumber, episodeNumber).execute();
    }

    @Override
    @Retryable(value = Exception.class, recover = "recover" ,backoff = @Backoff)
    public Response<Credits> searchMovieCredits(Integer movieId) throws Exception {
        return tmdb.moviesService().credits(movieId).execute();
    }

    @Override
    @Retryable(value = Exception.class, recover = "recover" ,backoff = @Backoff)
    public Response<Credit> searchCredit(String creditId) throws Exception {
        return tmdb.creditsService().credit(creditId).execute();
    }

    @Override
    @Retryable(value = Exception.class, recover = "recover" ,backoff = @Backoff)
    public Response<Person> searchPersonSummary(Integer personId) throws Exception {
        return tmdb.personService().summary(personId, "zh").execute();
    }

    @Recover
    public void recover(Exception e, int code) {
        log.info("search error, code: {}", code, e);
    }
}
