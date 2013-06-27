CREATE OR REPLACE FUNCTION public.csld_count_user_rating(userId integer)
  RETURNS integer
LANGUAGE plpgsql
AS $function$
DECLARE
  commentsRow RECORD;
  ratingsRow RECORD;
  playedRow RECORD;
  result integer;
BEGIN
  select into commentsRow count(*) as count from comment where user_id = userId;
  select into ratingsRow count(*) as count from rating where user_id = userId;
  select into playedRow count(*) as count from user_played_game where user_id = userId;

  result := playedRow.count + (ratingsRow.count * 2) + (commentsRow.count * 4);
  return result;
END;
$function$;