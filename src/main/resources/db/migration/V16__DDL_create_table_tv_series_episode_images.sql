create table if not exists tv_series_episode_image
(
    id                   bigint                               not null comment '主键 id'
    primary key,
    tv_series_id         bigint                               not null comment '电视剧 id',
    tv_series_season_id  bigint                               not null comment '电视剧季度 id',
    tv_series_episode_id bigint                               not null comment '电视剧季度 id',
    image_type           tinyint(1) default 0                 not null comment '图片类型: 0: backdrop, 1: poster, 2: still',
    image_id             bigint                               not null comment '图片 id',
    deleted              bigint     default 0                 not null comment '删除时间，0 为未删除',
    object_version       bigint     default 0                 not null comment '版本号',
    create_time          datetime   default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time          datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
    ) comment '电视剧集背景图片表' row_format = DYNAMIC;