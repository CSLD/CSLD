alter table csld_user_played_game drop CONSTRAINT csld_user_played_game_pkey;
alter table csld_user_played_game alter column user_id drop not null;
alter table csld_user_played_game alter column game_id drop not null;

alter table csld_rating drop constraint csld_rating_pkey;
alter table csld_rating alter COLUMN user_id drop not null;
alter table csld_rating alter COLUMN game_id drop not null;
