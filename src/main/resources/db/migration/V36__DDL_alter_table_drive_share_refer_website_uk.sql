alter table drive_share_refer_website
drop key uk_share_url_drive_type;

alter table drive_share_refer_website
    add constraint uk_share_url_drive_type
        unique (share_url, refer_url, drive_type);

