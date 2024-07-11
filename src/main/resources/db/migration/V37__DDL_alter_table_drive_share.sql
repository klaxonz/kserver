alter table drive_share
    add scrape_status int default 1 not null comment '刮削状态，1：未刮削，2：已刮削，3：分享过期，4：被封禁' after drive_type;

