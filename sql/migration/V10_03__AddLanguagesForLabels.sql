create sequence csld_label_has_languages_id_seq;

create table csld_label_has_languages (
  id integer not null DEFAULT nextval('csld_label_has_languages_id_seq'),
  id_label int not null REFERENCES csld_label(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
  language varchar(10) not null REFERENCES csld_language(language) ON UPDATE RESTRICT ON DELETE RESTRICT,
  name VARCHAR,
  description VARCHAR,

  PRIMARY KEY (id)
);

insert into csld_label_has_languages(id_label, language, name, description)
  select id, 'cs' as lang, name, description from csld_label;

ALTER TABLE csld_label ALTER COLUMN name DROP NOT NULL;
ALTER TABLE csld_label ALTER COLUMN description DROP NOT NULL;