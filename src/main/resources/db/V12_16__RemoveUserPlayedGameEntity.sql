/*
Reconcile the current state of the UserPlayedGame
*/
alter table csld_rating
    add state integer;
alter table csld_rating
    alter column rating drop not null;

/**
  Update those states that are already in the table.
 */
UPDATE
    csld_rating
SET state = subquery.state
FROM (
         SELECT state, game_id, user_id
         FROM csld_user_played_game
     ) AS subquery
WHERE csld_rating.game_id = subquery.game_id
  AND csld_rating.user_id = subquery.user_id;

/*
 Insert those states that aren't yet in the table.
 */
INSERT INTO csld_rating (user_id, game_id, state, rating)
SELECT csld_user_played_game.user_id, csld_user_played_game.game_id, csld_user_played_game.state, null as rating
FROM csld_user_played_game
         LEFT JOIN csld_rating ON
        csld_rating.game_id = csld_user_played_game.game_id AND
        csld_rating.user_id = csld_user_played_game.user_id
WHERE csld_user_played_game.state <> 0
  AND rating is NULL;

drop table csld_user_played_game;

