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
  update csld_game set total_rating = csld_count_rating(gameId) where id = gameId;
  update csld_game set average_rating = (select avg(rating) from csld_rating where csld_rating.game_id=gameId)*10;
  RETURN NEW;
END
$function$;
