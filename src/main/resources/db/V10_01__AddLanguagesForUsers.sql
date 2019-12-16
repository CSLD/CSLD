create table csld_language(
  language varchar(10) not null,

  PRIMARY KEY (language)
);

insert into csld_language values('');

create table csld_user_has_languages(
  id_user integer not null REFERENCES csld_csld_user(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
  language varchar(10) not null REFERENCES csld_language(language) ON UPDATE RESTRICT ON DELETE RESTRICT,

  PRIMARY KEY (id_user, language)
);