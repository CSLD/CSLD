alter table csld_csld_user add column default_lang VARCHAR(10);

alter table csld_csld_user add CONSTRAINT user_language_fk FOREIGN KEY (default_lang) references csld_language(language)
  MATCH SIMPLE;