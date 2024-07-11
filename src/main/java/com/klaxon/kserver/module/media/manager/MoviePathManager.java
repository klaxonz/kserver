package com.klaxon.kserver.module.media.manager;

import com.klaxon.kserver.module.media.model.entity.Movie;
import com.klaxon.kserver.module.media.model.entity.MoviePath;


public interface MoviePathManager {

    void deleteMoviePath(MoviePath moviePath);

    void saveMoviePath(Movie movie, Long libraryId, String path);

}
