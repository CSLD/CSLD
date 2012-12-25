CREATE SEQUENCE csld_comment_id_seq;

CREATE TABLE comment (
  id int NOT NULL UNIQUE,
  user_id int NOT NULL,
  game_id INT NOT NULL,
  date DATE NOT NULL,
  text TEXT,

  FOREIGN KEY (game_id) REFERENCES game(id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY (user_id) REFERENCES csld_user(id) ON UPDATE CASCADE ON DELETE RESTRICT,

  PRIMARY KEY(id)
);

ALTER TABLE comment
ALTER COLUMN id
SET DEFAULT NEXTVAL('csld_comment_id_seq');