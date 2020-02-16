CREATE FUNCTION csld_update_amount_of_ratings_2() RETURNS trigger
    LANGUAGE plpgsql
AS
$$
DECLARE
    gameId int;
BEGIN
    IF (TG_OP = 'DELETE') THEN
        gameId := OLD.game_id;

        if (OLD.rating is not null) THEN
            update csld_game set amount_of_ratings = amount_of_ratings - 1 where id = gameId;
        END IF;
    ELSIF (TG_OP = 'INSERT') THEN
        gameId := NEW.game_id;

        if (NEW.rating is not null) THEN
            update csld_game set amount_of_ratings = amount_of_ratings + 1 where id = gameId;
        END IF;
    END IF;
    return null;
END
$$;

CREATE TRIGGER update_ratings_count
    AFTER INSERT OR DELETE
    ON csld_rating
    FOR EACH ROW
EXECUTE PROCEDURE csld_update_amount_of_ratings_2();

