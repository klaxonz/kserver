create table account
(
    id             bigint                             not null comment '主键 id'
        primary key,
    username       varchar(32)                        not null comment '用户名',
    password       varchar(255)                       not null comment '密码',
    email          varchar(32)                        not null comment '电子邮箱',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '账号表' row_format = DYNAMIC;

create table web_page
(
    id             bigint unsigned                    not null comment '主键id'
        primary key,
    user_id        bigint                             not null comment '用户ID',
    url            text                               not null comment '链接',
    title          text                               not null comment '标题',
    content        text                               not null comment '网页内容',
    source         text                               not null comment '来源',
    favicon        text                               null comment '网站icon',
    description    text                               not null comment '描述',
    deleted        bigint   default 0                 not null comment '删除时间',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '网页表' row_format = DYNAMIC;

create table media_library
(
    id             bigint                             not null
        primary key,
    url            varchar(255) charset utf8mb4       not null comment '媒体库地址',
    name           varchar(255) charset utf8mb4       not null comment '媒体库名称',
    username       varchar(32) charset utf8mb4        not null comment '用户名',
    password       varchar(255) charset utf8mb4       not null comment '密码',
    deleted        bigint   default 0                 not null comment '删除时间',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '媒体库表';

