CREATE OR REPLACE FUNCTION public.csld_update_amount_of_comments()
  RETURNS trigger
LANGUAGE plpgsql
AS $function$
DECLARE
  gameId int;
  userId int;
BEGIN
  gameId := NEW.game_id;
  userId := NEW.user_id;

  IF (TG_OP = 'DELETE') THEN
      update csld_game set amount_of_comments = amount_of_comments - 1 where id = gameId;
      update csld_csld_user set amount_of_comments = amount_of_comments - 1 where id = userId;
  ELSIF (TG_OP = 'INSERT') THEN
      update csld_game set amount_of_comments = amount_of_comments + 1 where id = gameId;
      update csld_csld_user set amount_of_comments = amount_of_comments + 1 where id = userId;
  END IF;
END
$function$;