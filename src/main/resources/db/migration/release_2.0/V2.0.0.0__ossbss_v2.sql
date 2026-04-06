# SQL for OSSBSS Version 2

#Mohit 13-07-2021
alter table tblmstate
    add is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

# Vyom 16-07-2021
alter table tblpartners
    modify column status varchar(255);

#Utsav 21-07-2021 tblcustchargedtls
ALTER TABLE tblcustchargedtls
    ADD is_used BOOL NULL;
ALTER TABLE tblcustchargedtls
    ADD purchase_entity_id BIGINT NULL;

#Utsav 21-07-2021 tblippooldtls
ALTER TABLE tblippooldtls
    ADD allocated_id BIGINT NULL;
ALTER TABLE tblippooldtls
    ADD unblock_time DATETIME NULL;

#Utsav 21-07-2021 tblipallocationdtls
CREATE TABLE tblipallocationdtls
(
    id                    SERIAL PRIMARY KEY,
    is_delete             tinyint(1)   NOT NULL,
    CREATEDATE            timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    LASTMODIFIEDDATE      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATEDBYSTAFFID      bigint       NOT NULL,
    LASTMODIFIEDBYSTAFFID varchar(100) NOT NULL,
    cust_id               bigint                DEFAULT NULL,
    terminated_date       datetime              DEFAULT NULL,
    is_system_updated     tinyint(1)            DEFAULT NULL,
    termination_reason    varchar(100)          DEFAULT NULL
);

#Utsav 21-07-2021 - Already Executed
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Minutes', 'Minutes', 'quotaTypeDID', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Seconds', 'Seconds', 'quotaTypeDID', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('DID', 'DID', 'voiceQuotaType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Intercom', 'Intercom', 'voiceQuotaType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Both', 'VoiceBoth', 'voiceQuotaType', 'Active');

# Mohit 21-07-2021
ALTER TABLE tblcases
    ADD partnerid BIGINT UNSIGNED REFERENCES tblpartners (PARTNERID);

#Utsav 22-07-2021
ALTER TABLE tblmpostpaidplan
    ADD quotaunitdid varchar(50) NULL;
ALTER TABLE tblmpostpaidplan
    ADD quotaunitintercom varchar(50) NULL;
ALTER TABLE tblcustquotadtls
    ADD did_quota_unit varchar(50) NULL;
ALTER TABLE tblcustquotadtls
    ADD intercom_quota_unit varchar(50) NULL;

#Utsav 23-07-2021
ALTER TABLE tblippooldtls
    ADD block_by_cust_id bigint NULL;

#Jaymin 23-07-2021
create table tblmpartnerledger
(
    partnerledger_id SERIAL PRIMARY KEY,
    totaldue         decimal(20, 4),
    totalpaid        decimal(20, 4),
    partner_id       bigint(20) unsigned,
    CREATEDATE       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    LASTMODIFIEDDATE TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted       BOOLEAN   NOT NULL DEFAULT false,
    foreign KEY (partner_id) references tblpartners (PARTNERID)
);
create table tblmpartnerledgerdetails
(
    partnerledgerdtls_id SERIAL PRIMARY KEY,
    CREATEDATE           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transtype            varchar(10),
    transcategory        varchar(50),
    amount               decimal(20, 4)     DEFAULT 0.0,
    partner_id           bigint(20) unsigned,
    description          varchar(300)       DEFAULT NULL,
    is_deleted           BOOLEAN   NOT NULL DEFAULT false,
    foreign KEY (partner_id) references tblpartners (PARTNERID)
);


create table tblmpartnerpayment
(
    partnerpaymentid SERIAL PRIMARY KEY,
    CREATEDATE       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    transcategory    varchar(50),
    payment_mode     varchar(50),
    refno            varchar(100),
    partner_id       bigint(20) unsigned,
    amount           decimal(20, 4)     DEFAULT 0.0,
    chequenumber     varchar(20),
    chequedate       TIMESTAMP NULL     DEFAULT null,
    is_deleted       BOOLEAN   NOT NULL DEFAULT false,
    remarks          varchar(100),
    foreign KEY (partner_id) references tblpartners (PARTNERID)
);

ALTER TABLE tblpartners
    DROP COLUMN balance;

ALTER TABLE tblmpartnerpayment
    ADD COLUMN paymentdate TIMESTAMP NULL DEFAULT null;
ALTER TABLE tblmpartnerpayment
    ADD column bank_name varchar(100);
ALTER TABLE tblmpartnerpayment
    ADD column branch_name varchar(100);


#Jaymin 26-07-2021
alter table tblpricebookplandtls
    add column is_deleted BOOLEAN not null default false;

#Mohit 27-07-2021
create table tbl_payment_gateway
(
    pgid                  SERIAL primary key,
    returnurl             varchar(150),
    pgurl                 varchar(150),
    name                  varchar(100),
    partnerenableflag     boolean not null default false,
    userenableflag        boolean not null default false,
    is_deleted            Boolean NOT NULL DEFAULT FALSE,
    createdbystaffid      NUMERIC(20),
    createdate            TIMESTAMP        DEFAULT CURRENT_TIMESTAMP NOT NULL,
    lastmodifiedbystaffid NUMERIC(20),
    lastmodifieddate      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

#Utsav 29-07-2021
CREATE TABLE tbl_order_details
(
    orderid               SERIAL PRIMARY KEY,
    entityid              bigint(20)          DEFAULT NULL,
    pgid                  bigint(20) unsigned DEFAULT NULL,
    ordertype             varchar(100)        DEFAULT NULL,
    finalamount           decimal(10, 2)      DEFAULT NULL,
    basicamount           decimal(10, 2)      DEFAULT NULL,
    taxamount             decimal(10, 2)      DEFAULT NULL,
    orderdesc             text,
    createdbystaffid      decimal(20, 0)      DEFAULT NULL,
    createdate            timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lastmodifiedbystaffid decimal(20, 0)      DEFAULT NULL,
    lastmodifieddate      timestamp  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cust_id               bigint(20) NOT NULL,
    partner_id            bigint(20) NOT NULL,
    is_deleted            tinyint(1) NOT NULL DEFAULT '0',
    KEY pgid (pgid),
    CONSTRAINT tbl_order_details_ibfk_1 FOREIGN KEY (pgid) REFERENCES tbl_payment_gateway (pgid)
);

#Mohit 27-07-2021
create table tbl_purchase_details
(
    purchaseid            SERIAL primary key,
    orderid               bigint unsigned not null,
    custid                bigint unsigned,
    pgid                  bigint unsigned not null,
    partnerid             bigint unsigned,
    amount                decimal(10, 2),
    paymentstatus         varchar(100),
    transid               varchar(150),
    pg_res_status         varchar(100),
    purchase_status       varchar(100),
    purchasedate          DATETIME,
    trans_res_date        DATETIME,
    is_deleted            Boolean         NOT NULL DEFAULT FALSE,
    createdbystaffid      NUMERIC(20),
    createdate            TIMESTAMP                DEFAULT CURRENT_TIMESTAMP NOT NULL,
    lastmodifiedbystaffid NUMERIC(20),
    lastmodifieddate      TIMESTAMP                DEFAULT CURRENT_TIMESTAMP,
    foreign KEY (orderid) references tbl_order_details (orderid),
    foreign KEY (custid) references tblcustomers (custid),
    foreign KEY (pgid) references tbl_payment_gateway (pgid),
    foreign KEY (partnerid) references tblpartners (partnerid)
);

ALTER TABLE tbl_purchase_details
    ADD COLUMN pgtransid bigint null;


CREATE TABLE tbl_payment_gateway_response
(
    id                    SERIAL PRIMARY KEY,
    pg_id                 bigint(20) NOT NULL,
    purchase_id           bigint(20) NOT NULL,
    response              text,
    response_date         datetime        DEFAULT NULL,
    is_deleted            tinyint(1)      DEFAULT NULL,
    createdbystaffid      bigint(20)      DEFAULT NULL,
    createdate            timestamp  NULL DEFAULT NULL,
    lastmodifieddate      timestamp  NULL DEFAULT NULL,
    lastmodifiedbystaffid bigint(20)      DEFAULT NULL
);

#Mohit 29-07-2021
alter table tblcustdocdetails
    add column doc_sub_type varchar(100);

alter table tblcustdocdetails
    add column remark varchar(100);

#Utsav 29-07-2021
ALter table tbl_payment_gateway
    add column prefix varchar(100);

#Utsav 30-07-2021
ALter table tbl_payment_gateway
    add column status varchar(10);

INSERT INTO tbl_payment_gateway
values (1, '', 'https://test.payu.in/_payment', 'Payu-Money', 0, 1, 0, 1, '2021-07-29', 1, '2021-07-29', 'payu',
        'Active');

INSERT INTO tbl_payment_gateway
values (2, '', 'https://test.ccavenue.com', 'CCAvenue', 1, 0, 1, 1, '2021-07-29', 1, '2021-07-29', 'ccavenue',
        'Active');

#Already Fired
ALTER TABLE tbl_order_details
    MODIFY COLUMN partner_id bigint NULL;
ALTER TABLE tbl_order_details
    MODIFY COLUMN cust_id bigint NULL;

# Vyom 03-08-2021 - For Subscriber ACL
insert into tblroles (roleid, rolename, rstatus, is_delete)
values (8, "Subscriber", "Active", 0);

#Utsav 04-08-2021

alter table tbl_order_details
    add column balanced_used double null;

alter table tbl_order_details
    add column is_balance_used bool null;

# Vyom 06-08-2021
alter table tblroles
    add column sysrole Boolean not null default false;
update tblroles
set sysrole = TRUE
where roleid < 51;

#Utsav 06-08-2021
alter table tbl_order_details
    add column ledger_details_id bigint null;

alter table tbl_order_details
    add column is_settled bool null;

#jaymin 09-08-2021
alter table tblstaffuser add otp varchar(10);

alter table tblstaffuser add otpvalidate timestamp  NULL DEFAULT NULL;

alter table tblcustomers add otp varchar(10);

alter table tblcustomers add otpvalidate timestamp  NULL DEFAULT NULL;

#Utsav 10-08-2021
ALTER TABLE tblcustchargedtls ADD debitdocid BIGINT UNSIGNED NULL;
ALTER TABLE tblcustchargedtls ADD CONSTRAINT tblcustchargedtls_ibfk_4 FOREIGN KEY (debitdocid) REFERENCES tbltdebitdocument(debitdocumentid);

#UTSAV 12-08-2021
ALTER TABLE tblsubscriberupdates
    CHANGE textval entity_name longtext NULL;

CREATE TABLE communication (
id bigint(20) NOT NULL AUTO_INCREMENT,
email varchar(100) DEFAULT NULL,
subject varchar(100) DEFAULT NULL,
email_body text,
uuid varchar(100) NOT NULL,
destination varchar(12) DEFAULT NULL,
source varchar(10) DEFAULT NULL,
sms_message text,
template_id varchar(30) DEFAULT NULL,
channel varchar(100) DEFAULT NULL,
is_deleted tinyint(1) DEFAULT NULL,
CREATEDATE datetime DEFAULT NULL,
LASTMODIFIEDDATE datetime DEFAULT NULL,
createbyname varchar(100) DEFAULT NULL,
updatebyname varchar(100) DEFAULT NULL,
CREATEDBYSTAFFID bigint(20) DEFAULT NULL,
LASTMODIFIEDBYSTAFFID bigint(20) DEFAULT NULL,
is_sended tinyint(1) DEFAULT NULL,
error text,
PRIMARY KEY (id)
);

#Mohit 14-08-2021

drop table if exists tblteamusermapping;

create table tblteamusermapping(

    team_id    BIGINT UNSIGNED not NULL,

    staffid    BIGINT UNSIGNED not NULL

);

ALTER TABLE tblteamusermapping ADD FOREIGN KEY (team_id) REFERENCES tblteams (team_id);

ALTER TABLE tblteamusermapping ADD FOREIGN KEY (staffid) REFERENCES tblstaffuser (staffid);


#Mohit 17-08-2021
insert into tblroles (roleid, rolename, rstatus, is_delete,sysrole,CREATEDBYSTAFFID,LASTMODIFIEDBYSTAFFID)
values (9, 'PGUser', 'Active', 0,1,1,1);

alter table tblstaffuser add column sysstaff Boolean not null default false;

ALTER TABLE tblstaffuser AUTO_INCREMENT = 51;

update tblstaffuser
set sysstaff = TRUE
where staffid < 51;

insert into tblstaffuser (username,password,firstname,lastname,email,phone,sstatus,sysstaff,partnerid)
values ('pguser','Password@123','PG','USER','pguser@gmail.com','9876543210','Active',1,1);

insert into tblstaffrolerel (staffid,roleid) values ((select staffid from tblstaffuser t where t.username = 'pguser'),
(select roleid from tblroles where roleid  = 9));

ALTER TABLE tblstaffuser MODIFY COLUMN last_login_time timestamp NULL;

#Utsav 19-08-2021
ALTER TABLE tblipallocationdtls ADD pool_details_id BIGINT UNSIGNED NULL;
ALTER TABLE tblipallocationdtls ADD CONSTRAINT tblipallocationdtls_FK FOREIGN KEY (pool_details_id) REFERENCES tblippooldtls(pool_details_id);
ALTER TABLE tblcustchargedtls MODIFY COLUMN charge_date DATETIME NULL;
ALTER TABLE tblcustchargedtls MODIFY COLUMN startdate DATETIME NULL;
ALTER TABLE tblcustchargedtls MODIFY COLUMN enddate DATETIME NULL;

#Mohit 20-08-2021 #Already Fired
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Billing', 'Billing', 'templatetype', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Payment', 'Payment', 'templatetype', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Comission', 'Comission', 'templatetype', 'Active');

ALTER TABLE tblcases MODIFY COLUMN next_followup_date date NULL;
ALTER TABLE tblcases MODIFY COLUMN next_followup_time time NULL;

#Utsav 20-08-2021
ALTER TABLE tblcustpackagerel ADD offer_price DOUBLE NULL;
ALTER TABLE tblcustpackagerel ADD tax_amount DOUBLE NULL;

ALTER TABLE tblcustchargedtls ADD ippooldtlsid BIGINT UNSIGNED NULL;
ALTER TABLE tblcustchargedtls ADD CONSTRAINT tblcustchargedtls_FK FOREIGN KEY (ippooldtlsid) REFERENCES tblippooldtls(pool_details_id);

#Mohit 24-08-2021
alter table tblmcountry modify status varchar(50);
alter table tblmstate modify status varchar(50);
alter table tblmcity modify status varchar(50);
ALTER TABLE tblcases DROP FOREIGN KEY `tblcases_ibfk_2`;

#Jaymin 24-08-2021
alter table tblserverdetail add servertype varchar(50);
INSERT INTO tblserverdetail
(serverip, webport, status, created_on, lastmodified_on, is_delete, servertype)
VALUES('143.110.248.5', '20081', '1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'radius');

#Mohit 25-08-2021
ALTER TABLE tblteams ADD partnerid bigint REFERENCES tblpartners (PARTNERID);


#jaymin 25-08-2021
create table tbluser_disconnect(
	disuserid SERIAL primary key,
	remark varchar(200),
	req_type varchar(10),
	username varchar(100),
 	is_deleted  Boolean NOT NULL DEFAULT FALSE,
    createdbystaffid      NUMERIC(20),
    createdate            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    lastmodifiedbystaffid NUMERIC(20),
    lastmodifieddate      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    createbyname varchar(100) default null,
    updatebyname varchar(100) default null
);
create table tbluser_disconnect_details(
	disuserdtlid SERIAL primary key,
	sessionid varchar(50),
	NASIPAddress varchar(150),
	FramedIPAddress varchar(150),
	disuserid bigint unsigned,
	is_deleted  Boolean NOT NULL DEFAULT FALSE,
	foreign key(disuserid) references tbluser_disconnect(disuserid)
);
ALTER TABLE tbluser_disconnect_details add column is_deleted  Boolean NOT NULL DEFAULT false;

#Mohit 26-08-2021
ALTER TABLE tblippool add column default_pool_flag  Boolean NOT NULL DEFAULT false;
alter table tblcustomers drop column deposit;

#Jaymin 26-08-2021
alter table tblcustomers add column latitude varchar(50);
alter table tblcustomers add column longitude varchar(50);
alter table tblcustomers add column url varchar(100);

alter table tblcustomers add column gisCode varchar(50);
alter table tblcustomers CHANGE column gisCode  gis_code varchar(50);

#Mohit 1-9-2021
alter table tblmpostpaidplan add datacategory varchar(50);

#Mohit 04-09-2021
ALTER TABLE tbltdebitdocument ADD custchargeid BIGINT NULL;
ALTER TABLE tbltdebitdocument Change custchargeid cstchargeid bigint null;

#Jaymin 06-09-2021
alter table tblcharges add column saccode varchar(100);
