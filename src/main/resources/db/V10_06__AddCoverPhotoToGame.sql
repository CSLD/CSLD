alter table csld_game add COLUMN cover_photo int;

alter table csld_game add CONSTRAINT game_photo_cover_fk FOREIGN KEY (cover_photo) references csld_photo(id) MATCH SIMPLE;