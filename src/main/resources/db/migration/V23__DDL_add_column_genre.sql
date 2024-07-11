alter table movie_genre
    add genre_name varchar(64) not null comment '类型名称' after genre_id;

