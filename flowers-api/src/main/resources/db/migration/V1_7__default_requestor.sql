insert into requestor_config(id,order_validation_page_ttl,short_flow) values (requestor_config_id.nextval, 7200000, 0);
insert into requestor (id,service_name,config_id, ssn) values (requestor_id.nextval,'Default', requestor_config_id.currval, 10050042);

commit;