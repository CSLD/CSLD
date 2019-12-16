create sequence csld_group_has_languages_id_seq;

create table csld_group_has_languages (
  id integer not null DEFAULT nextval('csld_label_has_languages_id_seq'),
  id_group int not null REFERENCES csld_csld_group(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
  language varchar(10) not null REFERENCES csld_language(language) ON UPDATE RESTRICT ON DELETE RESTRICT,
  name VARCHAR,

  PRIMARY KEY (id)
);

insert into csld_group_has_languages(id_group, language, name)
  select id, 'cs' as lang, name from csld_csld_group;

ALTER TABLE csld_csld_group ALTER COLUMN name DROP NOT NULL;