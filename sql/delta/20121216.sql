insert into resource (id, path, default_path) values (DEFAULT, '/ajax/getLoggedBox', '/');

insert into role_has_resource (role_id, resource_id) values (1,24);
insert into role_has_resource (role_id, resource_id) values (2,24);
insert into role_has_resource (role_id, resource_id) values (3,24);
insert into role_has_resource (role_id, resource_id) values (4,24);
insert into role_has_resource (role_id, resource_id) values (5,24);

insert into resource (id, path, default_path) values (DEFAULT, '/ajax/getAuthors', '/');

insert into role_has_resource (role_id, resource_id) values (1,25);
insert into role_has_resource (role_id, resource_id) values (2,25);
insert into role_has_resource (role_id, resource_id) values (3,25);
insert into role_has_resource (role_id, resource_id) values (4,25);
insert into role_has_resource (role_id, resource_id) values (5,25);

insert into resource (id, path, default_path) values (DEFAULT, '/ajax/getLabels', '/');

insert into role_has_resource (role_id, resource_id) values (1,26);
insert into role_has_resource (role_id, resource_id) values (2,26);
insert into role_has_resource (role_id, resource_id) values (3,26);
insert into role_has_resource (role_id, resource_id) values (4,26);
insert into role_has_resource (role_id, resource_id) values (5,26);

insert into resource (id, path, default_path) values (DEFAULT, '/ajax/editGame', '/');

insert into role_has_resource (role_id, resource_id) values (2,27);
insert into role_has_resource (role_id, resource_id) values (3,27);
insert into role_has_resource (role_id, resource_id) values (4,27);
insert into role_has_resource (role_id, resource_id) values (5,27);

CREATE SEQUENCE csld_game_has_group_id_seq;

CREATE TABLE game_has_group (
  id int NOT NULL UNIQUE DEFAULT NEXTVAL('csld_game_has_group_id_seq'),
  game_id int NOT NULL,
  group_id int NOT NULL,

  FOREIGN KEY (game_id) REFERENCES game(id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY (group_id) REFERENCES csld_group(id) ON UPDATE CASCADE ON DELETE RESTRICT,

  PRIMARY KEY(id)
);