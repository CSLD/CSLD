CREATE OR REPLACE FUNCTION public.csld_update_rating()
  RETURNS trigger
LANGUAGE plpgsql
AS $function$
DECLARE
  gameId int;
BEGIN
  IF (TG_OP = 'DELETE') THEN
    gameId := OLD.game_id;
  ELSE THEN
    gameId := NEW.game_id;
  END IF
  update csld_game set total_rating = csld_count_rating(gameId) where game_id = gameId;
END
$function$;