alter table csld_rating add column added timestamp DEFAULT CURRENT_TIMESTAMP;
update csld_rating set added = CURRENT_TIMESTAMP;