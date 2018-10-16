insert into user_details (id, ssn, first_name, surname, certificate_type,email) values (user_details_id.nextval, '00101281985087818823', 'Oksana ', 'Moroziuk', 'PROFESSIONAL_PERSON','oksana.moroziuk-ext@luxtrust.lu');
insert into ref_user_role (user_id, role_id) values (user_details_id.currval, (select id from role where ROLE_TYPE='FLOWERS_ADMIN'));

insert into user_details (id, ssn, first_name, surname, certificate_type,email,language_id) values (user_details_id.nextval, '10101281985087818823', 'Ivan ', 'Skrypka', 'PROFESSIONAL_PERSON','ivan.skrypka-ext@luxtrust.lu', (select id from LANGUAGE where TWO_CHARS_CODE='en'));
insert into ref_user_role (user_id, role_id) values (user_details_id.currval, (select id from role where ROLE_TYPE='FLOWERS_ADMIN'));

insert into user_details (id, ssn, first_name, surname, certificate_type,email) values (user_details_id.nextval, '20101281985087818823', 'Antonina ', 'Galyant', 'PROFESSIONAL_PERSON','antonina.galyant-ext@luxtrust.lu');
insert into ref_user_role (user_id, role_id) values (user_details_id.currval, (select id from role where ROLE_TYPE='FLOWERS_ADMIN'));

insert into user_details (id, ssn, first_name, surname, certificate_type,email) values (user_details_id.nextval, '00102343731846760158', 'Aurelie', 'Bayard', 'PROFESSIONAL_PERSON','aurelie.bayard@luxtrust.lu');
insert into ref_user_role (user_id, role_id) values (user_details_id.currval, (select id from role where role_type='FLOWERS_ADMIN'));

insert into user_details (id, ssn, first_name, surname, certificate_type,email) values (user_details_id.nextval, '00106022831846873562', 'Glauber', 'Santos', 'PROFESSIONAL_PERSON','glauber.santos@luxtrust.lu');
insert into ref_user_role (user_id, role_id) values (user_details_id.currval, (select id from role where role_type='FLOWERS_ADMIN'));

commit;

