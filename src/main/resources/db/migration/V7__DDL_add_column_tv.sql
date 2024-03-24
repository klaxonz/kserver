alter table tv_series
    modify total_season int default 0 null comment '总季数';

alter table tv_series
    modify total_episode int default 0 null comment '总集数';

