update csld_game
set amount_of_ratings = subquery.ratings
FROM (
         select sum(amounts.amount_of_ratings) as ratings, amounts.game_id
         from (
                  select count(*) as amount_of_ratings, game_id
                  from csld_rating
                  group by game_id, rating
                  having rating is not null) AS amounts
         group by amounts.game_id) as subquery
where csld_game.id = subquery.game_id;
