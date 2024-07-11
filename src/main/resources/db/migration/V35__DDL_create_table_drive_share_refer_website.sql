create table if not exists drive_share_refer_website
(
    id             bigint                               not null comment '主键 id'
    primary key,
    share_id       bigint                               not null comment 'drive_share.id',
    share_url      varchar(255)                         not null comment '链接',
    refer_url      varchar(255)                         not null comment '来源链接',
    drive_type     tinyint(1) default 1                 not null comment '1: 阿里云盘',
    deleted        bigint     default 0                 not null comment '删除时间，0 为未删除',
    object_version bigint     default 0                 not null comment '版本号',
    create_time    datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    CONSTRAINT uk_share_url_drive_type UNIQUE (share_url, drive_type)
    ) comment '网站云盘资源分享表' row_format = DYNAMIC;