alter table actor
    change sex gender tinyint(1) default 0 not null comment '性别，0 为未知，1为女，2 为男';
