alter table csld_game add average_rating double precision;

update csld_game set average_rating = (select avg(rating) from csld_rating where csld_rating.game_id=csld_game.id)*10;
update csld_game set average_rating = 0 where average_rating is null;

CREATE OR REPLACE FUNCTION public.csld_update_rating()
  RETURNS trigger
LANGUAGE plpgsql
AS $function$
DECLARE
  gameId int;
BEGIN
  IF (TG_OP = 'DELETE') THEN
    gameId := OLD.game_id;
  ELSE
    gameId := NEW.game_id;
  END IF;
  update csld_game set total_rating = csld_count_rating(gameId) where game_id = gameId;
  update csld_game set average_rating = (select avg(rating) from csld_rating where csld_rating.game_id=gameId)*10;
END
$function$;
