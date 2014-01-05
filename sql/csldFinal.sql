--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


SET search_path = public, pg_catalog;

--
-- Name: csld_group_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_group_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_group_id_seq OWNER TO csld;

--
-- Name: csld_person_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_person_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_person_id_seq OWNER TO csld;

--
-- Name: csld_game_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_game_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_game_id_seq OWNER TO csld;

--
-- Name: csld_game_label_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_game_label_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_game_label_id_seq OWNER TO csld;
--
-- Name: csld_count_rating(integer); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE OR REPLACE FUNCTION public.csld_count_best_game(userid integer)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE
  gameRatings RECORD;
  mostRatedGame RECORD;
BEGIN
  select into gameRatings gha.id_game as bestId from csld_game_has_author gha join csld_game game on game.id = gha.id_game where gha.id_user = userid order by csld_count_rating(gha.id_game) desc limit 1;
  return gameRatings.bestId;
END
$function$;


CREATE OR REPLACE FUNCTION public.csld_count_rating(gameid integer)
 RETURNS double precision
 LANGUAGE plpgsql
AS $function$
DECLARE
  ratingsRow RECORD;
  result double precision;
  average RECORD;
BEGIN
  select into ratingsRow sum(rating) as ratingCount, count(*) as ratingAmount from csld_rating where game_id = gameId;
  if ratingsRow.ratingAmount < 5 THEN
        return 0;
  END IF;
  select into average sum(rating) as ratingCount, count(*) as ratingAmount, avg(rating) as ratingAvg from csld_rating;
  result = (ratingsRow.ratingCount + (1.0 * average.ratingCount / average.ratingAmount) * 5) / (0.0 + ratingsRow.ratingAmount + 5);
  return result * 10;
END
$function$;

ALTER FUNCTION public.csld_count_rating(gameid integer) OWNER TO csld;

CREATE OR REPLACE FUNCTION public.csld_update_rating()
  RETURNS trigger
LANGUAGE plpgsql
AS $function$
DECLARE
  gameId int;
BEGIN
  IF (TG_OP = 'DELETE') THEN
    gameId := OLD.game_id;
  ELSE
    gameId := NEW.game_id;
  END IF;
  update csld_game set total_rating = csld_count_rating(gameId) where id = gameId;
  return null;
END
$function$;

CREATE FUNCTION csld_amount_of_ratings(gameid integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
  ratingsRow RECORD;
  result integer;
BEGIN
  select into ratingsRow count(*) as ratingCount from csld_rating where game_id = gameId;
  return ratingsRow.ratingCount;
END
$$;

ALTER FUNCTION public.csld_amount_of_ratings(gameid integer) OWNER TO csld;

CREATE FUNCTION csld_count_average() RETURNS double precision
    LANGUAGE plpgsql
    AS $$
DECLARE
  ratingsRow RECORD;
BEGIN
  select into ratingsRow count(*) as ratingCount, sum(rating) as sumOf from csld_rating;
  return CAST(ratingsRow.sumOf as double precision) / ratingsRow.ratingCount;
END
$$;

ALTER FUNCTION public.csld_count_average() OWNER TO csld;

CREATE FUNCTION csld_user_amount_of_comments(userid integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
  ratingsRow RECORD;
  result integer;
BEGIN
  select into ratingsRow count(*) as ratingCount from csld_comment where user_id = userid;
  return ratingsRow.ratingCount;
END
$$;


ALTER FUNCTION public.csld_user_amount_of_comments(userid integer) OWNER TO csld;

CREATE FUNCTION csld_amount_of_comments(gameid integer) RETURNS integer
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


ALTER FUNCTION public.csld_amount_of_comments(gameid integer) OWNER TO csld;

CREATE OR REPLACE FUNCTION csld_user_amount_of_played(userid integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
  ratingsRow RECORD;
  result integer;
BEGIN
  select into ratingsRow count(*) as ratingCount from csld_user_played_game where user_id = userid and state = 2;
  return ratingsRow.ratingCount;
END
$$;

CREATE OR REPLACE FUNCTION csld_amount_of_played(gameid integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
  ratingsRow RECORD;
  result integer;
BEGIN
  select into ratingsRow count(*) as ratingCount from csld_user_played_game where game_id = gameid and state = 2;
  return ratingsRow.ratingCount;
END
$$;

ALTER FUNCTION public.csld_user_amount_of_played(userid integer) OWNER TO csld;

CREATE or replace FUNCTION csld_count_games_of_author(userid integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
  ratingsRow RECORD;
BEGIN
  select into ratingsRow count(*) as ratingCount from csld_game_has_author where id_user = userid;
  return ratingsRow.ratingCount;
END
$$;

--
-- Name: csld_count_user_rating(integer); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_count_user_rating(userid integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
  commentsRow RECORD;
  ratingsRow RECORD;
  playedRow RECORD;
  result integer;
BEGIN
  select into commentsRow count(*) as count from csld_comment where user_id = userId;
  select into ratingsRow count(*) as count from csld_rating where user_id = userId;
  select into playedRow count(*) as count from csld_user_played_game where user_id = userId;

  result := playedRow.count + (ratingsRow.count * 2) + (commentsRow.count * 4);
  return result;
END;
$$;


ALTER FUNCTION public.csld_count_user_rating(userid integer) OWNER TO csld;

CREATE OR REPLACE FUNCTION public.csld_update_amount_of_comments()
  RETURNS trigger
LANGUAGE plpgsql
AS $function$
DECLARE
  gameId int;
  userId int;
BEGIN
  IF (TG_OP = 'DELETE') THEN
      gameId := OLD.game_id;
      userId := OLD.user_id;

      update csld_game set amount_of_comments = amount_of_comments - 1 where id = gameId;
      update csld_csld_user set amount_of_comments = amount_of_comments - 1 where id = userId;
  ELSIF (TG_OP = 'INSERT') THEN
      gameId := NEW.game_id;
      userId := NEW.user_id;

      update csld_game set amount_of_comments = amount_of_comments + 1 where id = gameId;
      update csld_csld_user set amount_of_comments = amount_of_comments + 1 where id = userId;
  END IF;
  return null;
END
$function$;

CREATE OR REPLACE FUNCTION public.csld_update_amount_of_created()
  RETURNS trigger
LANGUAGE plpgsql
AS $function$
DECLARE
  userId int;
BEGIN
  IF (TG_OP = 'DELETE') THEN
      userId := OLD.id_user;

      update csld_csld_user set amount_of_created = amount_of_created - 1 where id = userId;
  ELSIF (TG_OP = 'INSERT') THEN
      userId := NEW.id_user;

      update csld_csld_user set amount_of_created = amount_of_created + 1 where id = userId;
  END IF;
  return null;
END
$function$;

CREATE OR REPLACE FUNCTION public.csld_update_amount_of_ratings()
  RETURNS trigger
LANGUAGE plpgsql
AS $function$
DECLARE
  gameId int;
BEGIN
  IF (TG_OP = 'DELETE') THEN
      gameId := OLD.game_id;

      update csld_game set amount_of_ratings = amount_of_ratings - 1 where id = gameId;
  ELSIF (TG_OP = 'INSERT') THEN
      gameId := NEW.game_id;

      update csld_game set amount_of_ratings = amount_of_ratings + 1 where id = gameId;
  END IF;
  return null;
END
$function$;

CREATE OR REPLACE FUNCTION public.csld_update_best_game()
  RETURNS trigger
LANGUAGE plpgsql
AS $function$
DECLARE
  userId int;
BEGIN
  IF (TG_OP = 'DELETE') THEN
    userId := OLD.user_id;
  ELSE
    userId := NEW.user_id;
  END IF;
  update csld_csld_user set best_game_id = csld_count_best_game(id) where id = userId;
  RETURN NULL;
END
$function$;

CREATE OR REPLACE FUNCTION public.csld_update_amount_of_played()
  RETURNS trigger
LANGUAGE plpgsql
AS $function$
DECLARE
  gameId int;
  userId int;
BEGIN
  IF (TG_OP = 'DELETE') THEN
      gameId := OLD.game_id;
      userId := OLD.user_id;
      if (OLD.state <> 2) THEN
        return null;
      END IF;

      update csld_game set amount_of_played = amount_of_played - 1 where id = gameId;
      update csld_csld_user set amount_of_played = amount_of_played - 1 where id = userId;
  ELSIF (TG_OP = 'INSERT') THEN
      gameId := NEW.game_id;
      userId := NEW.user_id;
      if (NEW.state <> 2) THEN
        return null;
      END IF;

      update csld_game set amount_of_played = amount_of_played + 1 where id = gameId;
      update csld_csld_user set amount_of_played = amount_of_played + 1 where id = userId;
  END IF;
  return null;
END
$function$;

SET default_tablespace = '';
SET default_with_oids = false;

--
-- Name: csld_comment; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_comment (
    user_id integer NOT NULL,
    game_id integer NOT NULL,
    comment text NOT NULL,
    added timestamp without time zone DEFAULT now(),
    is_hidden boolean not null default false,

    PRIMARY KEY (user_id, game_id)
);


ALTER TABLE public.csld_comment OWNER TO csld;

--
-- Name: csld_csld_group; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_csld_group (
    id integer DEFAULT nextval('csld_group_id_seq'::regclass) NOT NULL,
    image integer,
    name text NOT NULL,

    PRIMARY KEY(id)
);


ALTER TABLE public.csld_csld_group OWNER TO csld;

--
-- Name: csld_csld_user; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_csld_user (
    id integer DEFAULT nextval('csld_person_id_seq'::regclass) NOT NULL,
    password text NOT NULL,
    role smallint NOT NULL,
    name text,
    nickname text,
    birth_date date,
    email varchar(200),
    image integer,
    address text,
    description text,
    is_author boolean default false,
    amount_of_comments integer default 0,
    amount_of_played integer default 0,
    amount_of_created integer default 0,
    best_game_id integer,

    PRIMARY KEY(id)
);


ALTER TABLE public.csld_csld_user OWNER TO csld;

CREATE UNIQUE INDEX csld_csld_user_lc_email ON csld_csld_user(lower(email));

--
-- Name: csld_email_authentication_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_email_authentication_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_email_authentication_id_seq OWNER TO csld;

--
-- Name: csld_email_authentication; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_email_authentication (
    id integer DEFAULT nextval('csld_email_authentication_id_seq'::regclass) NOT NULL,
    auth_token text NOT NULL,
    user_id integer NOT NULL,

    PRIMARY KEY(id)
);


ALTER TABLE public.csld_email_authentication OWNER TO csld;

--
-- Name: csld_game; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_game (
    id integer DEFAULT nextval('csld_game_id_seq'::regclass) NOT NULL,
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
    added timestamp without time zone DEFAULT now() NOT NULL,
    video integer,
    image integer,
    added_by integer,
    total_rating double precision,
    amount_of_comments integer default 0,
    amount_of_played integer default 0,
    amount_of_ratings integer default 0,

    PRIMARY KEY(id)
);


ALTER TABLE public.csld_game OWNER TO csld;

--
-- Name: csld_game_has_author; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_game_has_author (
    id_user integer NOT NULL,
    id_game integer NOT NULL,

    PRIMARY KEY(id_user, id_game)
);


ALTER TABLE public.csld_game_has_author OWNER TO csld;

--
-- Name: csld_game_has_group; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_game_has_group (
    id_group integer NOT NULL,
    id_game integer NOT NULL,

    PRIMARY KEY(id_group, id_game)
);


ALTER TABLE public.csld_game_has_group OWNER TO csld;

--
-- Name: csld_game_has_label; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_game_has_label (
    id_label integer NOT NULL,
    id_game integer NOT NULL,

    PRIMARY KEY(id_label, id_game)
);


ALTER TABLE public.csld_game_has_label OWNER TO csld;

--
-- Name: csld_photo_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_photo_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_photo_id_seq OWNER TO csld;

--
-- Name: csld_group_has_administrator; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_group_has_administrator (
    id_group integer NOT NULL,
    id_user integer NOT NULL,

    PRIMARY KEY(id_group, id_user)
);


ALTER TABLE public.csld_group_has_administrator OWNER TO csld;

--
-- Name: csld_group_has_members; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_group_has_members (
    group_id integer NOT NULL,
    user_id integer NOT NULL,
    from_date date,
    to_date date,

    PRIMARY KEY(group_id, user_id)
);


ALTER TABLE public.csld_group_has_members OWNER TO csld;

--
-- Name: csld_image_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_image_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_image_id_seq OWNER TO csld;

--
-- Name: csld_image; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_image (
    id integer DEFAULT nextval('csld_image_id_seq'::regclass) NOT NULL,
    path text NOT NULL,

    PRIMARY KEY(id)
);


ALTER TABLE public.csld_image OWNER TO csld;

--
-- Name: csld_label; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_label (
    id integer DEFAULT nextval('csld_game_label_id_seq'::regclass) NOT NULL,
    name text NOT NULL,
    description text,
    is_required boolean DEFAULT false,
    is_authorized boolean DEFAULT false,
    added_by integer NOT NULL,

    PRIMARY KEY(id)
);


ALTER TABLE public.csld_label OWNER TO csld;

--
-- Name: csld_photo; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_photo (
    id integer DEFAULT nextval('csld_photo_id_seq'::regclass) NOT NULL,
    image integer NOT NULL,
    author integer,
    version integer,
    game integer,

    PRIMARY KEY(id)
);


ALTER TABLE public.csld_photo OWNER TO csld;

--
-- Name: csld_rating; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_rating (
    user_id integer NOT NULL,
    game_id integer NOT NULL,
    rating integer NOT NULL,

    PRIMARY KEY(user_id, game_id)
);


ALTER TABLE public.csld_rating OWNER TO csld;

--
-- Name: csld_user_played_game; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_user_played_game (
    user_id integer NOT NULL,
    game_id integer NOT NULL,
    state integer,

    PRIMARY KEY(user_id, game_id)
);


ALTER TABLE public.csld_user_played_game OWNER TO csld;

--
-- Name: csld_video_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_video_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_video_id_seq OWNER TO csld;

--
-- Name: csld_video; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_video (
    id integer DEFAULT nextval('csld_video_id_seq'::regclass) NOT NULL,
    path text NOT NULL,
    type integer NOT NULL,

    PRIMARY KEY(id)
);


ALTER TABLE public.csld_video OWNER TO csld;

--
-- Name: csld_comment_game_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_comment
    ADD CONSTRAINT csld_comment_game_fk FOREIGN KEY (game_id) REFERENCES csld_game(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_comment_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_comment
    ADD CONSTRAINT csld_comment_user_fk FOREIGN KEY (user_id) REFERENCES csld_csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_email_authentication_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_email_authentication
    ADD CONSTRAINT csld_email_authentication_user_fk FOREIGN KEY (user_id) REFERENCES csld_csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_game_image_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_game
    ADD CONSTRAINT csld_game_image_fk FOREIGN KEY (image) REFERENCES csld_image(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE ONLY csld_game
    ADD CONSTRAINT csld_game_added_by_fk FOREIGN KEY (added_by) REFERENCES csld_csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_game_video_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_game
    ADD CONSTRAINT csld_game_video_fk FOREIGN KEY (video) REFERENCES csld_video(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_group_has_member_group_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_group_has_members
    ADD CONSTRAINT csld_group_has_member_group_fk FOREIGN KEY (group_id) REFERENCES csld_csld_group(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_group_has_members_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_group_has_members
    ADD CONSTRAINT csld_group_has_members_user_fk FOREIGN KEY (user_id) REFERENCES csld_csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_group_image_pk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_csld_group
    ADD CONSTRAINT csld_group_image_pk FOREIGN KEY (image) REFERENCES csld_image(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_label_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_label
    ADD CONSTRAINT csld_label_user_fk FOREIGN KEY (added_by) REFERENCES csld_csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_photo_image_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_photo
    ADD CONSTRAINT csld_photo_image_fk FOREIGN KEY (image) REFERENCES csld_image(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;

--
-- Name: csld_photo_person_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_photo
    ADD CONSTRAINT csld_photo_person_fk FOREIGN KEY (author) REFERENCES csld_csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_rating_game_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_rating
    ADD CONSTRAINT csld_rating_game_fk FOREIGN KEY (game_id) REFERENCES csld_game(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_rating_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_rating
    ADD CONSTRAINT csld_rating_user_fk FOREIGN KEY (user_id) REFERENCES csld_csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_game_has_author
    ADD CONSTRAINT csld_user_fk FOREIGN KEY (id_user) REFERENCES csld_csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_group_has_administrator
    ADD CONSTRAINT csld_user_fk FOREIGN KEY (id_user) REFERENCES csld_csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;



--
-- Name: csld_user_played_game_game_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_user_played_game
    ADD CONSTRAINT csld_user_played_game_game_fk FOREIGN KEY (game_id) REFERENCES csld_game(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_user_played_game_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_user_played_game
    ADD CONSTRAINT csld_user_played_game_user_fk FOREIGN KEY (user_id) REFERENCES csld_csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: game_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_game_has_author
    ADD CONSTRAINT game_fk FOREIGN KEY (id_game) REFERENCES csld_game(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: game_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_game_has_group
    ADD CONSTRAINT game_fk FOREIGN KEY (id_game) REFERENCES csld_game(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: game_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_game_has_label
    ADD CONSTRAINT game_fk FOREIGN KEY (id_game) REFERENCES csld_game(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: group_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_game_has_group
    ADD CONSTRAINT group_fk FOREIGN KEY (id_group) REFERENCES csld_csld_group(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: group_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_group_has_administrator
    ADD CONSTRAINT group_fk FOREIGN KEY (id_group) REFERENCES csld_csld_group(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: label_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_game_has_label
    ADD CONSTRAINT label_fk FOREIGN KEY (id_label) REFERENCES csld_label(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;

CREATE TRIGGER update_rating
  AFTER INSERT OR UPDATE OR DELETE ON csld_rating
    FOR EACH ROW EXECUTE PROCEDURE csld_update_rating();

CREATE TRIGGER update_comments
  AFTER INSERT OR DELETE ON csld_comment
    FOR EACH ROW EXECUTE PROCEDURE csld_update_amount_of_comments();

CREATE TRIGGER update_played
  AFTER INSERT OR DELETE ON csld_user_played_game
    FOR EACH ROW EXECUTE PROCEDURE csld_update_amount_of_played();

CREATE TRIGGER update_created
  AFTER INSERT OR DELETE ON csld_game_has_author
    FOR EACH ROW EXECUTE PROCEDURE csld_update_amount_of_created();

CREATE TRIGGER update_ratings_count
  AFTER INSERT OR DELETE ON csld_rating
    FOR EACH ROW EXECUTE PROCEDURE csld_update_amount_of_ratings();

CREATE TRIGGER update_best_game
  AFTER INSERT OR DELETE OR UPDATE ON csld_rating
    FOR EACH ROW EXECUTE PROCEDURE csld_update_best_game();


create index total_rating_idx on csld_game(total_rating);

create index game_added_idx on csld_game(added);

create index game_name_idx on csld_game(name);

create index comment_added_idx on csld_comment(added);

create index user_name_idx on csld_csld_user(name);

create index group_name_idx on csld_csld_group(name);

create index game_amount_of_comments_idx on csld_game(amount_of_comments);

create index game_amount_of_played_idx on csld_game(amount_of_played);

create index user_amount_of_comments_idx on csld_csld_user(amount_of_comments);

create index user_amount_of_played_idx on csld_csld_user(amount_of_played);

create index game_amount_of_ratings_idx on csld_game(amount_of_ratings);

create index user_best_game_idx on csld_csld_user(best_game_id);