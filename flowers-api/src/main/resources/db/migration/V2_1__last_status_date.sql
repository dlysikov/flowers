alter table orders add last_status_date date;
update orders set last_status_date = last_update_time;
commit;