create table email_authentication(
  user_id int NOT NULL UNIQUE,
  mail_key text NOT NULL,

  FOREIGN KEY (user_id) REFERENCES csld_user(id) ON UPDATE CASCADE ON DELETE RESTRICT,

  PRIMARY KEY(user_id)
);