alter table movie
drop key uk_tmdb_id;

alter table movie
    add constraint uk_tmdb_id_deleted
        unique (tmdb_id, deleted);
