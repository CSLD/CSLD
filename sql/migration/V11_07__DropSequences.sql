alter table csld_email_authentication alter column id drop default;
drop SEQUENCE csld_email_authentication_id_seq;
alter table csld_game_has_languages alter column id drop default;
drop SEQUENCE csld_game_has_languages_id_seq;
alter table csld_game alter column id drop default;
drop SEQUENCE csld_game_id_seq;
alter table csld_label alter column id drop default;
drop SEQUENCE csld_game_label_id_seq;
alter table csld_group_has_languages alter column id drop default;
drop SEQUENCE csld_group_has_languages_id_seq;
alter table csld_csld_group alter column id drop default;
drop SEQUENCE csld_group_id_seq;
alter table csld_image alter column id drop default;
drop SEQUENCE csld_image_id_seq;
alter table csld_label_has_languages alter column id drop default;
drop SEQUENCE csld_label_has_languages_id_seq;
alter table csld_news alter column id drop default;
drop SEQUENCE csld_news_id_seq;
alter table csld_csld_user alter column id drop default;
drop SEQUENCE csld_person_id_seq;
alter table csld_photo alter column id drop default;
drop SEQUENCE csld_photo_id_seq;
alter table csld_user_has_languages alter column id drop default;
drop SEQUENCE csld_user_has_languages_id_seq;
alter table csld_video alter column id drop default;
drop SEQUENCE csld_video_id_seq;