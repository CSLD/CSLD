alter table csld_game add column deleted boolean;

update csld_game set deleted = false;