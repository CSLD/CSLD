create table user_played_game(
  user_id INT NOT NULL,
  game_id INT NOT NULL,

  FOREIGN KEY (user_id) REFERENCES csld_user(id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY (game_id) REFERENCES game(id) ON UPDATE CASCADE ON DELETE RESTRICT,

  PRIMARY KEY(user_id, game_id)
);