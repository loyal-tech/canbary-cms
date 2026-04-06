alter table tblservicearea modify column status varchar(50);

alter table tblmcountry modify status varchar(50);
alter table tblmstate modify status varchar(50);
alter table tblmcity modify status varchar(50);

alter table tblmpostpaidplan modify column MAXALLOWEDCHILD decimal(8,0);
alter table tblmpostpaidplan  modify column PLANSTATUS varchar(32);

-- UPDATE tblclientservice
-- SET NAME='partnerpdfpath', VALUE='//var/prod/comission'
-- WHERE SERVICEID=11;
--
-- UPDATE tblclientservice
-- SET NAME='pdfpath', VALUE='//var/prod/billpdf'
-- WHERE SERVICEID=3;
--
--
-- UPDATE tblclientservice
-- SET NAME='paymentpdfpath', VALUE='//var/prod/payment'
-- WHERE SERVICEID=16;
-- UPDATE tblclientservice
-- SET NAME='trialpdfpath', VALUE='//var/prod/trialbills'
-- WHERE SERVICEID=15;

alter table tblcustomers modify column intercomgrp varchar(75);
alter table tblcustomers modify column intercomno varchar(75);

ALTER TABLE tbltdebitdocument 
RENAME COLUMN plan_id TO planid;

alter table tblserverdetail add servertype varchar(50);

ALTER TABLE tblcases DROP FOREIGN KEY tblcases_ibfk_2;

alter table tblcustchargedtls modify  planid  bigint unsigned null;

ALTER TABLE tblipallocationdtls
    ADD column createbyname  varchar(100)   null,
    ADD column updatebyname  varchar(100) default null;
    
ALTER TABLE tblteams ADD partnerid bigint REFERENCES tblpartners (PARTNERID);
   
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

alter table tblcustomers add column latitude varchar(50);
alter table tblcustomers add column longitude varchar(50);
alter table tblcustomers add column url varchar(100);
ALTER TABLE tblippool add column default_pool_flag  Boolean NOT NULL DEFAULT false;
alter table tblcustomers drop column deposit; 
alter table tblcustomers add column gisCode varchar(50);

insert into tblclientservice (name, value) values("nexg_server", "http://voice1.expl.in:8080");
insert into tblclientservice (name, value) values("nexg_api_key", "zypHHgyh.ucq9WE69E9fTGhd6vlX3USDD8hzlSamPEm7");
insert into tblclientservice (name, value) values("nexg_auth_userid", "admin");
insert into tblclientservice (name, value) values("nexg_auth_password", "sgrfhjSDFSDVG");
insert into tblclientservice (name, value) values("nexg_req_parent", "expl");
ALTER TABLE tblcustomers add column voiceProvision boolean default false;

create table tblnotification_repeat (
    id SERIAL primary key,
    subscirberid bigint unsigned,
    packrelid bigint unsigned,
    notificationid bigint unsigned
);


alter table tblcustomers CHANGE column gisCode  gis_code varchar(50);
alter table tblmpostpaidplan add datacategory varchar(50);

Alter table tblbroadcast
    ADD column createbyname varchar(100) default null,
    ADD column updatebyname varchar(100) default null;

UPDATE tblbroadcast nld
    inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname,' ',ng.lastname)
where nld.createbyname is null;

UPDATE tblbroadcast nld
    inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname,' ',ng.lastname)
where nld.updatebyname is null;

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Add', 'Add', 'adjustmentType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Absolute', 'Absolute', 'adjustmentType', 'Active');

ALTER TABLE tbltdebitdocument DROP FOREIGN KEY tbltdebitdocument_ibfk_3;

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('FUP', 'FUP', 'dataCategory', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Normal', 'Normal', 'dataCategory', 'Active');
