update requestor set ssn = null;
update response_api set SERVICE_SSN=null;
commit;

alter table requestor modify (ssn varchar(100));
alter table response_api modify(service_ssn varchar(100));
