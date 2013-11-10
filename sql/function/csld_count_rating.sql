CREATE OR REPLACE FUNCTION public.csld_rev_path(gameid integer)
  RETURNS text
LANGUAGE plpgsql
AS $function$
DECLARE
  result RECORD;
BEGIN
  select into result REVERSE(SUBSTRING(REVERSE(path), 0, position('/' in REVERSE(path))+1)) as path_new from csld_image where id = gameid;
  return result.path_new;
END
$function$;

update csld_image set path = 'upload' || csld_rev_path(id);
update csld_image set path = '' where id = 1;