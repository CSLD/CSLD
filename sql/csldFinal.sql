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
  select into ratingsRow sum(rating) as ratingCount, count(*) as ratingAmount from csld_rating where game_id = gameId;
  if ratingsRow.ratingAmount = 0 THEN
        return 0;
  END IF;
  select into average sum(rating) as ratingCount, count(*) as ratingAmount from csld_rating;
  result = (ratingsRow.ratingCount + (average.ratingCount / average.ratingAmount * 8) / (ratingsRow.ratingAmount + 12));
  return result;
END
$$;


ALTER FUNCTION public.csld_count_rating(gameid integer) OWNER TO db2_vyuka;

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


ALTER FUNCTION public.csld_count_user_rating(userid integer) OWNER TO db2_vyuka;

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

  select into commentsRow count(*) as commentAmount from csld_comment where game_id = gameId and user_id = userId;

  IF commentsRow.commentAmount > 0 THEN
     RETURN NULL;
  END IF;
  RETURN NEW;
END
$$;


ALTER FUNCTION public.csld_only_one_comment() OWNER TO db2_vyuka;

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

  select into ratingsRow count(*) as ratingAmount from csld_rating where game_id = gameId and user_id = userId;

  IF ratingsRow.ratingAmount > 0 THEN
     RETURN NULL;
  END IF;
  RETURN NEW;
END
$$;


ALTER FUNCTION public.csld_only_one_rating() OWNER TO db2_vyuka;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: csld_comment; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_comment (
    user_id integer NOT NULL,
    game_id integer NOT NULL,
    comment text NOT NULL,
    added timestamp without time zone DEFAULT now()
);


ALTER TABLE public.csld_comment OWNER TO db2_vyuka;

--
-- Name: csld_group_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_group_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_group_id_seq OWNER TO db2_vyuka;

--
-- Name: csld_csld_group; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_csld_group (
    id integer DEFAULT nextval('csld_group_id_seq'::regclass) NOT NULL,
    image integer,
    name text NOT NULL
);


ALTER TABLE public.csld_csld_group OWNER TO db2_vyuka;

--
-- Name: csld_user_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_user_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_user_id_seq OWNER TO db2_vyuka;

--
-- Name: csld_csld_user; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_csld_user (
    id integer DEFAULT nextval('csld_user_id_seq'::regclass) NOT NULL,
    image integer,
    password text NOT NULL,
    role smallint NOT NULL,
    person integer NOT NULL,
    fb_user integer
);


ALTER TABLE public.csld_csld_user OWNER TO db2_vyuka;

--
-- Name: csld_email_authentication_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_email_authentication_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_email_authentication_id_seq OWNER TO db2_vyuka;

--
-- Name: csld_email_authentication; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_email_authentication (
    id integer DEFAULT nextval('csld_email_authentication_id_seq'::regclass) NOT NULL,
    auth_token text NOT NULL,
    user_id integer NOT NULL
);


ALTER TABLE public.csld_email_authentication OWNER TO db2_vyuka;

--
-- Name: csld_fb_user_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_fb_user_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_fb_user_id_seq OWNER TO db2_vyuka;

--
-- Name: csld_fb_user; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_fb_user (
    id integer DEFAULT nextval('csld_fb_user_id_seq'::regclass) NOT NULL,
    fb_token text NOT NULL,
    id_csld_user integer DEFAULT nextval('csld_user_id_seq'::regclass)
);


ALTER TABLE public.csld_fb_user OWNER TO db2_vyuka;

--
-- Name: csld_game_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_game_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_game_id_seq OWNER TO db2_vyuka;

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
    image integer
);


ALTER TABLE public.csld_game OWNER TO db2_vyuka;

--
-- Name: csld_game_has_author; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_game_has_author (
    id_csld_user integer DEFAULT nextval('csld_user_id_seq'::regclass) NOT NULL,
    id_game integer DEFAULT nextval('csld_game_id_seq'::regclass) NOT NULL
);


ALTER TABLE public.csld_game_has_author OWNER TO db2_vyuka;

--
-- Name: csld_game_has_group; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_game_has_group (
    id_group integer DEFAULT nextval('csld_group_id_seq'::regclass) NOT NULL,
    id_game integer DEFAULT nextval('csld_game_id_seq'::regclass) NOT NULL
);


ALTER TABLE public.csld_game_has_group OWNER TO db2_vyuka;

--
-- Name: csld_label_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_label_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_label_id_seq OWNER TO db2_vyuka;

--
-- Name: csld_game_has_label; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_game_has_label (
    id_label integer DEFAULT nextval('csld_label_id_seq'::regclass) NOT NULL,
    id_game integer DEFAULT nextval('csld_game_id_seq'::regclass) NOT NULL
);


ALTER TABLE public.csld_game_has_label OWNER TO db2_vyuka;

--
-- Name: csld_photo_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_photo_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_photo_id_seq OWNER TO db2_vyuka;

--
-- Name: csld_game_has_photo; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_game_has_photo (
    id_photo integer DEFAULT nextval('csld_photo_id_seq'::regclass) NOT NULL,
    id_game integer DEFAULT nextval('csld_game_id_seq'::regclass) NOT NULL
);


ALTER TABLE public.csld_game_has_photo OWNER TO db2_vyuka;

--
-- Name: csld_group_has_administrator; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_group_has_administrator (
    id_group integer DEFAULT nextval('csld_group_id_seq'::regclass) NOT NULL,
    id_csld_user integer DEFAULT nextval('csld_user_id_seq'::regclass) NOT NULL
);


ALTER TABLE public.csld_group_has_administrator OWNER TO db2_vyuka;

--
-- Name: csld_group_has_members; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_group_has_members (
    group_id integer NOT NULL,
    user_id integer NOT NULL,
    from_date date,
    to_date date
);


ALTER TABLE public.csld_group_has_members OWNER TO db2_vyuka;

--
-- Name: csld_image_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_image_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_image_id_seq OWNER TO db2_vyuka;

--
-- Name: csld_image; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_image (
    id integer DEFAULT nextval('csld_image_id_seq'::regclass) NOT NULL,
    path text NOT NULL
);


ALTER TABLE public.csld_image OWNER TO db2_vyuka;

--
-- Name: csld_label; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_label (
    id integer DEFAULT nextval('csld_label_id_seq'::regclass) NOT NULL,
    name text NOT NULL,
    description text,
    is_required boolean DEFAULT false,
    is_authorized boolean DEFAULT false,
    added_by integer NOT NULL
);


ALTER TABLE public.csld_label OWNER TO db2_vyuka;

--
-- Name: csld_person_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_person_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_person_id_seq OWNER TO db2_vyuka;

--
-- Name: csld_person; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_person (
    id integer DEFAULT nextval('csld_person_id_seq'::regclass) NOT NULL,
    name text NOT NULL,
    email text NOT NULL,
    nickname text,
    birth_date date,
    city text,
    description text
);


ALTER TABLE public.csld_person OWNER TO db2_vyuka;

--
-- Name: csld_photo; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_photo (
    id integer DEFAULT nextval('csld_photo_id_seq'::regclass) NOT NULL,
    image integer NOT NULL,
    author integer,
    version integer
);


ALTER TABLE public.csld_photo OWNER TO db2_vyuka;

--
-- Name: csld_rating; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_rating (
    user_id integer NOT NULL,
    game_id integer NOT NULL,
    rating integer NOT NULL
);


ALTER TABLE public.csld_rating OWNER TO db2_vyuka;

--
-- Name: csld_user_played_game; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_user_played_game (
    user_id integer NOT NULL,
    game_id integer NOT NULL,
    state integer
);


ALTER TABLE public.csld_user_played_game OWNER TO db2_vyuka;

--
-- Name: csld_video_id_seq; Type: SEQUENCE; Schema: public; Owner: csld
--

CREATE SEQUENCE csld_video_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.csld_video_id_seq OWNER TO db2_vyuka;

--
-- Name: csld_video; Type: TABLE; Schema: public; Owner: csld; Tablespace: 
--

CREATE TABLE csld_video (
    id integer DEFAULT nextval('csld_video_id_seq'::regclass) NOT NULL,
    path text NOT NULL,
    type integer NOT NULL
);


ALTER TABLE public.csld_video OWNER TO db2_vyuka;

--
-- Data for Name: csld_comment; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_comment (user_id, game_id, comment, added) FROM stdin;
1	2	Comment	2013-05-01 17:36:24.50377
2	1	Coment	2013-06-02 16:46:24.50377
3	4	Commnt	2013-04-03 15:38:24.50377
1	1	Commet	2013-08-04 20:26:24.50377
2	5	Commen	2013-03-05 10:18:24.50377
\.

--
-- Data for Name: csld_csld_group; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_csld_group (id, image, name) FROM stdin;
1	\N	CoM
2	3	MF
3	2	PbN
4	1	Larp
\.

SELECT pg_catalog.setval('csld_group_id_seq', 4, false);

--
-- Data for Name: csld_csld_user; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_csld_user (id, image, password, role, person, fb_user) FROM stdin;
1	1	heslo	2	1	\N
2	2	heslo2	1	2	\N
3	3	hesl1o	1	3	\N
4	2	hesnlo	1	4	\N
5	1	heso	3	5	\N
\.


--
-- Data for Name: csld_email_authentication; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_email_authentication (id, auth_token, user_id) FROM stdin;
1	Token1	1
2	Token2	2
\.


--
-- Name: csld_email_authentication_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_email_authentication_id_seq', 2, false);


--
-- Data for Name: csld_fb_user; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_fb_user (id, fb_token, id_csld_user) FROM stdin;
1	Token1	1
2	Token2	2
\.


--
-- Name: csld_fb_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_fb_user_id_seq', 2, false);


--
-- Data for Name: csld_game; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_game (id, name, description, year, web, hours, days, players, men_role, women_role, both_role, added, video, image) FROM stdin;
1	Bodlák	Popis	2008	www.bodlak.cz	4	1	12	7	3	0	2013-05-01 17:34:48.72	1	1
2	Hra 2	Popis 1	2009	www.hra.cz	3	2	10	4	3	1	2013-06-02 15:35:48.72	2	2
3	Hra 3	Popis 1	2010	www.test.cz	2	1	22	3	5	2	2013-07-03 14:44:48.72	1	1
4	Hra 4	Pop	2011	www.pom.cz	6	0	42	8	5	3	2013-08-04 12:31:48.72	3	3
5	Hra 5	Popi	2012	www.bod.cz	8	0	17	5	4	4	2013-09-05 10:14:48.72	1	1
\.

SELECT pg_catalog.setval('csld_game_id_seq', 5, false);

--
-- Data for Name: csld_game_has_author; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_game_has_author (id_csld_user, id_game) FROM stdin;
1	1
2	2
3	3
2	4
4	5
5	1
\.


--
-- Data for Name: csld_game_has_group; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_game_has_group (id_group, id_game) FROM stdin;
1	1
2	2
1	3
\.


--
-- Data for Name: csld_game_has_label; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_game_has_label (id_label, id_game) FROM stdin;
1	1
2	2
1	3
\.


--
-- Data for Name: csld_game_has_photo; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_game_has_photo (id_photo, id_game) FROM stdin;
1	1
2	2
1	3
\.


--
-- Data for Name: csld_group_has_administrator; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_group_has_administrator (id_group, id_csld_user) FROM stdin;
1	1
2	2
1	3
\.


--
-- Data for Name: csld_group_has_members; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_group_has_members (group_id, user_id, from_date, to_date) FROM stdin;
1	1	2013-05-01 17:34:48.72	2013-05-01 19:34:48.72
2	2	2013-05-01 17:34:48.72	2013-05-02 17:34:48.72
1	3	2013-05-01 17:34:48.72	2013-05-03 17:34:48.72
\.


--
-- Data for Name: csld_image; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_image (id, path) FROM stdin;
1	/files/upload/portret2.jpg
2	/files/upload/portret.jpg
3	/files/upload/bodlakLogo.png
\.


--
-- Name: csld_image_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_image_id_seq', 3, true);


--
-- Data for Name: csld_label; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_label (id, name, description, is_required, is_authorized, added_by) FROM stdin;
1	Komorni	\N	f	f	1
2	Opakova	\N	f	t	2
3	Skolni 	\N	f	t	3
4	Test 1 	\N	f	f	4
5	Dalsi 1	\N	f	f	5
\.


--
-- Name: csld_label_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_label_id_seq', 5, true);


--
-- Data for Name: csld_person; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_person (id, name, email, nickname, birth_date, city, description) FROM stdin;
1	Jakub Balhar	balhar.jakub@gmail.com	Balda	1988-01-14	Praha	\N
2	Tomáš Vokoun	vokoun.tomas@gmail.com	Vokis	1989-02-15	Praha	\N
3	Petr Vošahlk	balhar.jakub@gmail.com	PetrV	1980-03-16	Praha	\N
4	David Krtekd	david.krtekd@gmail.com	David	1998-04-17	Praha	\N
5	Pavel Hladík	hladik.pavel@gmail.com	Pavel	1986-05-18	Praha	\N
\.


--
-- Name: csld_person_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_person_id_seq', 5, true);


--
-- Data for Name: csld_photo; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_photo (id, image, author, version) FROM stdin;
1	3	3	1
2	2	4	2
3	1	5	3
\.


--
-- Name: csld_photo_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_photo_id_seq', 3, false);


--
-- Data for Name: csld_rating; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_rating (user_id, game_id, rating) FROM stdin;
1	1	5
2	2	10
3	2	8
\.


--
-- Name: csld_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_user_id_seq', 5, true);


--
-- Data for Name: csld_user_played_game; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_user_played_game (user_id, game_id, state) FROM stdin;
1	1	0
2	1	1
3	2	1
4	4	0
\.


--
-- Data for Name: csld_video; Type: TABLE DATA; Schema: public; Owner: csld
--

COPY csld_video (id, path, type) FROM stdin;
1	video1	0
2	video2	1
3	video3	1
\.


--
-- Name: csld_video_id_seq; Type: SEQUENCE SET; Schema: public; Owner: csld
--

SELECT pg_catalog.setval('csld_video_id_seq', 2, false);


--
-- Name: csld_comment_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_comment
    ADD CONSTRAINT csld_comment_pk PRIMARY KEY (user_id, game_id);


--
-- Name: csld_email_authentication_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_email_authentication
    ADD CONSTRAINT csld_email_authentication_pk PRIMARY KEY (id);


--
-- Name: csld_fb_user_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_fb_user
    ADD CONSTRAINT csld_fb_user_pk PRIMARY KEY (id);


--
-- Name: csld_group_has_members_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_group_has_members
    ADD CONSTRAINT csld_group_has_members_pk PRIMARY KEY (group_id, user_id, from_date, to_date);


--
-- Name: csld_group_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_csld_group
    ADD CONSTRAINT csld_group_pk PRIMARY KEY (id);


--
-- Name: csld_person_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_person
    ADD CONSTRAINT csld_person_pk PRIMARY KEY (id);


--
-- Name: csld_photo_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_photo
    ADD CONSTRAINT csld_photo_pk PRIMARY KEY (id);


--
-- Name: csld_rating_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_rating
    ADD CONSTRAINT csld_rating_pk PRIMARY KEY (user_id, game_id);


--
-- Name: csld_user_60604_uq; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_fb_user
    ADD CONSTRAINT csld_user_60604_uq UNIQUE (id_csld_user);


--
-- Name: csld_user_id_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_csld_user
    ADD CONSTRAINT csld_user_id_pk PRIMARY KEY (id);


--
-- Name: csld_user_played_game_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_user_played_game
    ADD CONSTRAINT csld_user_played_game_pk PRIMARY KEY (user_id, game_id);


--
-- Name: game_has_author_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_game_has_author
    ADD CONSTRAINT game_has_author_pk PRIMARY KEY (id_csld_user, id_game);


--
-- Name: game_has_group_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_game_has_group
    ADD CONSTRAINT game_has_group_pk PRIMARY KEY (id_group, id_game);


--
-- Name: game_has_label_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_game_has_label
    ADD CONSTRAINT game_has_label_pk PRIMARY KEY (id_label, id_game);


--
-- Name: game_has_photo_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_game_has_photo
    ADD CONSTRAINT game_has_photo_pk PRIMARY KEY (id_photo, id_game);


--
-- Name: game_id_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_game
    ADD CONSTRAINT game_id_pk PRIMARY KEY (id);


--
-- Name: group_has_administrator_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_group_has_administrator
    ADD CONSTRAINT group_has_administrator_pk PRIMARY KEY (id_group, id_csld_user);


--
-- Name: image_id_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_image
    ADD CONSTRAINT image_id_pk PRIMARY KEY (id);


--
-- Name: label_id_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_label
    ADD CONSTRAINT label_id_pk PRIMARY KEY (id);


--
-- Name: video_id_pk; Type: CONSTRAINT; Schema: public; Owner: csld; Tablespace: 
--

ALTER TABLE ONLY csld_video
    ADD CONSTRAINT video_id_pk PRIMARY KEY (id);


--
-- Name: csld_only_one_comment_trg; Type: TRIGGER; Schema: public; Owner: csld
--

CREATE TRIGGER csld_only_one_comment_trg BEFORE INSERT ON csld_comment FOR EACH ROW EXECUTE PROCEDURE csld_only_one_comment();


--
-- Name: csld_only_one_rating_trg; Type: TRIGGER; Schema: public; Owner: csld
--

CREATE TRIGGER csld_only_one_rating_trg BEFORE INSERT ON csld_rating FOR EACH ROW EXECUTE PROCEDURE csld_only_one_rating();


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
    ADD CONSTRAINT csld_photo_person_fk FOREIGN KEY (author) REFERENCES csld_person(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


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
-- Name: csld_user_fb_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_csld_user
    ADD CONSTRAINT csld_user_fb_user_fk FOREIGN KEY (fb_user) REFERENCES csld_fb_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_fb_user
    ADD CONSTRAINT csld_user_fk FOREIGN KEY (id_csld_user) REFERENCES csld_csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE SET NULL;


--
-- Name: csld_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_game_has_author
    ADD CONSTRAINT csld_user_fk FOREIGN KEY (id_csld_user) REFERENCES csld_csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_user_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_group_has_administrator
    ADD CONSTRAINT csld_user_fk FOREIGN KEY (id_csld_user) REFERENCES csld_csld_user(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: csld_user_person_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_csld_user
    ADD CONSTRAINT csld_user_person_fk FOREIGN KEY (person) REFERENCES csld_person(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


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

ALTER TABLE ONLY csld_game_has_photo
    ADD CONSTRAINT game_fk FOREIGN KEY (id_game) REFERENCES csld_game(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


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
    ADD CONSTRAINT label_fk FOREIGN KEY (id_label) REFERENCES csld_label(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: photo_fk; Type: FK CONSTRAINT; Schema: public; Owner: csld
--

ALTER TABLE ONLY csld_game_has_photo
    ADD CONSTRAINT photo_fk FOREIGN KEY (id_photo) REFERENCES csld_photo(id) MATCH FULL ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- PostgreSQL database dump complete
--

