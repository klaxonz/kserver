alter table movie_actor
    add `character` varchar(64) null comment '饰演角色' after actor_id;