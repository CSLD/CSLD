create table csld_group(
  id INT NOT NULL UNIQUE,
  name VARCHAR(200),
  image TEXT,

  primary key (id)
);

CREATE SEQUENCE csld_group_id_seq;

ALTER TABLE csld_group
ALTER COLUMN id
SET DEFAULT NEXTVAL('csld_group_id_seq');