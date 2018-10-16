delete from REF_USER_ROLE where ROLE_ID=(select id from ROLE where ROLE_TYPE='LUXTRUST_ADMIN');
delete from REF_USER_ROLE where ROLE_ID=(select id from ROLE where ROLE_TYPE='GDPR_AGENT');
delete from role where ROLE_TYPE='LUXTRUST_ADMIN';
delete from role where ROLE_TYPE='GDPR_AGENT';
commit;