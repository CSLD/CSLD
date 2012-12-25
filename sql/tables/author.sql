CREATE TABLE author (
  author_id INT NOT NULL UNIQUE,
  person_id INT NOT NULL UNIQUE,

  FOREIGN KEY (person_id) REFERENCES person(id) ON UPDATE CASCADE ON DELETE RESTRICT,

  PRIMARY KEY(author_id)
);

CREATE SEQUENCE csld_author_id_seq;

ALTER TABLE author
ALTER COLUMN author_id
SET DEFAULT NEXTVAL('csld_author_id_seq');
