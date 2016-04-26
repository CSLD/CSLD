CREATE OR REPLACE FUNCTION public.csld_update_rating()
  RETURNS trigger
LANGUAGE plpgsql
AS $function$
DECLARE
  eventId int;
BEGIN
  IF (TG_OP = 'DELETE') THEN
    eventId := OLD.game_id;
  ELSE
    eventId := NEW.game_id;
  END IF;
  update csld_game set total_rating = csld_count_rating(eventId) where game_id = eventId;
  update csld_game set average_rating = (select avg(rating) from csld_rating where csld_rating.game_id=eventId)*10 where game_id = eventId;
END
$function$;

update csld_game set average_rating = (select avg(rating) from csld_rating where csld_rating.game_id=csld_game.id)*10;
update csld_game set average_rating = 0 where average_rating is null;
