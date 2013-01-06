create table cookies_stored(
  user_id int not null,
  cookieValue text,

  FOREIGN KEY (user_id) REFERENCES csld_user(id) ON UPDATE CASCADE ON DELETE RESTRICT,

  PRIMARY KEY(user_id,cookieValue)
);