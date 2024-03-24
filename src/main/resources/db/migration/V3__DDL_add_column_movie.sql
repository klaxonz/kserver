alter table movie
    change name title varchar(255) not null comment '标题';

alter table movie
    add origin_title varchar(255) null comment '原始标题' after title;

alter table movie
    add origin_language varchar(12) null comment '原始语言' after origin_title;

alter table movie
    add backdrop_path varchar(255) null comment '背景图路径' after origin_language;

alter table movie
    add poster_path varchar(255) null comment '海报路径' after backdrop_path;

alter table movie
    add overview text null comment '简要介绍' after poster_path;

alter table movie
    add if_adult tinyint(1) default 0 not null comment '是否限制级' after overview;

