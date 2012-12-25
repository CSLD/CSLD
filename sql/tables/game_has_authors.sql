CREATE SEQUENCE csld_game_has_authors_id_seq;

CREATE TABLE game_has_authors (
  id int NOT NULL UNIQUE,
  game_id int NOT NULL,
  author_id int NOT NULL,

  FOREIGN KEY (game_id) REFERENCES game(id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY (author_id) REFERENCES author(author_id) ON UPDATE CASCADE ON DELETE RESTRICT,

  PRIMARY KEY(id)
);

ALTER TABLE game_has_authors
ALTER COLUMN id
SET DEFAULT NEXTVAL('csld_game_has_authors_id_seq');