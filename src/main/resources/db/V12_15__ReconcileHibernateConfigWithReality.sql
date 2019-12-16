ALTER TABLE event ALTER COLUMN amountofplayers TYPE int4 USING amountofplayers::integer;
create sequence csld_label_id_seq;
create sequence csld_plus_one_id_seq;
create sequence csld_similar_games_id_seq;