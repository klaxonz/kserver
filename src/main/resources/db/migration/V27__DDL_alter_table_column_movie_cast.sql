alter table movie_cast
    change `character` character_name varchar(64) not null comment '角色';

alter table movie_cast
    change `order` orderNo int not null comment '顺序';