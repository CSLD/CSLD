CREATE SEQUENCE csld_resource_id_seq;

CREATE TABLE resource (
  id INT NOT NULL UNIQUE,
  path VARCHAR(200) NOT NULL,
  default_path TEXT NOT NULL,

  PRIMARY KEY(id)
);

ALTER TABLE resource
ALTER COLUMN id
SET DEFAULT NEXTVAL('csld_resource_id_seq');