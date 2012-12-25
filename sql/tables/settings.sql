CREATE SEQUENCE csld_settings_id_seq;

create table settings (
  id INT NOT NULL UNIQUE,
  user_id INT NOT NULL,
  name VARCHAR(50),
  fvaluetext TEXT,
  fvaludate DATE,
  fvaluenumr DOUBLE PRECISION,

  FOREIGN KEY (user_id) REFERENCES csld_user(id) ON UPDATE CASCADE ON DELETE RESTRICT,

  PRIMARY KEY(id)
);

ALTER TABLE settings
ALTER COLUMN id
SET DEFAULT NEXTVAL('csld_settings_id_seq');