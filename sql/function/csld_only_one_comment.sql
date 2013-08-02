CREATE OR REPLACE FUNCTION public.csld_only_one_comment()
  RETURNS trigger
LANGUAGE plpgsql
AS $function$
DECLARE
  gameId int;
  userId int;
  commentsRow RECORD;
BEGIN
  gameId := NEW.game_id;
  userId := NEW.user_id;

  select into commentsRow count(*) as commentAmount from comment where game_id = gameId and user_id = userId;

  IF commentsRow.commentAmount > 0 THEN
     RETURN NULL;
  END IF;
  RETURN NEW;
END
$function$;