alter table tv_series_actor
    add tv_series_season_id bigint not null comment '电视剧季度id' after tv_series_id;

alter table tv_series_actor
    add actor_character varchar(64) null comment '饰演角色' after actor_id;

