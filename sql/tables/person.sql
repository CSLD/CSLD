CREATE SEQUENCE csld_person_id_seq;

create table person (
  id int NOT NULL UNIQUE,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  nickname VARCHAR(100),
  birth_date DATE,
  email VARCHAR(100),
  image VARCHAR(200),
  address TEXT,
  description TEXT,

  PRIMARY KEY(id)
);
ALTER TABLE person
ALTER COLUMN id
SET DEFAULT NEXTVAL('csld_person_id_seq');