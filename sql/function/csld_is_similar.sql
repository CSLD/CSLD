CREATE OR REPLACE FUNCTION public.csld_is_similar(game1Id integer, game2Id integer)
  RETURNS boolean
LANGUAGE plpgsql
AS $function$
DECLARE
  sameLabels RECORD;
  relevantLabels RECORD;
BEGIN
  select into relevantLabels count(id_label) as amount from csld_game_has_label where id_game=game1Id;
  WITH game1_labels
        as(select id_label from csld_game_has_label where id_game=game1Id),
       game2_labels
        as (select id_label from csld_game_has_label where id_game=game2Id)
  select into sameLabels count(g1.id_label) as amount from game1_labels g1 join game2_labels g2 on g1.id_label = g2.id_label;
  return sameLabels.amount = relevantLabels.amount;
END
$function$;