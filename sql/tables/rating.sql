CREATE SEQUENCE csld_rating_id_seq;

CREATE TABLE rating (
  id int NOT NULL UNIQUE,
  game_id int NOT NULL,
  user_id int NOT NULL,
  rating int NOT NULL,

  FOREIGN KEY (game_id) REFERENCES game(id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY (user_id) REFERENCES csld_user(id) ON UPDATE CASCADE ON DELETE RESTRICT,

  PRIMARY KEY(id)
);

ALTER TABLE rating
ALTER COLUMN id
SET DEFAULT NEXTVAL('csld_rating_id_seq');