DROP TRIGGER update_ratings_count ON csld_rating;

update csld_game
set amount_of_ratings = subquery.amount_of_ratings
FROM (select count(*) as amount_of_ratings, game_id
      from csld_rating
      group by game_id, rating
      having rating is not null) AS subquery
where csld_game.id = subquery.game_id;