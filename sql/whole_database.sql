/* Database generated with pgModeler (PostgreSQL Database Modeler).
  Project Site: pgmodeler.com.br
  Model Author: --- */

SET check_function_bodies = false;
-- ddl-end --

/* Database creation must be done outside an multicommand file.
   These commands were put in this file only for convenience.

-- object: new_database | type: DATABASE --
CREATE DATABASE new_database
	ENCODING = 'UTF8'
;
-- ddl-end --


*/

-- object: public."csld_countRating" | type: FUNCTION --
CREATE OR REPLACE FUNCTION public.csld_count_rating(gameid integer)
  RETURNS double precision
LANGUAGE plpgsql
AS $function$
DECLARE
  ratingsRow RECORD;
  result double precision;
  average RECORD;
BEGIN
  select into ratingsRow sum(rating) as ratingCount, count(*) as ratingAmount from rating where game_id = gameId;
  if ratingsRow.ratingAmount = 0 THEN
        return 0;
  END IF;
  select into average sum(rating) as ratingCount, count(*) as ratingAmount from rating;
  result = (ratingsRow.ratingCount + (average.ratingCount / average.ratingAmount * 8) / (ratingsRow.ratingAmount + 12));
  return result;
END
$function$;
-- ddl-end --

CREATE OR REPLACE FUNCTION public.csld_count_user_rating(userId integer)
  RETURNS integer
LANGUAGE plpgsql
AS $function$
DECLARE
  commentsRow RECORD;
  ratingsRow RECORD;
  playedRow RECORD;
  result integer;
BEGIN
  select into commentsRow count(*) as count from comment where user_id = userId;
  select into ratingsRow count(*) as count from rating where user_id = userId;
  select into playedRow count(*) as count from user_played_game where user_id = userId;

  result := playedRow.count + (ratingsRow.count * 2) + (commentsRow.count * 4);
  return result;
END;
$function$;

CREATE OR REPLACE FUNCTION public.csld_only_one_rating()
  RETURNS trigger
LANGUAGE plpgsql
AS $function$
DECLARE
  gameId int;
  userId int;
  ratingsRow RECORD;
BEGIN
  gameId := NEW.game_id;
  userId := NEW.user_id;

  select into ratingsRow count(*) as ratingAmount where game_id = gameId and user_id = userId;

  IF ratingsRow.ratingAmount > 0 THEN
     RETURN NULL;
  END IF;
  RETURN NEW;
END
$function$;

CREATE OR REPLACE FUNCTION public.csld_only_one_comment()
  RETURNS trigger
LANGUAGE plpgsql
AS $function$
DECLARE
  gameId int;
  userId int;
  commentsRow RECORD;
BEGIN
  gameId := NEW.game_id;
  userId := NEW.user_id;

  select into commentsRow count(*) as commentAmount from comment where game_id = gameId and user_id = userId;

  IF commentsRow.commentAmount > 0 THEN
     RETURN NULL;
  END IF;
  RETURN NEW;
END
$function$;

-- object: public.csld_game_id_seq | type: SEQUENCE --
CREATE SEQUENCE public.csld_game_id_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;
-- ddl-end --

-- object: public.csld_fb_user_seq | type: SEQUENCE --
CREATE SEQUENCE public.csld_fb_user_id_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;
-- ddl-end --

-- object: public.csld_video_id_seq | type: SEQUENCE --
CREATE SEQUENCE public.csld_video_id_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;
-- ddl-end --

-- object: public.csld_label_id_seq | type: SEQUENCE --
CREATE SEQUENCE public.csld_label_id_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;
-- ddl-end --

-- object: public.csld_user_id_seq | type: SEQUENCE --
CREATE SEQUENCE public.csld_user_id_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;
-- ddl-end --

-- object: public.csld_photo_id_seq | type: SEQUENCE --
CREATE SEQUENCE public.csld_photo_id_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;
-- ddl-end --

-- object: public.csld_image_id_seq | type: SEQUENCE --
CREATE SEQUENCE public.csld_image_id_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;
-- ddl-end --

-- object: public.csld_email_authentication_id_seq | type: SEQUENCE --
CREATE SEQUENCE public.csld_email_authentication_id_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;
-- ddl-end --

-- object: public.csld_person_id_seq | type: SEQUENCE --
CREATE SEQUENCE public.csld_person_id_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;
-- ddl-end --

-- object: public.csld_group_id_seq | type: SEQUENCE --
CREATE SEQUENCE public.csld_group_id_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;
-- ddl-end --

-- object: public.csld_interval_id_seq | type: SEQUENCE --
CREATE SEQUENCE public.csld_interval_id_seq
INCREMENT BY 1
MINVALUE 0
MAXVALUE 9223372036854775807
START WITH 1
CACHE 1
NO CYCLE
OWNED BY NONE;
-- ddl-end --

-- object: public.csld_user | type: TABLE --
CREATE TABLE public.csld_user(
  id integer NOT NULL DEFAULT nextval('csld_user_id_seq'),
  image integer,
  password text NOT NULL,
  role smallint NOT NULL,
  person integer NOT NULL,
  fb_user integer,
  CONSTRAINT csld_user_id_pk PRIMARY KEY (id)
);
-- ddl-end --

-- ddl-end --

-- object: public.fb_user | type: TABLE --
CREATE TABLE public.fb_user(
  id integer NOT NULL DEFAULT nextval('csld_fb_user_id_seq'),
  fb_token text NOT NULL,
  id_csld_user integer DEFAULT nextval('csld_user_id_seq'),
  CONSTRAINT csld_fb_user_pk PRIMARY KEY (id)
)
;
-- ddl-end --

-- ddl-end --

-- object: csld_user_fk | type: CONSTRAINT --
ALTER TABLE public.fb_user ADD CONSTRAINT csld_user_fk FOREIGN KEY (id_csld_user)
REFERENCES public.csld_user (id) MATCH FULL
ON DELETE SET NULL ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_user_60604_uq | type: CONSTRAINT --
ALTER TABLE public.fb_user ADD CONSTRAINT csld_user_60604_uq UNIQUE (id_csld_user);
-- ddl-end --


-- object: public.photo | type: TABLE --
CREATE TABLE public.photo(
  id integer NOT NULL DEFAULT nextval('csld_photo_id_seq'),
  image integer NOT NULL,
  author integer,
  version integer,
  CONSTRAINT csld_photo_pk PRIMARY KEY (id)
)
;
-- ddl-end --

-- ddl-end --

-- object: public.game | type: TABLE --
CREATE TABLE public.game(
  id integer NOT NULL DEFAULT nextval('csld_game_id_seq'),
  name text NOT NULL,
  description text,
  year integer,
  web text,
  hours integer,
  days integer,
  players integer,
  men_role integer,
  women_role integer,
  both_role integer,
  added timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  video integer,
  image integer,
  CONSTRAINT game_id_pk PRIMARY KEY (id)
)
;
-- ddl-end --

-- ddl-end --

-- object: public.game_has_photo | type: TABLE --
CREATE TABLE public.game_has_photo(
  id_photo integer DEFAULT nextval('csld_photo_id_seq'),
  id_game integer DEFAULT nextval('csld_game_id_seq'),
  CONSTRAINT game_has_photo_pk PRIMARY KEY (id_photo,id_game)
)
;
-- ddl-end --

-- ddl-end --

-- object: photo_fk | type: CONSTRAINT --
ALTER TABLE public.game_has_photo ADD CONSTRAINT photo_fk FOREIGN KEY (id_photo)
REFERENCES public.photo (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: game_fk | type: CONSTRAINT --
ALTER TABLE public.game_has_photo ADD CONSTRAINT game_fk FOREIGN KEY (id_game)
REFERENCES public.game (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: public.game_has_author | type: TABLE --
CREATE TABLE public.game_has_author(
  id_csld_user integer DEFAULT nextval('csld_user_id_seq'),
  id_game integer DEFAULT nextval('csld_game_id_seq'),
  CONSTRAINT game_has_author_pk PRIMARY KEY (id_csld_user,id_game)
)
;
-- ddl-end --

-- ddl-end --

-- object: csld_user_fk | type: CONSTRAINT --
ALTER TABLE public.game_has_author ADD CONSTRAINT csld_user_fk FOREIGN KEY (id_csld_user)
REFERENCES public.csld_user (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: game_fk | type: CONSTRAINT --
ALTER TABLE public.game_has_author ADD CONSTRAINT game_fk FOREIGN KEY (id_game)
REFERENCES public.game (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: public.group | type: TABLE --
CREATE TABLE public.group(
  id integer NOT NULL DEFAULT nextval('csld_group_id_seq'),
  image integer,
  name text NOT NULL,
  CONSTRAINT csld_group_pk PRIMARY KEY (id)
)
;
-- ddl-end --

-- ddl-end --

-- object: public.game_has_group | type: TABLE --
CREATE TABLE public.game_has_group(
  id_group integer DEFAULT nextval('csld_group_id_seq'),
  id_game integer DEFAULT nextval('csld_game_id_seq'),
  CONSTRAINT game_has_group_pk PRIMARY KEY (id_group,id_game)
)
;
-- ddl-end --

-- ddl-end --

-- object: group_fk | type: CONSTRAINT --
ALTER TABLE public.game_has_group ADD CONSTRAINT group_fk FOREIGN KEY (id_group)
REFERENCES public.group (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: game_fk | type: CONSTRAINT --
ALTER TABLE public.game_has_group ADD CONSTRAINT game_fk FOREIGN KEY (id_game)
REFERENCES public.game (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: public.label | type: TABLE --
CREATE TABLE public.label(
  id integer NOT NULL DEFAULT nextval('csld_label_id_seq'),
  name text NOT NULL,
  description text,
  is_required boolean DEFAULT false,
  is_authorized boolean DEFAULT false,
  added_by integer NOT NULL,
  CONSTRAINT label_id_pk PRIMARY KEY (id)
)
;
-- ddl-end --

-- ddl-end --

-- object: public.game_has_label | type: TABLE --
CREATE TABLE public.game_has_label(
  id_label integer DEFAULT nextval('csld_label_id_seq'),
  id_game integer DEFAULT nextval('csld_game_id_seq'),
  CONSTRAINT game_has_label_pk PRIMARY KEY (id_label,id_game)
)
;
-- ddl-end --

-- ddl-end --

-- object: label_fk | type: CONSTRAINT --
ALTER TABLE public.game_has_label ADD CONSTRAINT label_fk FOREIGN KEY (id_label)
REFERENCES public.label (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: game_fk | type: CONSTRAINT --
ALTER TABLE public.game_has_label ADD CONSTRAINT game_fk FOREIGN KEY (id_game)
REFERENCES public.game (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: public.group_has_administrator | type: TABLE --
CREATE TABLE public.group_has_administrator(
  id_group integer DEFAULT nextval('csld_group_id_seq'),
  id_csld_user integer DEFAULT nextval('csld_user_id_seq'),
  CONSTRAINT group_has_administrator_pk PRIMARY KEY (id_group,id_csld_user)
)
;
-- ddl-end --

-- ddl-end --

-- object: group_fk | type: CONSTRAINT --
ALTER TABLE public.group_has_administrator ADD CONSTRAINT group_fk FOREIGN KEY (id_group)
REFERENCES public.group (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_user_fk | type: CONSTRAINT --
ALTER TABLE public.group_has_administrator ADD CONSTRAINT csld_user_fk FOREIGN KEY (id_csld_user)
REFERENCES public.csld_user (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: public.group_has_members | type: TABLE --
CREATE TABLE public.group_has_members(
  group_id integer NOT NULL,
  user_id integer NOT NULL,
  CONSTRAINT csld_group_has_members_pk PRIMARY KEY (group_id,user_id)
)
;
-- ddl-end --

-- ddl-end --

-- object: public.interval | type: TABLE --
CREATE TABLE public.interval(
  id integer NOT NULL DEFAULT nextval('csld_interval_id_seq'),
  from_int timestamp NOT NULL,
  to_int timestamp NOT NULL,
  CONSTRAINT csld_interval_pk PRIMARY KEY (id)
)
;
-- ddl-end --

-- ddl-end --

-- object: public.group_has_members_in_intervals | type: TABLE --
CREATE TABLE public.group_has_members_in_intervals(
  group_id_group_has_members integer,
  user_id_group_has_members integer,
  id_interval integer DEFAULT nextval('csld_interval_id_seq'),
  CONSTRAINT group_has_members_in_intervals_pk PRIMARY KEY (group_id_group_has_members,user_id_group_has_members,id_interval)
)
;
-- ddl-end --

-- ddl-end --

-- object: group_has_members_fk | type: CONSTRAINT --
ALTER TABLE public.group_has_members_in_intervals ADD CONSTRAINT group_has_members_fk FOREIGN KEY (group_id_group_has_members,user_id_group_has_members)
REFERENCES public.group_has_members (group_id,user_id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: interval_fk | type: CONSTRAINT --
ALTER TABLE public.group_has_members_in_intervals ADD CONSTRAINT interval_fk FOREIGN KEY (id_interval)
REFERENCES public.interval (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: public.video | type: TABLE --
CREATE TABLE public.video(
  id integer NOT NULL DEFAULT nextval('csld_video_id_seq'),
  path text NOT NULL,
  type integer NOT NULL,
  CONSTRAINT video_id_pk PRIMARY KEY (id)
)
;
-- ddl-end --

-- ddl-end --

-- object: public.person | type: TABLE --
CREATE TABLE public.person(
  id integer NOT NULL DEFAULT nextval('csld_person_id_seq'),
  name text NOT NULL,
  email text NOT NULL,
  nickname text,
  birth_date date,
  city text,
  description text,
  CONSTRAINT csld_person_pk PRIMARY KEY (id)
)
;
-- ddl-end --

-- ddl-end --

-- object: public.email_authentication | type: TABLE --
CREATE TABLE public.email_authentication(
  id integer NOT NULL DEFAULT nextval('csld_email_authentication_id_seq'),
  auth_token text NOT NULL,
  user_id integer NOT NULL,
  CONSTRAINT csld_email_authentication_pk PRIMARY KEY (id)
)
;
-- ddl-end --

-- ddl-end --

-- object: public.image | type: TABLE --
CREATE TABLE public.image(
  id integer NOT NULL DEFAULT nextval('csld_image_id_seq'),
  path text NOT NULL,
  CONSTRAINT image_id_pk PRIMARY KEY (id)
)
;
-- ddl-end --

-- ddl-end --

-- object: public.rating | type: TABLE --
CREATE TABLE public.rating(
  user_id integer NOT NULL,
  game_id integer NOT NULL,
  rating integer NOT NULL,
  CONSTRAINT csld_rating_pk PRIMARY KEY (user_id,game_id)
)
;
-- ddl-end --

-- ddl-end --

-- object: public.comment | type: TABLE --
CREATE TABLE public.comment(
  user_id integer NOT NULL,
  game_id integer NOT NULL,
  comment text NOT NULL,
  CONSTRAINT csld_comment_pk PRIMARY KEY (user_id,game_id)
)
;
-- ddl-end --

-- ddl-end --

-- object: public.user_wants_game | type: TABLE --
CREATE TABLE public.user_wants_game(
  user_id integer NOT NULL,
  game_id integer NOT NULL,
  CONSTRAINT csld_user_wants_game_pk PRIMARY KEY (user_id,game_id)
)
;
-- ddl-end --

-- ddl-end --

-- object: public.user_played_game | type: TABLE --
CREATE TABLE public.user_played_game(
  user_id integer NOT NULL,
  game_id integer NOT NULL,
  CONSTRAINT csld_user_played_game_pk PRIMARY KEY (user_id,game_id)
)
;
-- ddl-end --

-- ddl-end --

-- object: csld_group_has_members_user_fk | type: CONSTRAINT --
ALTER TABLE public.group_has_members ADD CONSTRAINT csld_group_has_members_user_fk FOREIGN KEY (user_id)
REFERENCES public.csld_user (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_group_has_member_group_fk | type: CONSTRAINT --
ALTER TABLE public.group_has_members ADD CONSTRAINT csld_group_has_member_group_fk FOREIGN KEY (group_id)
REFERENCES public.group (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_user_played_game_game_fk | type: CONSTRAINT --
ALTER TABLE public.user_played_game ADD CONSTRAINT csld_user_played_game_game_fk FOREIGN KEY (game_id)
REFERENCES public.game (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_user_played_game_user_fk | type: CONSTRAINT --
ALTER TABLE public.user_played_game ADD CONSTRAINT csld_user_played_game_user_fk FOREIGN KEY (user_id)
REFERENCES public.csld_user (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_user_wants_game_game_fk | type: CONSTRAINT --
ALTER TABLE public.user_wants_game ADD CONSTRAINT csld_user_wants_game_game_fk FOREIGN KEY (game_id)
REFERENCES public.game (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_user_wants_game_user_fk | type: CONSTRAINT --
ALTER TABLE public.user_wants_game ADD CONSTRAINT csld_user_wants_game_user_fk FOREIGN KEY (user_id)
REFERENCES public.csld_user (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_comment_game_fk | type: CONSTRAINT --
ALTER TABLE public.comment ADD CONSTRAINT csld_comment_game_fk FOREIGN KEY (game_id)
REFERENCES public.game (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_comment_user_fk | type: CONSTRAINT --
ALTER TABLE public.comment ADD CONSTRAINT csld_comment_user_fk FOREIGN KEY (user_id)
REFERENCES public.csld_user (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_rating_user_fk | type: CONSTRAINT --
ALTER TABLE public.rating ADD CONSTRAINT csld_rating_user_fk FOREIGN KEY (user_id)
REFERENCES public.csld_user (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_rating_game_fk | type: CONSTRAINT --
ALTER TABLE public.rating ADD CONSTRAINT csld_rating_game_fk FOREIGN KEY (game_id)
REFERENCES public.game (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_group_image_pk | type: CONSTRAINT --
ALTER TABLE public.group ADD CONSTRAINT csld_group_image_pk FOREIGN KEY (image)
REFERENCES public.image (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_photo_image_fk | type: CONSTRAINT --
ALTER TABLE public.photo ADD CONSTRAINT csld_photo_image_fk FOREIGN KEY (image)
REFERENCES public.image (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_email_authentication_user_fk | type: CONSTRAINT --
ALTER TABLE public.email_authentication ADD CONSTRAINT csld_email_authentication_user_fk FOREIGN KEY (user_id)
REFERENCES public.csld_user (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_user_fb_user_fk | type: CONSTRAINT --
ALTER TABLE public.csld_user ADD CONSTRAINT csld_user_fb_user_fk FOREIGN KEY (fb_user)
REFERENCES public.fb_user (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_user_person_fk | type: CONSTRAINT --
ALTER TABLE public.csld_user ADD CONSTRAINT csld_user_person_fk FOREIGN KEY (person)
REFERENCES public.person (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_label_user_fk | type: CONSTRAINT --
ALTER TABLE public.label ADD CONSTRAINT csld_label_user_fk FOREIGN KEY (added_by)
REFERENCES public.csld_user (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_game_image_fk | type: CONSTRAINT --
ALTER TABLE public.game ADD CONSTRAINT csld_game_image_fk FOREIGN KEY (image)
REFERENCES public.image (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --


-- object: csld_game_video_fk | type: CONSTRAINT --
ALTER TABLE public.game ADD CONSTRAINT csld_game_video_fk FOREIGN KEY (video)
REFERENCES public.video (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE;
-- ddl-end --

CREATE TRIGGER csld_only_one_comment_trg
BEFORE INSERT
ON comment
FOR EACH ROW
EXECUTE PROCEDURE csld_only_one_comment();

CREATE TRIGGER csld_only_one_rating_trg
BEFORE INSERT
ON rating
FOR EACH ROW
EXECUTE PROCEDURE csld_only_one_rating();