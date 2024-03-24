alter table tv_series
    change tv_series_name title varchar (255) not null comment '电视剧名称';

alter table tv_series
    add origin_title varchar(255) null comment '原始名称' after title;

alter table tv_series
    add origin_language varchar(12) null comment '原始语言' after origin_title;

alter table tv_series
    add backdrop_path varchar(255) null comment '背景图路径' after origin_language;

alter table tv_series
    add poster_path varchar(255) null comment '海报路径' after backdrop_path;

alter table tv_series
    add overview text null comment '简要介绍' after poster_path;

alter table tv_series
    add if_adult tinyint(1) default 0 not null comment '是否限制级' after overview;


create table tv_series_season
(
    id              bigint                             not null comment '主键 id'
        primary key,
    library_id      bigint                             not null comment '媒体库 id',
    tv_series_id    bigint                             not null comment '电视剧 id',
    title           varchar(255)                       not null comment '标题',
    origin_title    varchar(255) null comment '原始标题',
    origin_language varchar(12) null comment '原始语言',
    backdrop_path   varchar(255) null comment '背景图路径',
    poster_path     varchar(255) null comment '海报路径',
    season          int                                not null comment '季数',
    episode_count   int                                not null comment '总集数',
    overview        text null comment '简要介绍',
    if_adult        tinyint(1) default 0 not null comment '是否限制级',
    release_date    date                               not null comment '发行日期',
    deleted         bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version  bigint   default 0                 not null comment '版本号',
    create_time     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '电视剧季度表' row_format = DYNAMIC;

create table tv_series_episode
(
    id                  bigint                             not null comment '主键 id'
        primary key,
    library_id          bigint                             not null comment '媒体库 id',
    tv_series_id        bigint                             not null comment '电视剧 id',
    tv_series_season_id bigint                             not null comment '电视剧季度 id',
    title               varchar(255)                       not null comment '标题',
    origin_title        varchar(255) null comment '原始标题',
    origin_language     varchar(12) null comment '原始语言',
    backdrop_path       varchar(255) null comment '背景图路径',
    poster_path         varchar(255) null comment '海报路径',
    path                varchar(1024)                      not null comment '路径',

    season              int                                not null comment '季数',
    episode             int                                not null comment '集数',
    overview            text null comment '简要介绍',
    if_adult            tinyint(1) default 0 not null comment '是否限制级',
    release_date        date                               not null comment '发行日期',
    deleted             bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version      bigint   default 0                 not null comment '版本号',
    create_time         datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time         datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '电视剧集数表' row_format = DYNAMIC;