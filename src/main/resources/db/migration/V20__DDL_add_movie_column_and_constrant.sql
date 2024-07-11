alter table movie
    add tmdb_id bigint not null comment 'tmdb id' after library_id;

alter table movie
    add constraint uk_tmdb_id
        unique (tmdb_id);