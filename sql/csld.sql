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


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

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
  select into ratingsRow sum(rating) as ratingCount, count(*) as ratingAmount from rating where game_id = gameId;
  if ratingsRow.ratingAmount = 0 THEN
        return 0;
  END IF;
  select into average sum(rating) as ratingCount, count(*) as ratingAmount from rating;
  result = (ratingsRow.ratingCount + (average.ratingCount / average.ratingAmount * 8) / (ratingsRow.ratingAmount + 12));
  return result;
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
  select into commentsRow count(*) as count from comment where user_id = userId;
  select into ratingsRow count(*) as count from rating where user_id = userId;
  select into playedRow count(*) as count from user_played_game where user_id = userId;

  result := playedRow.count + (ratingsRow.count * 2) + (commentsRow.count * 4);
  return result;
END;
$$;


ALTER FUNCTION public.csld_count_user_rating(userid integer) OWNER TO csld;

--
-- Name: csld_only_one_comment(); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_only_one_comment() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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
$$;


ALTER FUNCTION public.csld_only_one_comment() OWNER TO csld;

--
-- Name: csld_only_one_rating(); Type: FUNCTION; Schema: public; Owner: csld
--

CREATE FUNCTION csld_only_one_rating() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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
$$;


ALTER FUNCTION public.csld_only_one_rating() OWNER TO csld;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: comment; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE comment (
    user_id integer NOT NULL,
    game_id integer NOT NULL,
    comment text NOT NULL,
    added timestamp without time zone DEFAULT now()
);


ALTER TABLE public.comment OWNER TO csld;

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
-- Name: csld_fb_user_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_fb_user_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_fb_user_id_seq OWNER TO csld;

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
-- Name: csld_interval_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_interval_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_interval_id_seq OWNER TO csld;

--
-- Name: csld_label_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_label_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_label_id_seq OWNER TO csld;

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
-- Name: csld_user_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_user_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_user_id_seq OWNER TO csld;

--
-- Name: csld_user; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_user (
    id integer DEFAULT nextval('csld_user_id_seq'::regclass) NOT NULL,
    image integer,
    password text NOT NULL,
    role smallint NOT NULL,
    person integer NOT NULL,
    fb_user integer
);


ALTER TABLE public.csld_user OWNER TO csld;

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
-- Name: email_authentication; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE email_authentication (
    id integer DEFAULT nextval('csld_email_authentication_id_seq'::regclass) NOT NULL,
    auth_token text NOT NULL,
    user_id integer NOT NULL
);


ALTER TABLE public.email_authentication OWNER TO csld;

--
-- Name: fb_user; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE fb_user (
    id integer DEFAULT nextval('csld_fb_user_id_seq'::regclass) NOT NULL,
    fb_token text NOT NULL,
    id_csld_user integer DEFAULT nextval('csld_user_id_seq'::regclass)
);


ALTER TABLE public.fb_user OWNER TO csld;

--
-- Name: game; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE game (
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
    image integer
);


ALTER TABLE public.game OWNER TO csld;

--
-- Name: game_has_author; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE game_has_author (
    id_csld_user integer DEFAULT nextval('csld_user_id_seq'::regclass) NOT NULL,
    id_game integer DEFAULT nextval('csld_game_id_seq'::regclass) NOT NULL
);


ALTER TABLE public.game_has_author OWNER TO csld;

--
-- Name: game_has_group; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE game_has_group (
    id_group integer DEFAULT nextval('csld_group_id_seq'::regclass) NOT NULL,
    id_game integer DEFAULT nextval('csld_game_id_seq'::regclass) NOT NULL
);


ALTER TABLE public.game_has_group OWNER TO csld;

--
-- Name: game_has_label; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE game_has_label (
    id_label integer DEFAULT nextval('csld_label_id_seq'::regclass) NOT NULL,
    id_game integer DEFAULT nextval('csld_game_id_seq'::regclass) NOT NULL
);


ALTER TABLE public.game_has_label OWNER TO csld;

--
-- Name: game_has_photo; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE game_has_photo (
    id_photo integer DEFAULT nextval('csld_photo_id_seq'::regclass) NOT NULL,
    id_game integer DEFAULT nextval('csld_game_id_seq'::regclass) NOT NULL
);


ALTER TABLE public.game_has_photo OWNER TO csld;

--
-- Name: group; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_group (
    id integer DEFAULT nextval('csld_group_id_seq'::regclass) NOT NULL,
    image integer,
    name text NOT NULL
);


ALTER TABLE public.csld_group OWNER TO csld;

--
-- Name: group_has_administrator; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE group_has_administrator (
    id_group integer DEFAULT nextval('csld_group_id_seq'::regclass) NOT NULL,
    id_csld_user integer DEFAULT nextval('csld_user_id_seq'::regclass) NOT NULL
);


ALTER TABLE public.group_has_administrator OWNER TO csld;

--
-- Name: group_has_members; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE group_has_members (
    group_id integer NOT NULL,
    user_id integer NOT NULL,
    from_date date,
    to_date date
);


ALTER TABLE public.group_has_members OWNER TO csld;

--
-- Name: image; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE image (
    id integer DEFAULT nextval('csld_image_id_seq'::regclass) NOT NULL,
    path text NOT NULL
);


ALTER TABLE public.image OWNER TO csld;

--
-- Name: label; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE label (
    id integer DEFAULT nextval('csld_label_id_seq'::regclass) NOT NULL,
    name text NOT NULL,
    description text,
    is_required boolean DEFAULT false,
    is_authorized boolean DEFAULT false,
    added_by integer NOT NULL
);


ALTER TABLE public.label OWNER TO csld;

--
-- Name: person; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE person (
    id integer DEFAULT nextval('csld_person_id_seq'::regclass) NOT NULL,
    name text NOT NULL,
    email text NOT NULL,
    nickname text,
    birth_date date,
    city text
);


ALTER TABLE public.person OWNER TO csld;

--
-- Name: photo; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE photo (
    id integer DEFAULT nextval('csld_photo_id_seq'::regclass) NOT NULL,
    image integer NOT NULL,
    author integer,
    version integer
);


ALTER TABLE public.photo OWNER TO csld;

--
-- Name: rating; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE rating (
    user_id integer NOT NULL,
    game_id integer NOT NULL,
    rating integer NOT NULL
);


ALTER TABLE public.rating OWNER TO csld;

--
-- Name: user_played_game; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE user_played_game (
    user_id integer NOT NULL,
    game_id integer NOT NULL,
    state integer
);


ALTER TABLE public.user_played_game OWNER TO csld;

--
-- Name: video; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE video (
    id integer DEFAULT nextval('csld_video_id_seq'::regclass) NOT NULL,
    path text NOT NULL,
    type integer NOT NULL
);


ALTER TABLE public.video OWNER TO csld;

--
-- Data for Name: comment; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY comment (user_id, game_id, comment, added) FROM stdin;
1	1	Comment	2013-05-01 17:36:24.50377
\.


--
-- Name: csld_email_authentication_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_email_authentication_id_seq', 1, false);


--
-- Name: csld_fb_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_fb_user_id_seq', 1, false);


--
-- Name: csld_game_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_game_id_seq', 1, true);


--
-- Name: csld_group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_group_id_seq', 1, false);


--
-- Name: csld_image_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_image_id_seq', 2, true);


--
-- Name: csld_interval_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_interval_id_seq', 1, false);


--
-- Name: csld_label_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_label_id_seq', 1, false);


--
-- Name: csld_person_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_person_id_seq', 1, true);


--
-- Name: csld_photo_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_photo_id_seq', 1, false);


--
-- Data for Name: csld_user; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_user (id, image, password, role, person, fb_user) FROM stdin;
1	1	heslo	2	1	\N
\.


--
-- Name: csld_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_user_id_seq', 1, true);


--
-- Name: csld_video_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_video_id_seq', 1, false);


--
-- Data for Name: email_authentication; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY email_authentication (id, auth_token, user_id) FROM stdin;
\.


--
-- Data for Name: fb_user; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY fb_user (id, fb_token, id_csld_user) FROM stdin;
\.


--
-- Data for Name: game; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY game (id, name, description, year, web, hours, days, players, men_role, women_role, both_role, added, video, image) FROM stdin;
1	Bodlák	Popis	2010	www.bodlak.cz	4	0	12	7	5	0	2013-05-01 17:34:48.72	\N	\N
\.


--
-- Data for Name: game_has_author; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY game_has_author (id_csld_user, id_game) FROM stdin;
1	1
\.


--
-- Data for Name: game_has_group; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY game_has_group (id_group, id_game) FROM stdin;
\.


--
-- Data for Name: game_has_label; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY game_has_label (id_label, id_game) FROM stdin;
\.


--
-- Data for Name: game_has_photo; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY game_has_photo (id_photo, id_game) FROM stdin;
\.


--
-- Data for Name: group; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_group (id, image, name) FROM stdin;
\.


--
-- Data for Name: group_has_administrator; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY group_has_administrator (id_group, id_csld_user) FROM stdin;
\.


--
-- Data for Name: group_has_members; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY group_has_members (group_id, user_id, from_date, to_date) FROM stdin;
\.


--
-- Data for Name: image; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY image (id, path) FROM stdin;
1	/home/balhar/ProjektAll/CSLD/target/CSLD-1.0/files/upload/portret2.jpg
2	/home/balhar/ProjektAll/CSLD/target/CSLD-1.0/files/upload
\.


--
-- Data for Name: label; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY label (id, name, description, is_required, is_authorized, added_by) FROM stdin;
\.


--
-- Data for Name: person; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY person (id, name, email, nickname, birth_date, city) FROM stdin;
1	Jakub Balhar	balhar.jakub@gmail.com	Balda	1988-01-14	Praha
\.


--
-- Data for Name: photo; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY photo (id, image, author, version) FROM stdin;
\.


--
-- Data for Name: rating; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY rating (user_id, game_id, rating) FROM stdin;
\.


--
-- Data for Name: user_played_game; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY user_played_game (user_id, game_id, state) FROM stdin;
\.


--
-- Data for Name: video; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY video (id, path, type) FROM stdin;
\.


--
-- Name: csld_comment_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY comment
    ADD CONSTRAINT csld_comment_pk PRIMARY KEY (user_id, game_id);


--
-- Name: csld_email_authentication_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY email_authentication
    ADD CONSTRAINT csld_email_authentication_pk PRIMARY KEY (id);


--
-- Name: csld_fb_user_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY fb_user
    ADD CONSTRAINT csld_fb_user_pk PRIMARY KEY (id);


--
-- Name: csld_group_has_members_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY group_has_members
    ADD CONSTRAINT csld_group_has_members_pk PRIMARY KEY (group_id, user_id);


--
-- Name: csld_group_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_group
    ADD CONSTRAINT csld_group_pk PRIMARY KEY (id);


--
-- Name: csld_person_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY person
    ADD CONSTRAINT csld_person_pk PRIMARY KEY (id);


--
-- Name: csld_photo_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY photo
    ADD CONSTRAINT csld_photo_pk PRIMARY KEY (id);


--
-- Name: csld_rating_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY rating
    ADD CONSTRAINT csld_rating_pk PRIMARY KEY (user_id, game_id);


--
-- Name: csld_user_60604_uq; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY fb_user
    ADD CONSTRAINT csld_user_60604_uq UNIQUE (id_csld_user);


--
-- Name: csld_user_id_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_user
    ADD CONSTRAINT csld_user_id_pk PRIMARY KEY (id);


--
-- Name: csld_user_played_game_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY user_played_game
    ADD CONSTRAINT csld_user_played_game_pk PRIMARY KEY (user_id, game_id);


--
-- Name: game_has_author_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY game_has_author
    ADD CONSTRAINT game_has_author_pk PRIMARY KEY (id_csld_user, id_game);


--
-- Name: game_has_group_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY game_has_group
    ADD CONSTRAINT game_has_group_pk PRIMARY KEY (id_group, id_game);


--
-- Name: game_has_label_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY game_has_label
    ADD CONSTRAINT game_has_label_pk PRIMARY KEY (id_label, id_game);


--
-- Name: game_has_photo_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY game_has_photo
    ADD CONSTRAINT game_has_photo_pk PRIMARY KEY (id_photo, id_game);


--
-- Name: game_id_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY game
    ADD CONSTRAINT game_id_pk PRIMARY KEY (id);


--
-- Name: group_has_administrator_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY group_has_administrator
    ADD CONSTRAINT group_has_administrator_pk PRIMARY KEY (id_group, id_csld_user);


--
-- Name: image_id_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY image
    ADD CONSTRAINT image_id_pk PRIMARY KEY (id);


--
-- Name: label_id_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY label
    ADD CONSTRAINT label_id_pk PRIMARY KEY (id);


--
-- Name: video_id_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY video
    ADD CONSTRAINT video_id_pk PRIMARY KEY (id);


--
-- Name: csld_only_one_comment_trg; Type: TRIGGER; Schema: public; Owner: csld
--

CREATE TRIGGER csld_only_one_comment_trg BEFORE INSERT ON comment FOR EACH ROW EXECUTE PROCEDURE csld_only_one_comment();


--
-- Name: csld_only_one_rating_trg; Type: TRIGGER; Schema: public; Owner: csld
--

CREATE TRIGGER csld_only_one_rating_trg BEFORE INSERT ON rating FOR EACH ROW EXECUTE PROCEDURE csld_only_one_rating();


--
-- Name: csld_comment_game_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY comment
    ADD CONSTRAINT csld_comment_game_fk FOREIGN KEY (game_id) REFERENCES game(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_comment_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY comment
    ADD CONSTRAINT csld_comment_user_fk FOREIGN KEY (user_id) REFERENCES csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_email_authentication_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY email_authentication
    ADD CONSTRAINT csld_email_authentication_user_fk FOREIGN KEY (user_id) REFERENCES csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_game_image_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY game
    ADD CONSTRAINT csld_game_image_fk FOREIGN KEY (image) REFERENCES image(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_game_video_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY game
    ADD CONSTRAINT csld_game_video_fk FOREIGN KEY (video) REFERENCES video(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_group_has_member_group_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY group_has_members
    ADD CONSTRAINT csld_group_has_member_group_fk FOREIGN KEY (group_id) REFERENCES csld_group(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_group_has_members_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY group_has_members
    ADD CONSTRAINT csld_group_has_members_user_fk FOREIGN KEY (user_id) REFERENCES csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_group_image_pk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_group
    ADD CONSTRAINT csld_group_image_pk FOREIGN KEY (image) REFERENCES image(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_label_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY label
    ADD CONSTRAINT csld_label_user_fk FOREIGN KEY (added_by) REFERENCES csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_photo_image_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY photo
    ADD CONSTRAINT csld_photo_image_fk FOREIGN KEY (image) REFERENCES image(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_rating_game_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY rating
    ADD CONSTRAINT csld_rating_game_fk FOREIGN KEY (game_id) REFERENCES game(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_rating_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY rating
    ADD CONSTRAINT csld_rating_user_fk FOREIGN KEY (user_id) REFERENCES csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_user_fb_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_user
    ADD CONSTRAINT csld_user_fb_user_fk FOREIGN KEY (fb_user) REFERENCES fb_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY fb_user
    ADD CONSTRAINT csld_user_fk FOREIGN KEY (id_csld_user) REFERENCES csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE SET NULL;


--
-- Name: csld_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY game_has_author
    ADD CONSTRAINT csld_user_fk FOREIGN KEY (id_csld_user) REFERENCES csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY group_has_administrator
    ADD CONSTRAINT csld_user_fk FOREIGN KEY (id_csld_user) REFERENCES csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_user_person_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_user
    ADD CONSTRAINT csld_user_person_fk FOREIGN KEY (person) REFERENCES person(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_user_played_game_game_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY user_played_game
    ADD CONSTRAINT csld_user_played_game_game_fk FOREIGN KEY (game_id) REFERENCES game(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_user_played_game_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY user_played_game
    ADD CONSTRAINT csld_user_played_game_user_fk FOREIGN KEY (user_id) REFERENCES csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: game_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY game_has_photo
    ADD CONSTRAINT game_fk FOREIGN KEY (id_game) REFERENCES game(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: game_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY game_has_author
    ADD CONSTRAINT game_fk FOREIGN KEY (id_game) REFERENCES game(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: game_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY game_has_group
    ADD CONSTRAINT game_fk FOREIGN KEY (id_game) REFERENCES game(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: game_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY game_has_label
    ADD CONSTRAINT game_fk FOREIGN KEY (id_game) REFERENCES game(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: group_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY game_has_group
    ADD CONSTRAINT group_fk FOREIGN KEY (id_group) REFERENCES csld_group(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: group_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY group_has_administrator
    ADD CONSTRAINT group_fk FOREIGN KEY (id_group) REFERENCES csld_group(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: label_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY game_has_label
    ADD CONSTRAINT label_fk FOREIGN KEY (id_label) REFERENCES label(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: photo_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY game_has_photo
    ADD CONSTRAINT photo_fk FOREIGN KEY (id_photo) REFERENCES photo(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

