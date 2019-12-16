alter table csld_game_has_languages drop CONSTRAINT csld_game_has_languages_language_fkey;
alter table csld_label_has_languages drop constraint csld_label_has_languages_language_fkey;
alter table csld_user_has_languages drop CONSTRAINT csld_user_has_languages_language_fkey;

create sequence csld_user_has_languages_id_seq;

/* TODO: update all records in the table to contain correct ids. */
alter table csld_user_has_languages add column id integer not null DEFAULT nextval('csld_user_has_languages_id_seq');

