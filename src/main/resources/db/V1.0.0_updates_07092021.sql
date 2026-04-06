#Mohit 04-09-2021
alter table tblcustpackagerel
    add creditdocid bigint null;

#Jaymin 06-09-2021
alter table tblcharges
    add column saccode varchar(100);

#Vyom 06-09-2021
update tblstaffuser
set staffid=9
where staffid = 2;

#Mohit 08-09-2021
update tbl_payment_gateway
set is_deleted = 0
where pgid = 2;

ALTER TABLE tbltemplatemanagement MODIFY COLUMN status varchar(100) DEFAULT 'ACTIVE' NOT NULL;

#Utsav 08-09-2021
#PINCODE
insert into tblaclclass (classid, classname, dispname, disporder, operallid)
values (35, "com.adopt.apigw.modules.Pincode", "Pincode", 35, 212);

insert into tblacloperations (opid, classid, opname)
values (212, 35, "Pincode All");
insert into tblacloperations (opid, classid, opname)
values (213, 35, "Pincode View");
insert into tblacloperations (opid, classid, opname)
values (214, 35, "Pincode Add");
insert into tblacloperations (opid, classid, opname)
values (215, 35, "Pincode Edit");
insert into tblacloperations (opid, classid, opname)
values (216, 35, "Pincode Delete");

INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 35, 212);

insert into tblaclmenus (menuid, name, dispname, classid, parentid)
values (45, "Pincode", "Pincode", 35, 1);

#Utsav 08-09-2021
#AREA
insert into tblaclclass (classid, classname, dispname, disporder, operallid)
values (36, "com.adopt.apigw.modules.Area", "Area", 36, 217);

insert into tblacloperations (opid, classid, opname)
values (217, 36, "Area All");
insert into tblacloperations (opid, classid, opname)
values (218, 36, "Area View");
insert into tblacloperations (opid, classid, opname)
values (219, 36, "Area Add");
insert into tblacloperations (opid, classid, opname)
values (220, 36, "Area Edit");
insert into tblacloperations (opid, classid, opname)
values (221, 36, "Area Delete");

INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 36, 217);

insert into tblaclmenus (menuid, name, dispname, classid, parentid)
values (46, "Area", "Area", 36, 1);

#Utsav 08-09-2021
CREATE TABLE tblmpincode
(
    pincodeid             SERIAL primary key,
    pincode               varchar(10) NOT NULL,
    COUNTRYID             bigint unsigned      DEFAULT NULL,
    STATEID               bigint unsigned      DEFAULT NULL,
    CITYID                bigint unsigned      DEFAULT NULL,
    is_deleted            tinyint(1)  NOT NULL DEFAULT '0',
    createdbystaffid      decimal(20, 0)       DEFAULT NULL,
    createdate            timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lastmodifiedbystaffid decimal(20, 0)       DEFAULT NULL,
    lastmodifieddate      timestamp   NULL     DEFAULT CURRENT_TIMESTAMP,
    createbyname          varchar(100)         DEFAULT NULL,
    updatebyname          varchar(100)         DEFAULT NULL,
    status                varchar(100)         DEFAULT NULL
);

ALTER TABLE tblmpincode
    ADD CONSTRAINT tblmpincode_FK_1 FOREIGN KEY (CITYID) REFERENCES tblmcity (CITYID);
ALTER TABLE tblmpincode
    ADD CONSTRAINT tblmpincode_FK_2 FOREIGN KEY (STATEID) REFERENCES tblmstate (STATEID);
ALTER TABLE tblmpincode
    ADD CONSTRAINT tblmpincode_FK FOREIGN KEY (COUNTRYID) REFERENCES tblmcountry (COUNTRYID);


CREATE TABLE tblmarea
(
    areaid                SERIAL primary key,
    name                  varchar(100) NOT NULL,
    COUNTRYID             bigint unsigned       DEFAULT NULL,
    STATEID               bigint unsigned       DEFAULT NULL,
    is_deleted            tinyint(1)   NOT NULL DEFAULT '0',
    createdbystaffid      decimal(20, 0)        DEFAULT NULL,
    createdate            timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lastmodifiedbystaffid decimal(20, 0)        DEFAULT NULL,
    lastmodifieddate      timestamp    NULL     DEFAULT CURRENT_TIMESTAMP,
    createbyname          varchar(100)          DEFAULT NULL,
    updatebyname          varchar(100)          DEFAULT NULL,
    CITYID                bigint unsigned       DEFAULT NULL,
    status                varchar(100)          DEFAULT NULL,
    pincodeid             bigint unsigned       DEFAULT NULL
);

ALTER TABLE tblmarea
    ADD CONSTRAINT tblmarea_FK_1 FOREIGN KEY (CITYID) REFERENCES tblmcity (CITYID);
ALTER TABLE tblmarea
    ADD CONSTRAINT tblmarea_FK_2 FOREIGN KEY (STATEID) REFERENCES tblmstate (STATEID);
ALTER TABLE tblmarea
    ADD CONSTRAINT tblmarea_FK_3 FOREIGN KEY (pincodeid) REFERENCES tblmpincode (pincodeid);
ALTER TABLE tblmarea
    ADD CONSTRAINT tblmarea_FK FOREIGN KEY (COUNTRYID) REFERENCES tblmcountry (COUNTRYID);


alter table tblmsubscriberaddressrel
    drop column AREA;
alter table tblmsubscriberaddressrel
    drop column PINCODE;

alter table tblmsubscriberaddressrel
    add column PINCODEID bigint unsigned DEFAULT NULL;
alter table tblmsubscriberaddressrel
    add column AREAID bigint unsigned DEFAULT NULL;

ALTER TABLE tblmsubscriberaddressrel
    ADD CONSTRAINT tblmsubscriberaddressrel_FK FOREIGN KEY (PINCODEID) REFERENCES tblmpincode (pincodeid);
ALTER TABLE tblmsubscriberaddressrel
    ADD CONSTRAINT tblmsubscriberaddressrel_FK_1 FOREIGN KEY (AREAID) REFERENCES tblmarea (areaid);

#jaymin 08-09-2021
alter table tblradiusprofilecheckitm add column is_deleted  Boolean NOT NULL DEFAULT false;
alter table tblippool add column display_name varchar(100);

#Mohit 09-09-2021
create table tblcasereasonconfig
(
    config_id SERIAL PRIMARY KEY,
    staffid BIGINT UNSIGNED NOT NULL,
    reasonid BIGINT UNSIGNED NOT NULL,
    is_deleted BOOLEAN   NOT NULL DEFAULT false
);
ALTER TABLE tblcasereasonconfig ADD FOREIGN KEY (staffid) REFERENCES tblstaffuser (staffid);
ALTER TABLE tblcasereasonconfig ADD FOREIGN KEY (reasonid) REFERENCES tblcasereasons (reason_id);
ALTER TABLE tblcasereasonconfig ADD serviceareaid bigint REFERENCES tblservicearea (service_area_id);

ALTER TABLE tblcustomers DROP INDEX username;

#Jaymin 09-09-2021
alter table tblmpostpaidplan add column taxamount decimal(16,4);
alter table tblcharges add column taxamount decimal(16,4);
INSERT INTO tblclientservice(NAME, VALUE)VALUES('locationid', '2');


#Utsav 09-09-2021
ALTER TABLE tblnotification_repeat CHANGE subscirberid subscriberid bigint unsigned NULL;
ALTER TABLE tblnotification_repeat ADD CONSTRAINT tblnotification_repeat_FK FOREIGN KEY (subscriberid) REFERENCES tblcustomers(custid);
ALTER TABLE tblnotification_repeat ADD CONSTRAINT tblnotification_repeat_FK_1 FOREIGN KEY (notificationid) REFERENCES tblnotifications(notification_id);
ALTER TABLE tblnotification_repeat ADD CONSTRAINT tblnotification_repeat_FK_2 FOREIGN KEY (packrelid) REFERENCES tblcustpackagerel(custpackageid);

#Utsav 13-09-2021
ALTER TABLE tblcustpackagerel ADD wallet_bal_used DOUBLE DEFAULT 0.0 NOT NULL;

ALTER TABLE tblcustpackagerel ADD purchase_type varchar(100) DEFAULT 'New' NOT NULL;
ALTER TABLE tblcustpackagerel ADD online_purchase_id BIGINT UNSIGNED NULL;
ALTER TABLE tblcustpackagerel ADD purchase_from varchar(100) DEFAULT 'Admin' NOT NULL;

#Mohit 14-09-2021
ALTER table tblcaseupdates add remarktype varchar(100) null;

#Mohit 16-09-2021
ALTER TABLE tblmpostpaidplan MODIFY COLUMN PLANCODE varchar(50)  NOT NULL;

#Utsav 17-09-2021
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('convert_vol_booster_topup', '0');

#Utsav 20-09-2021
ALTER TABLE tbl_order_details ADD purchase_type varchar(100) NULL;


#Mohit 20-09-2021
#Recent Renewal Report
insert into tblaclclass (classid, classname, dispname, disporder, operallid)
values (37, 'com.adopt.apigw.modules.reports.recentrenewal.queryscript', 'Recent Renewal Report', 37, 222);

insert into tblacloperations (opid, classid, opname) values (222, 37, 'Recent Renewal Report View');

#Charge Details Report
insert into tblaclclass (classid, classname, dispname, disporder, operallid)
values (38, 'com.adopt.apigw.modules.reports.recentrenewal.queryscripts', 'Charge Details Report', 38, 223);

insert into tblacloperations (opid, classid, opname) values (223, 38, 'Charge Details Report View');

#Acl Entry For Admin
INSERT INTO tblaclentry (roleid, classid, permit) values (1, 37, 222);

INSERT INTO tblaclentry (roleid, classid, permit) values (1, 38, 223);

#Menu Entry
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (48, 'Recent Renewal Report', 'Recent Renewal Report', 37, 35);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (49, 'Charge Details Report', 'Charge Details Report', 38, 35);

#Mohit 21-09-2021
INSERT INTO tblserverconf (attributename,attributevalue) VALUES ('INACTIVEUSER_PROFILEID','5');

#Utsav 21-09-2021
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('whatsappno1', '9999999999');
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('whatsappno2', '9898989898');
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('custcontactno', '12312312321');
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('bulkcsv_path', 'E:\\\\Users\\\\ossbss\\\\networkdetails');
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('cron_permission_for_refund', 'yes');
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('subscriber_roleId', '8');
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('subscriber_roleName', 'Subscriber');
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('ipblocktime', '30');
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('ticketpath', '/var/ticketdoc/');
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('api_subscribercharge_group', 'INSTALLATION,PLAN,TERMINATION');
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('api_docType', 'docType');
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('api_chargeCategory', 'INSTALLATION,');
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('custdocpath', '/var/custdoc/');
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('request_maxPageSize', '100');
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('request_defaultPage', '1');
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('request_defaultPageSize', '10');
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('request_defaultSortBy', 'id');
INSERT INTO tblclientservice
(NAME, VALUE)
VALUES('request_defaultSortOrder', '0');

#Mohit 22-09-2021
ALTER TABLE tblcustomers ADD salesremark varchar(150) NULL;
ALTER TABLE tblcustomers ADD servicetype varchar(50) NULL;

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES('Leased Line', 'LeasedLine', 'serviceType', 'Active');

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES('SME', 'SME', 'serviceType', 'Active');

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES('Broadband', 'Broadband', 'serviceType', 'Active');

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES('FTTH', 'FTTH', 'serviceType', 'Active');

ALTER TABLE tblmpostpaidplan MODIFY COLUMN PLANCODE varchar(50) NULL; #(Already Fired)

#Utsav 22-09-2021
ALTER TABLE tblcustquotadtls DROP COLUMN created_on;
ALTER TABLE tblcustquotadtls DROP COLUMN lastmodified_on;


#Jaymin 22-09-2021
ALTER TABLE tbloltportdetails
ADD CONSTRAINT sloatId_portName UNIQUE (slotid,portname);

ALTER TABLE tblmpostpaidplan Modify column maxconcurrentsession bigint;

#Jaymin 23-09-2021
update tblcommonlist set status='InActive' where list_type='planStatus' and list_value='Stage';
update tblcommonlist set status='InActive' where list_type='planStatus' and list_value='Live';

update tblcommonlist set status='InActive' where list_type='chargeCategory' and list_value='PLAN';

#Mohit 23-09-2021 (Already Fired)
alter table TBLTDEBITDOCUMENT add custpackrelid bigint;
alter table tblcustpackagerel  add debitdocid bigint references tbltdebitdocument(debitdocumentid);

#Utsav 23-09-2021
delete from tblcommonlist where list_type='planStatus' and list_value='Stage';
delete from tblcommonlist where list_type='planStatus' and list_value='Live';
delete from tblcommonlist where list_type='chargeCategory' and list_value='PLAN';

#Mohit 23-09-2021
ALTER TABLE tblacctprofile
    ADD column createbyname  varchar(100)   null,
    ADD column updatebyname  varchar(100) default null,
    add column CREATEDBYSTAFFID  NUMERIC(20)  default 1 not null,
    add column LASTMODIFIEDBYSTAFFID NUMERIC(20)  default 1 not null,
    add column createdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
    add column lastmodifieddate  TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

update tblacctprofile t1, tblacctprofile t2
set t1.createdate   = t2.created_on
  , t1.lastmodifieddate = t2.lastmodified_on
where t1.acctprofileid = t2.acctprofileid ;

alter table tblacctprofile
    drop column created_on;
alter table tblacctprofile
    drop column lastmodified_on;

UPDATE tblacctprofile nld
    inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblacctprofile nld
    inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

#Mohit 24-09-2021
ALTER TABLE TBLTDEBITDOCUMENT
    ADD column createbyname  varchar(100)   null,
    ADD column updatebyname  varchar(100) default null,
    add column CREATEDBYSTAFFID  NUMERIC(20)  default 1 not null,
    add column LASTMODIFIEDBYSTAFFID NUMERIC(20)  default 1 not null,
    add column lastmodifieddate  TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

UPDATE TBLTDEBITDOCUMENT nld
    inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE TBLTDEBITDOCUMENT nld
    inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

#jaymin 25-09-2021
ALTER TABLE tbloltslots modify status varchar(50);
