create sequence csld_news_id_seq;

create table csld_news (
  id integer not null DEFAULT nextval('csld_news_id_seq'),
  lang VARCHAR(20) not null,
  author_id integer not null,
  added TIMESTAMP not null,
  text TEXT not null,

  FOREIGN KEY (author_id) references csld_csld_user(id),
  PRIMARY KEY (id)
);