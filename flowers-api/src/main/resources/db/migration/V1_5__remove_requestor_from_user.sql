alter table user_details drop constraint t_user_details_f_service;
alter table user_details drop column requestor_id;
commit;