/** Create test users */
/** email: tester1@test.cz, heslo: test */
INSERT INTO csld_csld_user (id,password,role,name,nickname,birth_date,email,image,address,description,is_author,amount_of_comments,amount_of_played,amount_of_created,best_game_id) VALUES (1,'098f6bcd4621d373cade4e832627b4f6',1,'Jméno','Přezdívka','1990-01-11','tester1@test.cz',2,'Město','Napište něco o sobě',false,0,0,0,null);

/** Create test images */
INSERT INTO csld_image (id,path)
VALUES
        (1,'/files/img/author_icon.png'),
        (2,'/files/img/group_icon.png'),
        (3,'/files/img/icon/question_icon_game.png');

/** Create test group */
INSERT INTO csld_csld_group (id,image,name)
VALUES
        (1,2,'Testovaci skupina');

/** Create test game */
INSERT INTO csld_game (id,name,description,year,web,hours,days,players,men_role,women_role,both_role,added,video,image,added_by,total_rating,amount_of_comments,amount_of_played,amount_of_ratings)
VALUES (1,'Hra 1','Nejaky popis hry',2013,'Web',4,0,15,5,5,5,'2013-11-14 10:42:10',null,3,null,0.0,0,0,0);

/** Create binding between game and group */
INSERT INTO csld_game_has_group (id_group,id_game) VALUES (1,1);
