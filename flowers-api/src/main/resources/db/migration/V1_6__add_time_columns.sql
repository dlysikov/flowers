create index t_holder_phone on holder (phone_number);
create index t_orders_ssn on orders (ssn);
create index t_cert_order_device on certificate_order (device);
create index t_eseal_order_orgunit on eseal_order (organisational_unit);

alter table delivery_address add created_time date;
alter table delivery_address add last_update_time date;

alter table certificate add created_time date;
alter table certificate add last_update_time date;

alter table document add created_time date;
alter table document add last_update_time date;

alter table holder add created_time date;
alter table holder add last_update_time date;

alter table orders add created_time date;
alter table orders add last_update_time date;

alter table order_user_validate_page add created_time date;
alter table order_user_validate_page add last_update_time date;

alter table unit add created_time date;
alter table unit add last_update_time date;

alter table requestor add created_time date;
alter table requestor add last_update_time date;

alter table requestor_config add created_time date;
alter table requestor_config add last_update_time date;

alter table response_api add created_time date;
alter table response_api add last_update_time date;

alter table user_details add created_time date;
alter table user_details add last_update_time date;