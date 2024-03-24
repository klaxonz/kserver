alter table tv_series_season
drop column origin_title;

alter table tv_series_season
drop column origin_language;

alter table tv_series_season
drop column backdrop_path;

alter table tv_series_season
drop column poster_path;

alter table tv_series_episode
drop column origin_title;

alter table tv_series_episode
drop column origin_language;

alter table tv_series_episode
drop column backdrop_path;

alter table tv_series_episode
drop column poster_path;

drop table if exists tv_series_resource;

