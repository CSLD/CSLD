CREATE TABLE similar_games (
  id SERIAL,
  id_game1 INTEGER,
  id_game2 INTEGER,
  similarity_coefficient DOUBLE PRECISION
);

ALTER TABLE ONLY similar_games
    ADD CONSTRAINT similar_games_game_1_fk FOREIGN KEY (id_game1) REFERENCES csld_game(id) MATCH FULL;

ALTER TABLE ONLY similar_games
    ADD CONSTRAINT similar_games_game_2_fk FOREIGN KEY (id_game2) REFERENCES csld_game(id) MATCH FULL;