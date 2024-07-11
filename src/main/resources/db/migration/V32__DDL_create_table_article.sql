create table if not exists article
(
    id             bigint                             not null comment '主键 id'
    primary key,
    url            varchar(255)                       not null comment '链接',
    title          varchar(255)                       null comment '标题',
    deleted        bigint   default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint   default 0                 not null comment '版本号',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    CONSTRAINT uk_url UNIQUE (url)
) comment '文章表' row_format = DYNAMIC;