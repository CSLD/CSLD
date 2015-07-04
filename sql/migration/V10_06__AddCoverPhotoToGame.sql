alter table csld_game add COLUMN cover_image int;

alter table csld_game add CONSTRAINT game_cover_image_fk FOREIGN KEY (cover_image) references csld_image(id) MATCH SIMPLE;