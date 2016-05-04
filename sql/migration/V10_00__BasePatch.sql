--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

--
-- Name: csld_amount_of_comments(integer); Type: FUNCTION; Schema: public; Owner: csld
--

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

--
-- Name: csld_amount_of_played(integer); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_amount_of_played(gameid integer) RETURNS integer
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


ALTER FUNCTION public.csld_amount_of_played(gameid integer) OWNER TO csld;

--
-- Name: csld_amount_of_ratings(integer); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_amount_of_ratings(gameid integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
  ratingsRow RECORD;
  result integer;
BEGIN
  select into ratingsRow count(*) as ratingCount from csld_rating where game_id = eventId;
  return ratingsRow.ratingCount;
END
$$;


ALTER FUNCTION public.csld_amount_of_ratings(gameid integer) OWNER TO csld;

--
-- Name: csld_count_average(); Type: FUNCTION; Schema: public; Owner: csld
--

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

--
-- Name: csld_count_best_game(integer); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_count_best_game(userid integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
  gameRatings RECORD;
  mostRatedGame RECORD;
BEGIN
  select into gameRatings gha.id_game as bestId from csld_game_has_author gha join csld_game game on game.id = gha.id_game where gha.id_user = userid order by csld_count_rating(gha.id_game) desc limit 1;
  return gameRatings.bestId;
END
$$;


ALTER FUNCTION public.csld_count_best_game(userid integer) OWNER TO csld;

--
-- Name: csld_count_games_of_author(integer); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_count_games_of_author(userid integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
  ratingsRow RECORD;
BEGIN
  select into ratingsRow count(*) as ratingCount from csld_game_has_author where id_user = userid;
  return ratingsRow.ratingCount;
END
$$;


ALTER FUNCTION public.csld_count_games_of_author(userid integer) OWNER TO csld;

--
-- Name: csld_count_rating(integer); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_count_rating(gameid integer) RETURNS double precision
    LANGUAGE plpgsql
    AS $$
DECLARE
  ratingsRow RECORD;
  result double precision;
  average RECORD;
BEGIN
  select into ratingsRow sum(rating) as ratingCount, count(*) as ratingAmount from csld_rating where game_id = eventId;
  if ratingsRow.ratingAmount < 5 THEN
        return 0;
  END IF;
  select into average sum(rating) as ratingCount, count(*) as ratingAmount, avg(rating) as ratingAvg from csld_rating;
  result = (ratingsRow.ratingCount + (1.0 * average.ratingCount / average.ratingAmount) * 5) / (0.0 + ratingsRow.ratingAmount + 5);
  return result * 10;
END
$$;


ALTER FUNCTION public.csld_count_rating(gameid integer) OWNER TO csld;

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

--
-- Name: csld_countcomments(integer); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_countcomments(gameid integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
  commentsRow RECORD;
BEGIN
  select into commentsRow count(*) as commentCount from comment where game_id = eventId;
  return commentsRow.commentCount;
END;
$$;


ALTER FUNCTION public.csld_countcomments(gameid integer) OWNER TO csld;

--
-- Name: csld_countrating(integer); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_countrating(gameid integer) RETURNS double precision
    LANGUAGE plpgsql
    AS $$DECLARE
  ratingsRow RECORD;
  result double precision;
  average RECORD;
BEGIN
  select into ratingsRow sum(rating) as ratingCount, count(*) as ratingAmount from rating where game_id = eventId;
  if ratingsRow.ratingAmount < 5 THEN
        return 0;
  END IF;
  select into average sum(rating) as ratingCount, count(*) as ratingAmount, avg(rating) as ratingAvg from rating;
  result = (ratingsRow.ratingCount + (1.0 * average.ratingCount / average.ratingAmount) * 5) / (0.0 + ratingsRow.ratingAmount + 5);
  return result;
END$$;


ALTER FUNCTION public.csld_countrating(gameid integer) OWNER TO csld;

--
-- Name: csld_countuserrating(integer); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_countuserrating(userid integer) RETURNS double precision
    LANGUAGE plpgsql
    AS $$
DECLARE
  ratingsRow RECORD;
  result double precision;
BEGIN
  select into ratingsRow count(rating) as ratingCount, count(*) as ratingAmount from user_rating where user_id = userId;
  if ratingsRow.ratingAmount = 0 THEN
        return 0;
  END IF;
  return ratingsRow.ratingCount;
END;
$$;


ALTER FUNCTION public.csld_countuserrating(userid integer) OWNER TO csld;

--
-- Name: csld_is_similar(integer, integer); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_is_similar(game1id integer, game2id integer) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
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
$$;


ALTER FUNCTION public.csld_is_similar(game1id integer, game2id integer) OWNER TO csld;

--
-- Name: csld_rev_path(integer); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_rev_path(gameid integer) RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
  result RECORD;
BEGIN
  select into result REVERSE(SUBSTRING(REVERSE(path), 0, position('/' in REVERSE(path))+1)) as path_new from csld_image where id = gameid;
  return result.path_new;
END
$$;


ALTER FUNCTION public.csld_rev_path(gameid integer) OWNER TO csld;

--
-- Name: csld_update_amount_of_comments(); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_update_amount_of_comments() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
  eventId int;
  userId int;
BEGIN
  IF (TG_OP = 'DELETE') THEN
      eventId := OLD.game_id;
      userId := OLD.user_id;

      update csld_game set amount_of_comments = amount_of_comments - 1 where id = eventId;
      update csld_csld_user set amount_of_comments = amount_of_comments - 1 where id = userId;
  ELSIF (TG_OP = 'INSERT') THEN
      eventId := NEW.game_id;
      userId := NEW.user_id;

      update csld_game set amount_of_comments = amount_of_comments + 1 where id = eventId;
      update csld_csld_user set amount_of_comments = amount_of_comments + 1 where id = userId;
  END IF;
  return null;
END
$$;


ALTER FUNCTION public.csld_update_amount_of_comments() OWNER TO csld;

--
-- Name: csld_update_amount_of_created(); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_update_amount_of_created() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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
$$;


ALTER FUNCTION public.csld_update_amount_of_created() OWNER TO csld;

--
-- Name: csld_update_amount_of_played(); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_update_amount_of_played() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
  eventId int;
  userId int;
BEGIN
  IF (TG_OP = 'DELETE') THEN
      eventId := OLD.game_id;
      userId := OLD.user_id;
      if (OLD.state <> 2) THEN
        return null;
      END IF;

      update csld_game set amount_of_played = amount_of_played - 1 where id = eventId;
      update csld_csld_user set amount_of_played = amount_of_played - 1 where id = userId;
  ELSIF (TG_OP = 'INSERT') THEN
      eventId := NEW.game_id;
      userId := NEW.user_id;
      if (NEW.state <> 2) THEN
        return null;
      END IF;

      update csld_game set amount_of_played = amount_of_played + 1 where id = eventId;
      update csld_csld_user set amount_of_played = amount_of_played + 1 where id = userId;
  END IF;
  return null;
END
$$;


ALTER FUNCTION public.csld_update_amount_of_played() OWNER TO csld;

--
-- Name: csld_update_amount_of_ratings(); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_update_amount_of_ratings() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
  eventId int;
BEGIN
  IF (TG_OP = 'DELETE') THEN
      eventId := OLD.game_id;

      update csld_game set amount_of_ratings = amount_of_ratings - 1 where id = eventId;
  ELSIF (TG_OP = 'INSERT') THEN
      eventId := NEW.game_id;

      update csld_game set amount_of_ratings = amount_of_ratings + 1 where id = eventId;
  END IF;
  return null;
END
$$;


ALTER FUNCTION public.csld_update_amount_of_ratings() OWNER TO csld;

--
-- Name: csld_update_best_game(); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_update_best_game() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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
$$;


ALTER FUNCTION public.csld_update_best_game() OWNER TO csld;

--
-- Name: csld_update_rating(); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_update_rating() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
  eventId int;
BEGIN
  IF (TG_OP = 'DELETE') THEN
    eventId := OLD.game_id;
  ELSE
    eventId := NEW.game_id;
  END IF;
  update csld_game set total_rating = csld_count_rating(eventId) where id = eventId;
  update csld_game set average_rating = (select avg(rating) from csld_rating where csld_rating.game_id=eventId)*10 where id = eventId;
  RETURN NEW;
END
$$;


ALTER FUNCTION public.csld_update_rating() OWNER TO csld;

--
-- Name: csld_user_amount_of_comments(integer); Type: FUNCTION; Schema: public; Owner: csld
--

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

--
-- Name: csld_user_amount_of_played(integer); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_user_amount_of_played(userid integer) RETURNS integer
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


ALTER FUNCTION public.csld_user_amount_of_played(userid integer) OWNER TO csld;

--
-- Name: reverse(text); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION reverse(text) RETURNS text
    LANGUAGE plpgsql IMMUTABLE
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
END;$_$;


ALTER FUNCTION public.reverse(text) OWNER TO csld;

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
    is_hidden boolean DEFAULT false NOT NULL
);


ALTER TABLE public.csld_comment OWNER TO csld;

--
-- Name: csld_group_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_group_id_seq OWNER TO csld;

--
-- Name: csld_csld_group; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_csld_group (
    id integer DEFAULT nextval('csld_group_id_seq'::regclass) NOT NULL,
    image integer,
    name text NOT NULL
);


ALTER TABLE public.csld_csld_group OWNER TO csld;

--
-- Name: csld_person_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_person_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_person_id_seq OWNER TO csld;

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
    email character varying(200),
    image integer,
    address text,
    description text,
    is_author boolean DEFAULT false,
    amount_of_comments integer DEFAULT 0,
    amount_of_played integer DEFAULT 0,
    amount_of_created integer DEFAULT 0,
    best_game_id integer,
    last_rating integer
);


ALTER TABLE public.csld_csld_user OWNER TO csld;

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
    user_id integer NOT NULL
);


ALTER TABLE public.csld_email_authentication OWNER TO csld;

--
-- Name: csld_game_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_game_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_game_id_seq OWNER TO csld;

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
    amount_of_comments integer DEFAULT 0,
    amount_of_played integer DEFAULT 0,
    amount_of_ratings integer DEFAULT 0,
    gallery_url text,
    photo_author text,
    average_rating double precision,
    deleted boolean,
    lang character varying(32) DEFAULT 'cs'::character varying,
    ratingsdisabled boolean DEFAULT false NOT NULL,
    commentsdisabled boolean DEFAULT false NOT NULL
);


ALTER TABLE public.csld_game OWNER TO csld;

--
-- Name: csld_game_has_author; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_game_has_author (
    id_user integer NOT NULL,
    id_game integer NOT NULL
);


ALTER TABLE public.csld_game_has_author OWNER TO csld;

--
-- Name: csld_game_has_group; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_game_has_group (
    id_group integer NOT NULL,
    id_game integer NOT NULL
);


ALTER TABLE public.csld_game_has_group OWNER TO csld;

--
-- Name: csld_game_has_label; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_game_has_label (
    id_label integer NOT NULL,
    id_game integer NOT NULL
);


ALTER TABLE public.csld_game_has_label OWNER TO csld;

--
-- Name: csld_game_label_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_game_label_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_game_label_id_seq OWNER TO csld;

--
-- Name: csld_group_has_administrator; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_group_has_administrator (
    id_group integer NOT NULL,
    id_user integer NOT NULL
);


ALTER TABLE public.csld_group_has_administrator OWNER TO csld;

--
-- Name: csld_group_has_members; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_group_has_members (
    group_id integer NOT NULL,
    user_id integer NOT NULL,
    from_date date,
    to_date date
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
    contenttype character varying(256)
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
    added_by integer NOT NULL
);


ALTER TABLE public.csld_label OWNER TO csld;

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
-- Name: csld_photo; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_photo (
    id integer DEFAULT nextval('csld_photo_id_seq'::regclass) NOT NULL,
    image integer NOT NULL,
    author integer,
    game integer,
    orderseq integer DEFAULT 0 NOT NULL,
    description character varying(2048),
    fullwidth integer DEFAULT 0 NOT NULL,
    fullheight integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.csld_photo OWNER TO csld;

--
-- Name: csld_rating; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_rating (
    user_id integer NOT NULL,
    game_id integer NOT NULL,
    rating integer NOT NULL,
    added timestamp without time zone DEFAULT now()
);


ALTER TABLE public.csld_rating OWNER TO csld;

--
-- Name: csld_user_played_game; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_user_played_game (
    user_id integer NOT NULL,
    game_id integer NOT NULL,
    state integer
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
    type integer NOT NULL
);


ALTER TABLE public.csld_video OWNER TO csld;

--
-- Name: csld_comment_pkey; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_comment
    ADD CONSTRAINT csld_comment_pkey PRIMARY KEY (user_id, game_id);


--
-- Name: csld_csld_group_pkey; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_csld_group
    ADD CONSTRAINT csld_csld_group_pkey PRIMARY KEY (id);


--
-- Name: csld_csld_user_pkey; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_csld_user
    ADD CONSTRAINT csld_csld_user_pkey PRIMARY KEY (id);


--
-- Name: csld_email_authentication_pkey; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_email_authentication
    ADD CONSTRAINT csld_email_authentication_pkey PRIMARY KEY (id);


--
-- Name: csld_game_has_author_pkey; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_game_has_author
    ADD CONSTRAINT csld_game_has_author_pkey PRIMARY KEY (id_user, id_game);


--
-- Name: csld_game_has_group_pkey; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_game_has_group
    ADD CONSTRAINT csld_game_has_group_pkey PRIMARY KEY (id_group, id_game);


--
-- Name: csld_game_has_label_pkey; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_game_has_label
    ADD CONSTRAINT csld_game_has_label_pkey PRIMARY KEY (id_label, id_game);


--
-- Name: csld_game_pkey; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_game
    ADD CONSTRAINT csld_game_pkey PRIMARY KEY (id);


--
-- Name: csld_group_has_administrator_pkey; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_group_has_administrator
    ADD CONSTRAINT csld_group_has_administrator_pkey PRIMARY KEY (id_group, id_user);


--
-- Name: csld_group_has_members_pkey; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_group_has_members
    ADD CONSTRAINT csld_group_has_members_pkey PRIMARY KEY (group_id, user_id);


--
-- Name: csld_image_pkey; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_image
    ADD CONSTRAINT csld_image_pkey PRIMARY KEY (id);


--
-- Name: csld_label_pkey; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_label
    ADD CONSTRAINT csld_label_pkey PRIMARY KEY (id);


--
-- Name: csld_photo_pkey; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_photo
    ADD CONSTRAINT csld_photo_pkey PRIMARY KEY (id);


--
-- Name: csld_rating_pkey; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_rating
    ADD CONSTRAINT csld_rating_pkey PRIMARY KEY (user_id, game_id);


--
-- Name: csld_user_played_game_pkey; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_user_played_game
    ADD CONSTRAINT csld_user_played_game_pkey PRIMARY KEY (user_id, game_id);


--
-- Name: csld_video_pkey; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_video
    ADD CONSTRAINT csld_video_pkey PRIMARY KEY (id);


--
-- Name: comment_added_idx; Type: INDEX; Schema: public; Owner: csld; Tablespace: 
--

CREATE INDEX comment_added_idx ON csld_comment USING btree (added);


--
-- Name: game_added_idx; Type: INDEX; Schema: public; Owner: csld; Tablespace: 
--

CREATE INDEX game_added_idx ON csld_game USING btree (added);


--
-- Name: game_amount_of_comments_idx; Type: INDEX; Schema: public; Owner: csld; Tablespace: 
--

CREATE INDEX game_amount_of_comments_idx ON csld_game USING btree (amount_of_comments);


--
-- Name: game_amount_of_played_idx; Type: INDEX; Schema: public; Owner: csld; Tablespace: 
--

CREATE INDEX game_amount_of_played_idx ON csld_game USING btree (amount_of_played);


--
-- Name: game_amount_of_ratings_idx; Type: INDEX; Schema: public; Owner: csld; Tablespace: 
--

CREATE INDEX game_amount_of_ratings_idx ON csld_game USING btree (amount_of_ratings);


--
-- Name: game_name_idx; Type: INDEX; Schema: public; Owner: csld; Tablespace: 
--

CREATE INDEX game_name_idx ON csld_game USING btree (name);


--
-- Name: group_name_idx; Type: INDEX; Schema: public; Owner: csld; Tablespace: 
--

CREATE INDEX group_name_idx ON csld_csld_group USING btree (name);


--
-- Name: total_rating_idx; Type: INDEX; Schema: public; Owner: csld; Tablespace: 
--

CREATE INDEX total_rating_idx ON csld_game USING btree (total_rating);


--
-- Name: user_amount_of_comments_idx; Type: INDEX; Schema: public; Owner: csld; Tablespace: 
--

CREATE INDEX user_amount_of_comments_idx ON csld_csld_user USING btree (amount_of_comments);


--
-- Name: user_amount_of_played_idx; Type: INDEX; Schema: public; Owner: csld; Tablespace: 
--

CREATE INDEX user_amount_of_played_idx ON csld_csld_user USING btree (amount_of_played);


--
-- Name: user_best_game_idx; Type: INDEX; Schema: public; Owner: csld; Tablespace: 
--

CREATE INDEX user_best_game_idx ON csld_csld_user USING btree (best_game_id);


--
-- Name: user_name_idx; Type: INDEX; Schema: public; Owner: csld; Tablespace: 
--

CREATE INDEX user_name_idx ON csld_csld_user USING btree (name);


--
-- Name: update_best_game; Type: TRIGGER; Schema: public; Owner: csld
--

CREATE TRIGGER update_best_game AFTER INSERT OR DELETE OR UPDATE ON csld_rating FOR EACH ROW EXECUTE PROCEDURE csld_update_best_game();


--
-- Name: update_comments; Type: TRIGGER; Schema: public; Owner: csld
--

CREATE TRIGGER update_comments AFTER INSERT OR DELETE ON csld_comment FOR EACH ROW EXECUTE PROCEDURE csld_update_amount_of_comments();


--
-- Name: update_created; Type: TRIGGER; Schema: public; Owner: csld
--

CREATE TRIGGER update_created AFTER INSERT OR DELETE ON csld_game_has_author FOR EACH ROW EXECUTE PROCEDURE csld_update_amount_of_created();


--
-- Name: update_played; Type: TRIGGER; Schema: public; Owner: csld
--

CREATE TRIGGER update_played AFTER INSERT OR DELETE ON csld_user_played_game FOR EACH ROW EXECUTE PROCEDURE csld_update_amount_of_played();


--
-- Name: update_rating; Type: TRIGGER; Schema: public; Owner: csld
--

CREATE TRIGGER update_rating AFTER INSERT OR DELETE OR UPDATE ON csld_rating FOR EACH ROW EXECUTE PROCEDURE csld_update_rating();


--
-- Name: update_ratings_count; Type: TRIGGER; Schema: public; Owner: csld
--

CREATE TRIGGER update_ratings_count AFTER INSERT OR DELETE ON csld_rating FOR EACH ROW EXECUTE PROCEDURE csld_update_amount_of_ratings();


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
-- Name: csld_game_added_by_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_game
    ADD CONSTRAINT csld_game_added_by_fk FOREIGN KEY (added_by) REFERENCES csld_csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_game_image_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_game
    ADD CONSTRAINT csld_game_image_fk FOREIGN KEY (image) REFERENCES csld_image(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


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
    ADD CONSTRAINT group_fk FOREIGN KEY (id_group) REFERENCES csld_csld_group(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: label_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_game_has_label
    ADD CONSTRAINT label_fk FOREIGN KEY (id_label) REFERENCES csld_label(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- Name: csld_comment; Type: ACL; Schema: public; Owner: csld
--

REVOKE ALL ON TABLE csld_comment FROM PUBLIC;
REVOKE ALL ON TABLE csld_comment FROM csld;
GRANT ALL ON TABLE csld_comment TO csld;


--
-- Name: csld_csld_group; Type: ACL; Schema: public; Owner: csld
--

REVOKE ALL ON TABLE csld_csld_group FROM PUBLIC;
REVOKE ALL ON TABLE csld_csld_group FROM csld;
GRANT ALL ON TABLE csld_csld_group TO csld;


--
-- Name: csld_csld_user; Type: ACL; Schema: public; Owner: csld
--

REVOKE ALL ON TABLE csld_csld_user FROM PUBLIC;
REVOKE ALL ON TABLE csld_csld_user FROM csld;
GRANT ALL ON TABLE csld_csld_user TO csld;


--
-- Name: csld_email_authentication; Type: ACL; Schema: public; Owner: csld
--

REVOKE ALL ON TABLE csld_email_authentication FROM PUBLIC;
REVOKE ALL ON TABLE csld_email_authentication FROM csld;
GRANT ALL ON TABLE csld_email_authentication TO csld;


--
-- Name: csld_game; Type: ACL; Schema: public; Owner: csld
--

REVOKE ALL ON TABLE csld_game FROM PUBLIC;
REVOKE ALL ON TABLE csld_game FROM csld;
GRANT ALL ON TABLE csld_game TO csld;


--
-- Name: csld_game_has_author; Type: ACL; Schema: public; Owner: csld
--

REVOKE ALL ON TABLE csld_game_has_author FROM PUBLIC;
REVOKE ALL ON TABLE csld_game_has_author FROM csld;
GRANT ALL ON TABLE csld_game_has_author TO csld;


--
-- Name: csld_game_has_group; Type: ACL; Schema: public; Owner: csld
--

REVOKE ALL ON TABLE csld_game_has_group FROM PUBLIC;
REVOKE ALL ON TABLE csld_game_has_group FROM csld;
GRANT ALL ON TABLE csld_game_has_group TO csld;


--
-- Name: csld_game_has_label; Type: ACL; Schema: public; Owner: csld
--

REVOKE ALL ON TABLE csld_game_has_label FROM PUBLIC;
REVOKE ALL ON TABLE csld_game_has_label FROM csld;
GRANT ALL ON TABLE csld_game_has_label TO csld;


--
-- Name: csld_group_has_administrator; Type: ACL; Schema: public; Owner: csld
--

REVOKE ALL ON TABLE csld_group_has_administrator FROM PUBLIC;
REVOKE ALL ON TABLE csld_group_has_administrator FROM csld;
GRANT ALL ON TABLE csld_group_has_administrator TO csld;


--
-- Name: csld_group_has_members; Type: ACL; Schema: public; Owner: csld
--

REVOKE ALL ON TABLE csld_group_has_members FROM PUBLIC;
REVOKE ALL ON TABLE csld_group_has_members FROM csld;
GRANT ALL ON TABLE csld_group_has_members TO csld;


--
-- Name: csld_image; Type: ACL; Schema: public; Owner: csld
--

REVOKE ALL ON TABLE csld_image FROM PUBLIC;
REVOKE ALL ON TABLE csld_image FROM csld;
GRANT ALL ON TABLE csld_image TO csld;


--
-- Name: csld_label; Type: ACL; Schema: public; Owner: csld
--

REVOKE ALL ON TABLE csld_label FROM PUBLIC;
REVOKE ALL ON TABLE csld_label FROM csld;
GRANT ALL ON TABLE csld_label TO csld;


--
-- Name: csld_photo; Type: ACL; Schema: public; Owner: csld
--

REVOKE ALL ON TABLE csld_photo FROM PUBLIC;
REVOKE ALL ON TABLE csld_photo FROM csld;
GRANT ALL ON TABLE csld_photo TO csld;


--
-- Name: csld_rating; Type: ACL; Schema: public; Owner: csld
--

REVOKE ALL ON TABLE csld_rating FROM PUBLIC;
REVOKE ALL ON TABLE csld_rating FROM csld;
GRANT ALL ON TABLE csld_rating TO csld;


--
-- Name: csld_user_played_game; Type: ACL; Schema: public; Owner: csld
--

REVOKE ALL ON TABLE csld_user_played_game FROM PUBLIC;
REVOKE ALL ON TABLE csld_user_played_game FROM csld;
GRANT ALL ON TABLE csld_user_played_game TO csld;


--
-- Name: csld_video; Type: ACL; Schema: public; Owner: csld
--

REVOKE ALL ON TABLE csld_video FROM PUBLIC;
REVOKE ALL ON TABLE csld_video FROM csld;
GRANT ALL ON TABLE csld_video TO csld;


--
-- PostgreSQL database dump complete
--

