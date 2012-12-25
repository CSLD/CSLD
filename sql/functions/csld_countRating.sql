CREATE OR REPLACE FUNCTION public.csld_countRating(gameid integer)
  RETURNS double precision
LANGUAGE plpgsql
AS $function$
DECLARE
  ratingsRow RECORD;
  result double precision;
  average RECORD;
BEGIN
  select into ratingsRow sum(rating) as ratingCount, count(*) as ratingAmount from rating where game_id = gameId;
  if ratingsRow.ratingAmount = 0 THEN
        return 0;
  END IF;
  select into average sum(rating) as ratingCount, count(*) as ratingAmount from rating;
  result = (ratingsRow.ratingCount + (average.ratingCount / average.ratingAmount) / (ratingsRow.ratingAmount + 10));
  return result;
END
$function$