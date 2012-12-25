CREATE SEQUENCE csld_game_id_seq;

CREATE TABLE game(
  id int NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  year int,
  description TEXT NOT NULL,
  image text,
  men_role int,
  women_role int,
  hours int,
  days int,
  players int,
  premier DATE,
  added TIMESTAMP not null default CURRENT_TIMESTAMP,

  PRIMARY KEY(id)
);

ALTER TABLE game
ALTER COLUMN id
SET DEFAULT NEXTVAL('csld_game_id_seq');