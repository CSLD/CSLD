;CREATE OR REPLACE FUNCTION public.csld_count_rating(gameid integer)
  RETURNS double precision
LANGUAGE plpgsql
AS $function$
DECLARE
  gameRatings RECORD;
  result double precision;
  mostRatedGame RECORD;
BEGIN
  select into gameRatings sum(rating) as sumOf, count(*) as amountOf from csld_rating where game_id = gameId;
  if gameRatings.amountOf = 0 THEN
        return 0;
  END IF;
  select into mostRatedGame max(t.ratingsOfGames) as amountOfRatings from (select count(*) as ratingsOfGames from csld_rating group by game_id) as t;
  result = 0.3 * (10 * (gameRatings.amountOf / mostRatedGame.amountOfRatings)) + 0.7 * (gameRatings.sumOf / gameRatings.amountOf);
  return (result * 10);
END
$function$;