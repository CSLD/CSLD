alter table csld_comment drop CONSTRAINT csld_comment_pkey;

alter table csld_comment alter column user_id drop not null;
alter table csld_comment alter column game_id drop not null;