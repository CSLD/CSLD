create SEQUENCE csld_comment_id_seq;
create SEQUENCE csld_rating_id_seq;
create SEQUENCE csld_group_has_members_id_seq;
create SEQUENCE csld_user_played_game_id_seq;

/* Create sequence update with correct ids */
alter table csld_comment add column id INTEGER NOT NULL DEFAULT nextval('csld_comment_id_seq'::REGCLASS);
alter table csld_rating add COLUMN id INTEGER NOT NULL DEFAULT nextval('csld_rating_id_seq'::REGCLASS);
alter table csld_group_has_members ADD COLUMN id INTEGER NOT NULL DEFAULT nextval('csld_group_has_members_id_seq'::REGCLASS);
alter table csld_user_played_game ADD COLUMN id INTEGER NOT NULL DEFAULT nextval('csld_user_played_game_id_seq'::REGCLASS);