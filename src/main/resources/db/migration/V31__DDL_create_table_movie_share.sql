create table if not exists movie_share
(
    id             bigint                             not null comment '主键 id'
    primary key,
    movie_id       bigint                             not null comment '电影 id',
    share_url      varchar(255)                       not null comment '分享链接',
    referer_url    varchar(255)                       null     comment '来源链接',
    frame_rate     int                                null     comment '帧率',
    bit_rate       int                                null     comment '比特率',
    resolution     varchar(255)                       null     comment '分辨率',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '电影分享表' row_format = DYNAMIC;