CREATE SEQUENCE csld_role_id_seq;

CREATE TABLE role (
  id INT NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,

  PRIMARY KEY(id)
);

ALTER TABLE role
ALTER COLUMN id
SET DEFAULT NEXTVAL('csld_role_id_seq');