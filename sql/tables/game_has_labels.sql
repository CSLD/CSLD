CREATE SEQUENCE csld_game_has_labels_id_seq;

CREATE TABLE game_has_labels (
  id int NOT NULL UNIQUE,
  game_id int NOT NULL,
  label_id int NOT NULL,

  FOREIGN KEY (game_id) REFERENCES game(id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY (label_id) REFERENCES label(id) ON UPDATE CASCADE ON DELETE RESTRICT,

  PRIMARY KEY(id)
);

ALTER TABLE game_has_labels
ALTER COLUMN id
SET DEFAULT NEXTVAL('csld_game_has_labels_id_seq');