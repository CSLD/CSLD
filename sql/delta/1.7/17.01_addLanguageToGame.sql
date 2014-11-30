alter table csld_game add column lang varchar(32);

update csld_game set lang = 'cs';