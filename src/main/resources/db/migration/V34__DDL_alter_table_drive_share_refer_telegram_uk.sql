alter table drive_share_refer_telegram
drop key uk_share_url_drive_type;

create unique index uk_share_url_drive_type
    on drive_share_refer_telegram (share_url, drive_type, chat_id, message_id);