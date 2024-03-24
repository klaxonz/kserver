alter table tv_series
    add library_id bigint not null comment '媒体库 id' after tmdb_id;