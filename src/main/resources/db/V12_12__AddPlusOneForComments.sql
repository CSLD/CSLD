CREATE TABLE csld_comment_upvote (
  id    SERIAL PRIMARY KEY,
  comment_id integer,
  user_id  integer,
  added TIMESTAMP DEFAULT now()
);