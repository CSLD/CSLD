alter table event add column added_by INT;
alter table event add column web text;
alter table event add column deleted boolean default false;

ALTER TABLE event ADD CONSTRAINT event_added_by FOREIGN KEY (added_by) REFERENCES csld_csld_user (id) MATCH FULL;