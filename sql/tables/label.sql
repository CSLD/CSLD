CREATE SEQUENCE csld_game_label_id_seq;

create table label (
  id INT NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  description TEXT NOT NULL,
  requires boolean,
  is_authorized boolean DEFAULT TRUE,
  user_id int DEFAULT -1,

  PRIMARY KEY(id)
);


ALTER TABLE label
ALTER COLUMN id
SET DEFAULT NEXTVAL('csld_game_label_id_seq');