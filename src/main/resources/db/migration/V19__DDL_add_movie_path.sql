create table if not exists movie_path
(
    id             bigint                             not null comment '主键 id'
    primary key,
    movie_id       bigint                             not null comment '电视剧 id',
    `path`         varchar(1024)                      not null comment '路径',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '电影路径表' row_format = DYNAMIC;