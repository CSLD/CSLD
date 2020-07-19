drop trigger if exists update_ratings_count on csld_rating;

drop function if exists csld_calculate_amount_of_ratings;
CREATE FUNCTION csld_calculate_amount_of_ratings() RETURNS trigger
    LANGUAGE plpgsql
AS
$$
BEGIN
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
    return null;
END
$$;

drop trigger if exists update_ratings_count on csld_rating;
CREATE TRIGGER update_ratings_count
    AFTER INSERT OR DELETE
    ON csld_rating
    FOR EACH ROW
EXECUTE PROCEDURE csld_calculate_amount_of_ratings();