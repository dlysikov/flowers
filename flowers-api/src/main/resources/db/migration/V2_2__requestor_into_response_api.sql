create sequence response_api_id start with 1 INCREMENT BY 1;

alter table RESPONSE_API add id number(19,0);
alter table RESPONSE_API add requestor_id number(19,0);

update RESPONSE_API set id = response_api_id.nextval;
update RESPONSE_API r set r.requestor_id=(select u.REQUESTOR_ID from CERTIFICATE_ORDER co JOIN ORDERS o ON O.ID=co.ID JOIN UNIT u on u.ID=o.UNIT_ID JOIN REQUESTOR req on REQ.ID=u.REQUESTOR_ID where co.USER_EXTERNAL_ID=r.USER_EXTERNAL_ID);
commit;

alter table CERTIFICATE_ORDER drop constraint t_certificate_order_f_response;

BEGIN
  FOR cur_rec IN (select CONSTRAINT_NAME as OBJECT_NAME, 'CONSTRAINT' as OBJECT_TYPE from all_cons_columns where table_name='RESPONSE_API' and COLUMN_NAME='USER_EXTERNAL_ID')
  LOOP
    BEGIN

      EXECUTE IMMEDIATE    'alter table RESPONSE_API DROP '
                           || cur_rec.object_type
                           || ' "'
                           || cur_rec.object_name
                           || '"';
    END;
  END LOOP;
END;

ALTER TABLE RESPONSE_API add constraint pk_response_api primary key (id);
ALTER TABLE RESPONSE_API modify(user_external_id varchar2(100 char) constraint not_null_user_ext_id not null);
ALTER TABLE RESPONSE_API modify(requestor_id number(19,0) constraint not_null_requestor_id not null);
ALTER TABLE RESPONSE_API add constraint t_response_api_req_ext_id unique (USER_EXTERNAL_ID, requestor_id);
create index t_cert_order_user_ext_id on certificate_order (user_external_id);