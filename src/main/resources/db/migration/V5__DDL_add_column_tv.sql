alter table tv_series
    add tmdb_id bigint not null comment 'TMDB id' after id;