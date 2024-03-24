create table if not exists media_library_directory
(
    id             bigint                             not null comment '主键 id'
        primary key,
    library_id     bigint                             not null comment '媒体库 id',
    path           varchar(1024)                      not null comment '路径',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '媒体库目录表' row_format = DYNAMIC;

create table if not exists movie
(
    id             bigint                             not null comment '主键 id'
        primary key,
    library_id     bigint                             not null comment '媒体库 id',
    path           varchar(1024)                      not null comment '路径',
    name           varchar(255)                       not null comment '名称',
    release_date   date                               not null comment '发行日期',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '电影表' row_format = DYNAMIC;


create table if not exists movie_actor
(
    id             bigint                             not null comment '主键 id'
        primary key,
    movie_id       bigint                             not null comment '电影 id',
    actor_id       bigint                             not null comment '演员 id',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '演员表' row_format = DYNAMIC;

create table if not exists actor
(
    id             bigint                             not null comment '主键 id'
        primary key,
    actor_name     varchar(255)                       not null comment '演员名称',
    actor_type     varchar(255)                       not null comment '演员类型',
    sex            tinyint(1) default 0 not null comment '性别，0 为未知，1 为男，2 为女',
    birthdate      date                               not null comment '出生日期',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '演员表' row_format = DYNAMIC;

create table if not exists actor_image
(
    id             bigint                             not null comment '主键 id'
        primary key,
    actor_id       bigint                             not null comment '演员 id',
    image_id       bigint                             not null comment '演员图片 id',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '演员图片表' row_format = DYNAMIC;

create table if not exists image
(
    id             bigint                             not null comment '主键 id'
        primary key,
    url            varchar(1024)                      not null comment '路径',
    type           tinyint(1) default 0 not null comment '图片类型，0 为未知，1 为封面，2 为海报',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '图片表' row_format = DYNAMIC;

create table if not exists director
(
    id             bigint                             not null comment '主键 id'
        primary key,
    director_name  varchar(255)                       not null comment '导演名称',
    sex            tinyint(1) default 0 not null comment '性别，0 为未知，1 为男，2 为女',
    birthdate      date                               not null comment '出生日期',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '导演表' row_format = DYNAMIC;

create table if not exists movie_director
(
    id             bigint                             not null comment '主键 id'
        primary key,
    movie_id       bigint                             not null comment '电影 id',
    director_id    bigint                             not null comment '导演 id',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '电影导演表' row_format = DYNAMIC;

create table if not exists director_image
(
    id             bigint                             not null comment '主键 id'
        primary key,
    director_id    bigint                             not null comment '导演 id',
    image_id       bigint                             not null comment '导演图片 id',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '导演图片表' row_format = DYNAMIC;

create table if not exists tv_series
(
    id             bigint                             not null comment '主键 id'
        primary key,
    tv_series_name varchar(255)                       not null comment '电视剧名称',
    release_date   date                               not null comment '发行日期',
    total_season   tinyint(1) default 0 not null comment '总季数',
    total_episode  tinyint(1) default 0 not null comment '总集数',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '电视剧表' row_format = DYNAMIC;

create table if not exists tv_series_resource
(
    tv_series_id   bigint                             not null comment '电视剧 id',
    library_id     bigint                             not null comment '资源 id',
    path           varchar(1024)                      not null comment '路径',
    season         tinyint(1) default 0 not null comment '季数',
    episode        tinyint(1) default 0 not null comment '集数',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '电视剧资源表' row_format = DYNAMIC;

create table if not exists tv_series_actor
(
    id             bigint                             not null comment '主键 id'
        primary key,
    tv_series_id   bigint                             not null comment '电视剧 id',
    actor_id       bigint                             not null comment '演员 id',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '电视剧演员表' row_format = DYNAMIC;

create table if not exists tv_series_director
(
    id             bigint                             not null comment '主键 id'
        primary key,
    tv_series_id   bigint                             not null comment '电视剧 id',
    director_id    bigint                             not null comment '导演 id',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '电视剧导演表' row_format = DYNAMIC;

create table if not exists music
(
    id             bigint                             not null comment '主键 id'
        primary key,
    music_name     varchar(255)                       not null comment '音乐名称',
    release_date   date                               not null comment '发行日期',
    path           varchar(1024)                      not null comment '路径',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '音乐表' row_format = DYNAMIC;

create table if not exists singer
(
    id             bigint                             not null comment '主键 id'
        primary key,
    singer_name    varchar(255)                       not null comment '歌手名称',
    sex            tinyint(1) default 0 not null comment '性别，0 为未知，1为男，2为女',
    birthdate      date                               not null comment '出生日期',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '歌手表' row_format = DYNAMIC;

create table if not exists music_singer
(
    id             bigint                             not null comment '主键 id'
        primary key,
    music_id       bigint                             not null comment '音乐 id',
    singer_id      bigint                             not null comment '歌手 id',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '音乐歌手表' row_format = DYNAMIC;

create table if not exists book
(
    id             bigint                             not null comment '主键 id'
        primary key,
    book_name      varchar(255)                       not null comment '书籍名称',
    author_id      bigint                             not null comment '作者 id',
    publisher_id   bigint                             not null comment '出版社 id',
    isbn           varchar(255)                       not null comment 'isbn',
    publish_date   date                               not null comment '出版日期',
    path           varchar(1024)                      not null comment '路径',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '书籍表' row_format = DYNAMIC;

create table if not exists book_author
(
    id             bigint                             not null comment '主键 id'
        primary key,
    author_name    varchar(255)                       not null comment '作者名称',
    sex            tinyint(1) default 0 not null comment '性别，0 为未知，1为男，2为女',
    birthdate      date                               not null comment '出生日期',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '书籍作者表' row_format = DYNAMIC;

create table if not exists book_publisher
(
    id             bigint                             not null comment '主键 id'
        primary key,
    publisher_name varchar(255)                       not null comment '出版社名称',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '书籍出版社表' row_format = DYNAMIC;