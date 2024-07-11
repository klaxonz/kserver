alter table movie_credit
drop column person_id;

alter table movie_credit
    add constraint movie_credit_pk
        unique (tmdb_id, deleted);

rename table movie_credit to movie_cast;

alter table movie_path
    add library_id bigint not null comment 'media_library.id' after movie_id;

