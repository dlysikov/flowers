alter table requestor modify (ssn varchar2(50 char));
alter table requestor rename column ssn to csn;