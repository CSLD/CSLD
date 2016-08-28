alter table csld_game add column name text;
alter table csld_game add column description text;

alter table csld_label add column name text;
alter table csld_label add column description text;

alter table csld_csld_group add column name text;

update csld_game set name = csld_game_has_languages.name FROM csld_game_has_languages WHERE language = 'cs' and csld_game.id = csld_game_has_languages.id_game;
update csld_game set description = csld_game_has_languages.description FROM csld_game_has_languages WHERE language = 'cs' and csld_game.id = csld_game_has_languages.id_game;

update csld_label set name = csld_label_has_languages.name FROM csld_label_has_languages WHERE language = 'cs' and csld_label.id = csld_label_has_languages.id_label;
update csld_label set description = csld_label_has_languages.description FROM csld_label_has_languages WHERE language = 'cs' and csld_label.id = csld_label_has_languages.id_label;

update csld_csld_group set name = csld_group_has_languages.name FROM csld_group_has_languages WHERE language = 'cs' and csld_csld_group.id = csld_group_has_languages.id_group;

drop table csld_game_has_languages;
drop table csld_label_has_languages;
drop table csld_group_has_languages;

delete from csld_photo where game in (select id from csld_game where name is NULL);
delete from csld_game_has_author where id_game in (select id from csld_game where name is NULL);
delete from csld_game_has_group where id_game in (select id from csld_game where name is NULL);
delete from csld_game_has_label where id_game in (select id from csld_game where name is NULL);
delete from csld_comment where game_id  in (select id from csld_game where name is NULL);
delete from csld_rating where game_id  in (select id from csld_game where name is NULL);
delete from csld_user_played_game where game_id  in (select id from csld_game where name is NULL);
update csld_csld_user set best_game_id = null where best_game_id in (select id from csld_game where name is NULL);
delete from csld_game where name is NULL;
delete from csld_label where name is NULL;
delete from csld_csld_group where name is NULL;

alter table csld_comment drop column lang;