CREATE OR REPLACE FUNCTION public.csld_countComments(gameId integer)
  RETURNS integer
LANGUAGE plpgsql
AS $function$
DECLARE
  commentsRow RECORD;
BEGIN
  select into commentsRow count(*) as commentCount from comment where game_id = gameId;
  return commentsRow.commentCount;
END;
$function$;