insert into csld_image (path) select image from person;
insert into csld_image (path) select image from game;
insert into csld_image (path) select image from csld_group;
delete from csld_image a where a.ctid <> (select min(b.ctid)
  from csld_image b
  where a.path = b.path
);

insert into csld_csld_user (id, password, role, name, nickname, birth_date, email, image, address,
  description)
  select person.id, coalesce(usr.password, '1246854gfdgdr'), coalesce(usr.role_id, 1), person.first_name || ' ' || person.last_name,
  person.nickname, person.birth_date, person.email, img.id, person.address, person.description
  from person
    left join csld_user as usr on person.id = usr.id
      left join csld_image as img on person.image = img.path;

insert into csld_game (id, name, description, year, web, hours, days, players, men_role,
  women_role, both_role, added, image, added_by)
  select game.id, game.name, game.description, game.year, game.web, game.hours, game.days,
    game.players, game.men_role, game.women_role, game.both_role, game.added, img.id,
    CASE WHEN game.user_who_added=-1 then 1 else game.user_who_added END
  from game
    left join csld_image as img on game.image = img.path;

insert into csld_label (id, name, description, is_required, is_authorized, added_by)
  select label.id, label.name, label.description, label.requires, label.is_authorized,
  CASE WHEN label.user_id=-1 then 1 ELSE label.user_id END
  from label;

insert into csld_comment (user_id, game_id, comment, added)
  select user_id, game_id, text, time from comment;

insert into csld_csld_group(id, name)
  select id, name from csld_group;

delete from game_has_authors a where a.ctid <> (select min(b.ctid)
  from game_has_authors b
  where a.game_id = b.game_id and a.author_id = b.author_id
);
insert into csld_game_has_author (id_user, id_game)
  select author.person_id, gha.game_id from game_has_authors as gha
    left join author on gha.author_id = author.author_id;


delete from game_has_labels a where a.ctid <> (select min(b.ctid)
  from game_has_labels b
  where a.game_id = b.game_id and a.label_id = b.label_id
);
insert into csld_game_has_label(id_label, id_game)
  select label_id, game_id from game_has_labels;

insert into csld_group_has_members(group_id, user_id)
  select ghm.group_id, author.person_id from author_has_group as ghm
    left join author on ghm.author_id = author.author_id;


delete from rating a where a.ctid <> (select min(b.ctid)
  from rating b
  where a.game_id = b.game_id and a.user_id = b.user_id
);
insert into csld_rating (user_id, game_id, rating)
  select user_id, game_id, rating from rating;

insert into csld_user_played_game (user_id, game_id, state)
  select user_id, game_id, '2' from user_played_game;


-- insert into all new tables

--- Dropping unnecessary tables and views. ---
drop view test;
drop view test_nahled;

drop table role_has_resource ;
drop table resource;
drop table cookies_stored ;
drop table settings;
drop table user_rating;
drop table author_has_group;
drop table game_has_authors;
drop table author;
drop table game_has_labels;
drop table email_authentication;
drop table user_played_game;
drop table label;
drop table rating;
drop table csld_group;
drop table comment;
drop table game;
drop table csld_user;
drop table role;
drop table person;

drop sequence csld_role_id_seq;
drop SEQUENCE csld_user_rating_id_seq;
drop SEQUENCE csld_game_has_labels_id_seq;
drop sequence csld_author_id_seq ;
drop sequence csld_game_has_authors_id_seq ;
drop sequence csld_settings_id_seq;
drop sequence csld_resource_id_seq;
drop sequence csld_comment_id_seq;
drop sequence csld_rating_id_seq;

update csld_game set total_rating = csld_count_rating(id);
update csld_game set amount_of_comments = csld_amount_of_comments(id);
update csld_game set amount_of_played = csld_amount_of_played(id);
update csld_game set amount_of_ratings = csld_amount_of_ratings(id);

update csld_csld_user set amount_of_comments = csld_user_amount_of_comments(id);
update csld_csld_user set amount_of_played = csld_user_amount_of_played(id);
update csld_csld_user set amount_of_created = csld_count_games_of_author(id);

update csld_csld_user set role = 1 where role = 2;
update csld_csld_user set role = 2 where role = 4;
update csld_csld_user set role = 3 where role = 5;
update csld_csld_user set best_game_id = csld_count_best_game(id);
delete from csld_csld_user where email = ''
  and amount_of_created = 0 and id not in(select user_id from csld_group_has_members);
update csld_csld_user set email = id || nickname || '@larpovadatabaze.cz' where email = '';

insert into csld_image (path) values ('/files/img/author_icon.png');
insert into csld_image (path) values ('/files/img/group_icon.png');
insert into csld_image (path) values ('/files/img/icon/question_icon_game.png');

update csld_game set image = (select id from csld_image where path = '/files/img/icon/question_icon_game.png') where (image is null or image = (select id from csld_image where path=''));
update csld_csld_group set image = (select id from csld_image where path = '/files/img/group_icon.png') where (image is null or image = (select id from csld_image where path=''));
update csld_csld_user set image = (select id from csld_image where path = '/files/img/author_icon.png') where (image is null or image = (select id from csld_image where path=''));