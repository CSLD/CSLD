drop table csld_group_has_members;

alter table csld_csld_group add column administrator_id INTEGER;
update csld_csld_group set administrator_id = subquery.id_user from (
    select id_user, id_group from csld_group_has_administrator) as subquery
  where csld_csld_group.id = subquery.id_group;

drop table csld_group_has_administrator;

