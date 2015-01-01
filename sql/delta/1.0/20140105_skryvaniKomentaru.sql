alter table csld_comment add is_hidden boolean not null default false;

CREATE OR REPLACE FUNCTION csld_amount_of_comments(gameid integer) RETURNS integer
LANGUAGE plpgsql
AS $$
DECLARE
  ratingsRow RECORD;
  result integer;
BEGIN
  select into ratingsRow count(*) as ratingCount from csld_comment where game_id = gameid and not is_hidden;
  return ratingsRow.ratingCount;
END
$$;
