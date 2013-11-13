CREATE OR REPLACE FUNCTION reverse(text) RETURNS text
    AS $_$
DECLARE
original alias for $1;
      reverse_str text;
      i int4;
BEGIN
    reverse_str := '';
    FOR i IN REVERSE LENGTH(original)..1 LOOP
      reverse_str := reverse_str || substr(original,i,1);
    END LOOP;
RETURN reverse_str;
END;$_$
    LANGUAGE plpgsql IMMUTABLE;

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
