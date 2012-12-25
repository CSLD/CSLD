create table author_has_group(
  author_id int not null,
  group_id int not null,

  FOREIGN KEY (group_id) REFERENCES csld_group(id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY (author_id) REFERENCES author(author_id) ON UPDATE CASCADE ON DELETE RESTRICT,

  PRIMARY KEY (author_id, group_id)
);