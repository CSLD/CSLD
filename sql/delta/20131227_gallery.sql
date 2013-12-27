alter table csld_photo drop column version;

alter table csld_photo add column orderSeq int default 0 not null;

alter table csld_photo add column description varchar(2048);

alter table csld_photo add column fullWidth int not null default 0;

alter table csld_photo add column fullHeight int not null default 0;

alter table csld_image add column contentType varchar(256);
