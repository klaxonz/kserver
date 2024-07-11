alter table genre
    add constraint uk_name
        unique (name);

alter table movie_genre
    add constraint uk_movie_id_genre_id_deleted
        unique (movie_id, genre_id, deleted);

