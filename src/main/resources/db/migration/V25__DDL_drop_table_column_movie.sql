
alter table movie
drop column library_id;

alter table movie
drop column path;

alter table movie
    add runtime int not null comment '时长' after release_date;


create table if not exists credit
(
    id             bigint                               not null comment '主键id'
    primary key,
    credit_id      varchar(64)                          not null comment 'tmdb的credit_id',
    person_id      int                                  null comment 'tmdb的person_id',
    `name`           varchar(64)                          not null comment '名称',
    department     varchar(255)                         not null,
    job            varchar(64)                          null,
    profile_path   varchar(255)                         null comment '头像',
    is_adult       tinyint(1) default 0                 not null comment '是否成人，0：否，1：是',
    deleted        bigint     default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint     default 0                 not null comment '版本号',
    create_time    datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
    ) comment '职员表';


create table if not exists movie_credit
(
    id             bigint                             not null comment '主键id'
    primary key,
    movie_id       bigint                             not null comment '电影id',
    tmdb_id        bigint                             not null comment 'tmdb的id',
    credit_id      varchar(64)                        not null comment 'tmdb的credit_id',
    person_id      int                                null comment 'tmdb的person_id',
    `name`           varchar(64)                        not null comment '名称',
    `character`      varchar(64)                        not null comment '角色',
    profile_path   varchar(255)                       null comment '头像',
    `order`          int                                not null comment '顺序',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
    ) comment '电影职员表';


alter table genre
    add tmdb_id int not null comment 'tmdb_id' after id;

alter table movie_genre
    add name varchar(64) not null comment '类型' after genre_id;