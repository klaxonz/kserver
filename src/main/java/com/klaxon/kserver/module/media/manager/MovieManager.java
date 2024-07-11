package com.klaxon.kserver.module.media.manager;


import com.klaxon.kserver.module.media.model.entity.Movie;

public interface MovieManager {

    Movie saveMovie(com.uwetrottmann.tmdb2.entities.Movie movie);

}
