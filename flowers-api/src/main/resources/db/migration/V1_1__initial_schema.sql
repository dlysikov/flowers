create sequence country_id start with 1 INCREMENT BY 1;
create table country (
  id number(19,0) not null,
  country_code varchar2(255 char) not null,
  primary key (id)
);
alter table country add constraint t_country_u_country_code unique (country_code);

create sequence nationality_id start with 1 INCREMENT BY 1;
create table nationality (
  id number(19,0) not null,
  nationality_code varchar2(255 char) not null,
  primary key (id)
);
alter table nationality add constraint t_nationality_u_code unique (nationality_code);

create sequence language_id start with 1 INCREMENT BY 1;
create table language (
  id number(19,0) not null,
  two_chars_code varchar2(255 char) not null,
  use_by_default number(1,0) DEFAULT 0,
  primary key (id)
);
alter table language add constraint t_language_u_code unique (two_chars_code);

create sequence role_id start with 1 INCREMENT BY 1;
create table role (
  id number(19,0) not null,
  role_type varchar2(255 char),
  primary key (id)
);
alter table role add constraint t_role_u_role_type unique (role_type);

create sequence country_vat_config_id start with 1 INCREMENT BY 1;
create table country_vat_config (
  id number(19,0) not null,
  country_id number(19,0) not null,
  vat_pattern varchar2(100 char) not null,
  primary key (id)
);
alter table country_vat_config add constraint t_country_vat_conf_f_country foreign key (country_id) references country;

create sequence requestor_config_id start with 1 INCREMENT BY 1;
create table requestor_config (
  id number(19,0) not null,
  order_validation_page_ttl number(19,0),
  remote_id number(1,0) DEFAULT 0,
  response_url varchar2(500 char),
  short_flow number(1,0) DEFAULT 0,
  primary key (id)
);

create sequence requestor_id start with 1 increment by 1;
create table requestor (
  id number(19,0) not null,
  service_name varchar2(255 char) not null,
  ssn number(19,0),
  config_id number(19,0) not null,
  primary key (id)
);
alter table requestor add constraint t_requestor_u_name unique (service_name);
alter table requestor add constraint t_requestor_u_ssn unique (ssn);
alter table requestor add constraint t_requestor_f_config_id foreign key (config_id) references requestor_config;

create sequence unit_id start with 1 increment by 1;
create table unit (
  id number(19,0) not null,
  city varchar2(25 char) not null,
  common_name varchar2(50 char) not null,
  company_name varchar2(50 char) not null,
  email varchar2(100 char),
  identifier_type varchar2(50 char) not null,
  identifier_value varchar2(15 char) not null,
  phone_number varchar2(20 char),
  post_code varchar2(15 char),
  street_and_house_no varchar2(35 char),
  country_id number(19,0),
  requestor_id number(19,0),
  primary key (id)
);
create index unit_identifier_index on unit (identifier_type, identifier_value);
create index unit_identifier_value on unit (identifier_value);
create index unit_common_name on unit (common_name);
alter table unit add constraint t_unit_f_country foreign key (country_id) references country;
alter table unit add constraint t_unit_f_requestor foreign key (requestor_id) references requestor;

create sequence user_details_id start with 1 INCREMENT BY 1;
create table user_details (
  id number(19,0) not null,
  certificate_type varchar2(255 char),
  email varchar2(255 char) not null,
  first_name varchar2(255 char) not null,
  ssn varchar2(255 char) not null,
  surname varchar2(255 char) not null,
  language_id number(19,0),
  requestor_id number(19,0),
  unit_id number(19,0),
  nationality_id number(19,0),
  birth_date date,
  phone_number varchar(20 char),
  primary key (id)
);
alter table user_details add constraint t_user_details_u_ssn unique (ssn);
alter table user_details add constraint t_user_details_f_language foreign key (language_id) references language;
alter table user_details add constraint t_user_details_f_service foreign key (requestor_id) references requestor;
alter table user_details add constraint t_user_details_f_unit foreign key (unit_id) references unit;
alter table user_details add constraint t_user_d_f_nationalities foreign key (nationality_id) references nationality;

create table ref_user_role (
  user_id number(19,0) not null,
  role_id number(19,0) not null,
  primary key (user_id, role_id)
);
alter table ref_user_role add constraint t_user_role_f_role foreign key (role_id) references role;
alter table ref_user_role add constraint t_user_role_f_user_details foreign key (user_id) references user_details;

create sequence delivery_address_id start with 1 INCREMENT BY 1;
create table delivery_address (
  id number(19,0) not null,
  address_line_2 varchar2(35 char),
  street_and_house_no varchar2(255 char),
  city varchar2(35 char),
  company varchar2(50 char),
  name_of_addressee varchar2(100 char),
  post_code varchar2(35 char),
  country_id number(19,0),
  primary key (id)
);
alter table delivery_address add constraint t_delivery_address_f_country foreign key (country_id) references country;

create sequence holder_id start with 1 INCREMENT BY 1;
create table holder (
  id number(19,0) not null,
  activation_code varchar2(5 char),
  birth_date date,
  is_publiched_cert_in_directory number(1,0) default 0,
  certificate_level varchar2(255 char),
  certificate_type varchar2(255 char),
  email varchar2(100 char),
  second_email varchar2(100 char),
  first_name varchar2(60 char),
  notify_email varchar2(100 char),
  phone_number varchar2(20 char),
  role_type varchar2(255 char),
  surname varchar2(60 char),
  nationality_id number(19,0),
  requestor_id number(19,0),
  primary key (id)
);
alter table holder add constraint t_holder_f_nationality foreign key (nationality_id) references nationality;
alter table holder add constraint t_holder_f_requestor foreign key (requestor_id) references requestor;
create index t_holder_duplicate on holder (first_name, surname, notify_email);
create index t_holder_first_name on holder (first_name);
create index t_holder_surname on holder (surname);
create index t_holder_notify_email on holder (notify_email);

create sequence document_id start with 1 INCREMENT BY 1;
create table document (
  id number(19,0) not null,
  file_content blob not null,
  file_name varchar2(255 char) not null,
  holder_id number(19,0) not null,
  primary key (id)
);
alter table document add constraint t_document_f_holder foreign key (holder_id) references holder;

create sequence order_id start with 1 increment by 1;
create table orders (
  id number(19,0) not null,
  accepted_gtc number(1,0) DEFAULT 0,
  lrs_order_number varchar2(255 char),
  publish number(1,0) DEFAULT 0,
  ssn varchar2(255 char),
  status varchar2(255 char),
  unit_id number(19,0),
  primary key (id)
);
create index status_index on orders (status);
create index lrs_order_number_index on orders (lrs_order_number);
alter table orders add constraint t_orders_f_unit foreign key (unit_id) references unit;

create sequence certificate_id start with 1 increment by 1;
create table certificate (
  id number(19,0) not null,
  csn varchar2(255 char),
  order_id number(19,0) not null,
  primary key (id)
);
alter table certificate add constraint t_certificate_f_order_id foreign key (order_id) references orders;

create table response_api (
  user_external_id  varchar2(100 char) not null,
  ssn               varchar2(255 char),
  service_ssn       number(19,0),
  status            varchar2(255 char),
  device            varchar2(255 char),
  modified_date     date,
  primary key (user_external_id)
);

create table certificate_order (
  department varchar2(50 char),
  device varchar2(255 char),
  remote_id number(1,0) default 0,
  request_date date,
  token_serial_number varchar2(255 char),
  user_external_id varchar2(100 char),
  id number(19,0) not null,
  address_id number(19,0),
  holder_id number(19,0),
  issuer_id number(19,0) default null null,
  primary key (id)
);
alter table certificate_order add constraint t_certificate_order_f_delivery foreign key (address_id) references delivery_address;
alter table certificate_order add constraint t_certificate_order_f_holder foreign key (holder_id) references holder;
alter table certificate_order add constraint t_certificate_order_f_user foreign key (issuer_id) references user_details;
alter table certificate_order add constraint t_certificate_order_f_orders foreign key (id) references orders;
alter table certificate_order add constraint t_certificate_order_f_response foreign key (user_external_id) references response_api;

create sequence order_user_validate_page_id start with 1 INCREMENT BY 1;
create table order_user_validate_page (
  id number(19,0) not null,
  expiration_time date not null,
  page_hash varchar2(255 char) not null,
  order_id number(19,0) not null,
  mobile_validation_code varchar(7 char) not null,
  primary key (id)
);
alter table order_user_validate_page add constraint t_order_user_u_page_hash unique (page_hash);
alter table order_user_validate_page add constraint t_order_user_f_orders foreign key (order_id) references orders;
create index t_validation_page_mob_code on order_user_validate_page (page_hash, mobile_validation_code);

create table eseal_order (
  eseal_administrator_email varchar2(255 char),
  organisational_unit varchar2(255 char),
  id number(19,0) not null,
  primary key (id)
);
alter table eseal_order add constraint t_eseal_order_f_orders foreign key (id) references orders;

create table ref_eseal_order_user (
	order_id number(19,0) not null,
	user_id number(19,0) not null
);
alter table ref_eseal_order_user add constraint t_ref_eseal_user_f_user foreign key (user_id) references user_details;
alter table ref_eseal_order_user add constraint t_ref_eseal_user_f_order foreign key (order_id) references eseal_order;

commit;

