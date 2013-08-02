CREATE OR REPLACE FUNCTION public.csld_only_one_rating()
  RETURNS trigger
LANGUAGE plpgsql
AS $function$
DECLARE
  gameId int;
  userId int;
  ratingsRow RECORD;
BEGIN
  gameId := NEW.game_id;
  userId := NEW.user_id;

  select into ratingsRow count(*) as ratingAmount where game_id = gameId and user_id = userId;

  IF ratingsRow.ratingAmount > 0 THEN
     RETURN NULL;
  END IF;
  RETURN NEW;
END
$function$;