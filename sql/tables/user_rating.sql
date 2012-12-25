--- Table containing ratings.
CREATE SEQUENCE csld_user_rating_id_seq;

create table user_rating (
  id INT NOT NULL UNIQUE,
  user_id INT NOT NULL,
  rating INT NOT NULL,
  description TEXT,

  FOREIGN KEY (user_id) REFERENCES csld_user(id) ON UPDATE CASCADE ON DELETE RESTRICT,

  PRIMARY KEY(id)
);

ALTER TABLE user_rating
ALTER COLUMN id
SET DEFAULT NEXTVAL('csld_user_rating_id_seq');