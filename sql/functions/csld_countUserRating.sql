CREATE OR REPLACE FUNCTION public.csld_countUserRating(userId integer)
  RETURNS double precision
LANGUAGE plpgsql
AS $function$
DECLARE
  ratingsRow RECORD;
  result double precision;
BEGIN
  select into ratingsRow count(rating) as ratingCount, count(*) as ratingAmount from user_rating where user_id = userId;
  if ratingsRow.ratingAmount = 0 THEN
        return 0;
  END IF;
  return ratingsRow.ratingCount;
END;
$function$;