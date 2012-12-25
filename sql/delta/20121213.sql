alter table game add column both_role int default 0;
alter table comment add column time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

insert into resource (id, path, default_path) values (DEFAULT, '/group', '/');

insert into role_has_resource (role_id, resource_id) values (1,23);
insert into role_has_resource (role_id, resource_id) values (2,23);
insert into role_has_resource (role_id, resource_id) values (3,23);
insert into role_has_resource (role_id, resource_id) values (4,23);
insert into role_has_resource (role_id, resource_id) values (5,23);