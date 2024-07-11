alter table movie_cast
drop key movie_credit_pk;

alter table movie_cast
    add constraint uk_movie_id_credit_id
        unique (tmdb_id, credit_id, deleted);

