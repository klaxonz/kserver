alter table actor
    change actor_name name varchar (255) not null comment '演员名称';

alter table actor
drop
column actor_type;

alter table image
    add source tinyint(1) null comment '来源, 0: 本地, 1: tmdb' after type;

