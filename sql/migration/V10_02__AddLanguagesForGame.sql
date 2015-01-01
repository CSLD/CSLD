create sequence csld_game_has_languages_id_seq;

create table csld_game_has_languages (
  id integer not null DEFAULT nextval('csld_game_has_languages_id_seq'),
  id_game int not null REFERENCES csld_game(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
  language varchar(10) not null REFERENCES csld_language(language) ON UPDATE RESTRICT ON DELETE RESTRICT,
  name VARCHAR,
  description VARCHAR,

  PRIMARY KEY (id)
);

insert into csld_game_has_languages(id_game, language, name, description) select id, lang, name, description from csld_game;

ALTER TABLE csld_game ALTER COLUMN name DROP NOT NULL;
ALTER TABLE csld_game ALTER COLUMN description DROP NOT NULL;
ALTER TABLE csld_game ALTER COLUMN lang DROP NOT NULL;