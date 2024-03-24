package com.klaxon.kserver.module.media.service;

import com.uwetrottmann.tmdb2.entities.Credit;
import com.uwetrottmann.tmdb2.entities.Credits;
import com.uwetrottmann.tmdb2.entities.Images;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;
import com.uwetrottmann.tmdb2.entities.Person;
import com.uwetrottmann.tmdb2.entities.TvEpisode;
import com.uwetrottmann.tmdb2.entities.TvSeason;
import com.uwetrottmann.tmdb2.entities.TvShow;
import com.uwetrottmann.tmdb2.entities.TvShowResultsPage;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import retrofit2.Response;


public interface TmdbSearchService {

    Response<MovieResultsPage> searchMovie(String name) throws Exception;

    Response<TvShowResultsPage> searchBaseTvShow(String name) throws Exception;

    Response<TvShow> searchTvShow(Integer tvShowId) throws Exception;

    Response<TvSeason> searchTvSeason(Integer tvShowId, Integer seasonNumber) throws Exception;

    Response<Images> searchTvSeasonImages(Integer tvShowId, Integer seasonNumber) throws Exception;

    Response<Credits> searchTvSeasonCredits(Integer tvShowId, Integer seasonNumber) throws Exception;

    Response<TvEpisode> searchTvEpisode(Integer tvShowId, Integer seasonNumber, Integer episodeNumber) throws Exception;

    Response<Images> searchTvEpisodeImages(Integer tvShowId, Integer seasonNumber, Integer episodeNumber) throws Exception;

    Response<Credits> searchMovieCredits(Integer movieId) throws Exception;

    Response<Credit> searchCredit(String creditId) throws Exception;

    Response<Person> searchPersonSummary(Integer personId) throws Exception;

}
