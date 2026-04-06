#Mohit 09-08-2021
#Alter Only Name
Alter table tblmstate
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblmcountry
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblmcity
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblmservices
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblpartners
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblcharges
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblmtax
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblmpostpaidplan
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblmdiscount
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tbl_qos_policy
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblcustquotadtls
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblcaseresolutions
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblservicearea
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblteams
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblnetworkdevices
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tbloltportdetails
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tbloltslots
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblcustpackagerel
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tbltcreditdoc
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblpricebook
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblcustchargedtls
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblcustdocdetails
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblcasereasons
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblcases
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblcaseupdates
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblippool
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

Alter table tblippooldtls
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

#Alter All Auditable Fields
ALTER TABLE tblclients
ADD column createbyname  varchar(100)   null,
ADD column updatebyname  varchar(100) default null,
add column CREATEDBYSTAFFID  NUMERIC(20)  default 1 not null,
add column LASTMODIFIEDBYSTAFFID NUMERIC(20)  default 1 not null,
add column createdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
add column lastmodifieddate  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
add column is_delete boolean not null default false;

ALTER TABLE tblradiusprofile
ADD column createbyname  varchar(100)   null,
ADD column updatebyname  varchar(100) default null,
add column CREATEDBYSTAFFID  NUMERIC(20)  default 1 not null,
add column LASTMODIFIEDBYSTAFFID NUMERIC(20)  default 1 not null,
add column createdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
add column lastmodifieddate  TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE tblradiusprofilecheckitm
ADD column createbyname  varchar(100)   null,
ADD column updatebyname  varchar(100) default null,
add column CREATEDBYSTAFFID  NUMERIC(20)  default 1 not null,
add column LASTMODIFIEDBYSTAFFID NUMERIC(20)  default 1 not null,
add column createdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
add column lastmodifieddate  TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE tblradiusprofilereplyitm
ADD column createbyname  varchar(100)   null,
ADD column updatebyname  varchar(100) default null,
add column CREATEDBYSTAFFID  NUMERIC(20)  default 1 not null,
add column LASTMODIFIEDBYSTAFFID NUMERIC(20)  default 1 not null,
add column createdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
add column lastmodifieddate  TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE tblcustmacmapping
ADD column createbyname  varchar(100)   null,
ADD column updatebyname  varchar(100) default null,
add column CREATEDBYSTAFFID  NUMERIC(20)  default 1 not null,
add column LASTMODIFIEDBYSTAFFID NUMERIC(20)  default 1 not null,
add column createdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
add column lastmodifieddate  TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE tblmsubscriberaddressrel
ADD column createbyname  varchar(100)   null,
ADD column updatebyname  varchar(100) default null,
add column CREATEDBYSTAFFID  NUMERIC(20)  default 1 not null,
add column LASTMODIFIEDBYSTAFFID NUMERIC(20)  default 1 not null,
add column createdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
add column lastmodifieddate  TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE tblclientgroup
ADD column createbyname  varchar(100)   null,
ADD column updatebyname  varchar(100) default null,
add column CREATEDBYSTAFFID  NUMERIC(20)  default 1 not null,
add column LASTMODIFIEDBYSTAFFID NUMERIC(20)  default 1 not null,
add column createdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
add column lastmodifieddate  TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE tblcustomers
ADD column createbyname varchar(100) null,
ADD column updatebyname varchar(100) null,
add column lastmodifieddate TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE TBLMCUSTLEDGER
ADD column createbyname  varchar(100)  null,
ADD column updatebyname  varchar(100)  null,
add column CREATEDBYSTAFFID  NUMERIC(20) default 1 not null,
add column LASTMODIFIEDBYSTAFFID NUMERIC(20) default 1 not null;

#Update Name Query For CreateBy
update tblmcountry nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblmstate nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblmcountry nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblmcity nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblmservices nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblpartners nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblcharges nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblmtax nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblmpostpaidplan nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblmdiscount nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tbl_qos_policy nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblcustquotadtls nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblcaseresolutions nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblservicearea nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblteams nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblnetworkdevices nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tbloltportdetails nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tbloltslots nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblcustpackagerel nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tbltcreditdoc nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblpricebook nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblcustchargedtls nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblcustdocdetails nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblcasereasons nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblcases nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblcaseupdates nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblippool nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblippooldtls nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

#Update Query For LastUpdatedBy
UPDATE tblmstate nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblmcountry nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblmcity nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblmservices nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblpartners nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblcharges nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblmtax nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblmpostpaidplan nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblmdiscount nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tbl_qos_policy nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblcustquotadtls nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblcaseresolutions nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblservicearea nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblteams nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblnetworkdevices nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tbloltportdetails nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tbloltslots nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblcustpackagerel nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tbltcreditdoc nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblpricebook nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblcustchargedtls nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblcustdocdetails nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblcasereasons nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblcases nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblcaseupdates nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblippool nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblippooldtls nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

#Update All Auditable Fields
update tblclients t1, tblclients t2
set t1.createdate   = t2.created_on
  , t1.lastmodifieddate = t2.lastmodified_on
where t1.clientid = t2.clientid;

UPDATE tblclients nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblclients nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

update tblclientgroup t1, tblclientgroup t2
set t1.createdate   = t2.created_on
  , t1.lastmodifieddate = t2.lastmodified_on
where t1.clientgroupid = t2.clientgroupid;

UPDATE tblclientgroup nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblclientgroup nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

update tblradiusprofile t1, tblradiusprofile t2
set t1.createdate   = t2.created_on
  , t1.lastmodifieddate = t2.lastmodified_on
where t1.radiusprofileid = t2.radiusprofileid;

UPDATE tblradiusprofile nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblradiusprofile nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

update tblradiusprofilecheckitm t1, tblradiusprofilecheckitm t2
set t1.createdate   = t2.created_on
  , t1.lastmodifieddate = t2.lastmodified_on
where t1.radiuscheckitmid = t2.radiuscheckitmid;

UPDATE tblradiusprofilecheckitm nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblradiusprofilecheckitm nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

update tblradiusprofilereplyitm t1, tblradiusprofilereplyitm t2
set t1.createdate   = t2.created_on
  , t1.lastmodifieddate = t2.lastmodified_on
where t1.radiusreplyitmid = t2.radiusreplyitmid;

UPDATE tblradiusprofilereplyitm nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblradiusprofilereplyitm nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

update tblcustmacmapping t1, tblcustmacmapping t2
set t1.createdate   = t2.created_on
  , t1.lastmodifieddate = t2.lastmodified_on
where t1.custmacmapid = t2.custmacmapid;

UPDATE tblcustmacmapping nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblcustmacmapping nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

UPDATE tblmsubscriberaddressrel nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblmsubscriberaddressrel nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

update tblcustomers t1, tblcustomers t2
set t1.lastmodifieddate = t2.lastmodified_on
where t1.custid = t2.custid;

UPDATE tblcustomers nld
inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname, ' ', ng.lastname)
where nld.createbyname is null;

UPDATE tblcustomers nld
inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname, ' ', ng.lastname)
where nld.updatebyname is null;

#Drop old Columns
alter table tblcustomers
drop column created_on;
alter table tblcustomers
drop column lastmodified_on;

alter table tblradiusprofilereplyitm
drop column created_on;
alter table tblradiusprofilereplyitm
drop column lastmodified_on;

alter table tblradiusprofilecheckitm
drop column created_on;
alter table tblradiusprofilecheckitm
drop column lastmodified_on;

alter table tblradiusprofile
drop column created_on;
alter table tblradiusprofile
drop column lastmodified_on;

alter table tblclients
drop column created_on;
alter table tblclients
drop column lastmodified_on;

alter table tblclientgroup
drop column created_on;
alter table tblclientgroup
drop column lastmodified_on;

alter table tblcustmacmapping
drop column created_on;
alter table tblcustmacmapping
drop column lastmodified_on;

#Mohit 10-08-2021 (Already Fired)
ALTER TABLE tbl_payment_gateway ADD column createbyname varchar(100) default null
    , ADD column updatebyname varchar(100) default null;

ALTER TABLE tbl_payment_gateway_response ADD column createbyname varchar(100) default null
    , ADD column updatebyname varchar(100) default null;

ALTER TABLE tbl_purchase_details ADD column createbyname varchar(100) default null
    , ADD column updatebyname varchar(100) default null;

ALTER TABLE tbl_order_details ADD column createbyname varchar(100) default null
    , ADD column updatebyname varchar(100) default null;

UPDATE tbl_payment_gateway nld
    inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname,' ',ng.lastname)
where nld.createbyname is null;

UPDATE tbl_payment_gateway_response nld
    inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname,' ',ng.lastname)
where nld.createbyname is null;

UPDATE tbl_purchase_details nld
    inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname,' ',ng.lastname)
where nld.createbyname is null;

UPDATE tbl_order_details nld
    inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname,' ',ng.lastname)
where nld.createbyname is null;

UPDATE tbl_payment_gateway nld
    inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname,' ',ng.lastname)
where nld.updatebyname is null;

UPDATE tbl_payment_gateway_response nld
    inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname,' ',ng.lastname)
where nld.updatebyname is null;

UPDATE tbl_purchase_details nld
    inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname,' ',ng.lastname)
where nld.updatebyname is null;

UPDATE tbl_order_details nld
    inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname,' ',ng.lastname)
where nld.updatebyname is null;

ALTER TABLE tblsubscriberupdates
    ADD column createbyname  varchar(100)   null,
    ADD column updatebyname  varchar(100) default null,
    add column CREATEDBYSTAFFID  NUMERIC(20)  default 1 not null,
    add column LASTMODIFIEDBYSTAFFID NUMERIC(20)  default 1 not null,
    add column createdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
    add column lastmodifieddate  TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

#Mohit 16-08-2021
ALTER TABLE tblstaffuser
    ADD column createbyname  varchar(100)   null,
    ADD column updatebyname  varchar(100) default null,
    add column CREATEDBYSTAFFID  NUMERIC(20)  default 1 not null,
    add column LASTMODIFIEDBYSTAFFID NUMERIC(20)  default 1 not null,
    add column createdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
    add column lastmodifieddate  TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE tblroles
    ADD column createbyname  varchar(100)   null,
    ADD column updatebyname  varchar(100) default null,
    add column CREATEDBYSTAFFID  NUMERIC(20)  default 1 not null,
    add column LASTMODIFIEDBYSTAFFID NUMERIC(20)  default 1 not null,
    add column createdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
    add column lastmodifieddate  TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

#Update
UPDATE tblstaffuser nld
    inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname,' ',ng.lastname)
where nld.createbyname is null;

UPDATE tblstaffuser nld
    inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname,' ',ng.lastname)
where nld.updatebyname is null;

UPDATE tblroles nld
    inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname,' ',ng.lastname)
where nld.createbyname is null;

UPDATE tblroles nld
    inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname,' ',ng.lastname)
where nld.updatebyname is null;


#Drop
alter table tblstaffuser
    drop column created_on;
alter table tblstaffuser
    drop column lastmodified_on;

alter table tblroles
    drop column created_on;
alter table tblroles
    drop column lastmodified_on;

#Mohit 18-08-2021 (Already Fired)
Alter table tblnotifications
ADD column createbyname varchar(100) default null,
ADD column updatebyname varchar(100) default null;

UPDATE tblnotifications nld
    inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname,' ',ng.lastname)
where nld.createbyname is null;

UPDATE tblnotifications nld
    inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname,' ',ng.lastname)
where nld.updatebyname is null;


#Mohit 19-08-2021
ALTER TABLE tbltemplatemanagement
ADD column createbyname  varchar(100)   null,
ADD column updatebyname  varchar(100) default null,
add column CREATEDBYSTAFFID  NUMERIC(20)  default 1 not null,
add column LASTMODIFIEDBYSTAFFID NUMERIC(20)  default 1 not null,
add column createdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT null,
add column lastmodifieddate  TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

UPDATE tbltemplatemanagement nld
    inner join tblstaffuser ng on nld.CREATEDBYSTAFFID = ng.staffid
set nld.createbyname = concat(ng.firstname,' ',ng.lastname)
where nld.createbyname is null;

UPDATE tbltemplatemanagement nld
    inner join tblstaffuser ng on nld.LASTMODIFIEDBYSTAFFID = ng.staffid
set nld.updatebyname = concat(ng.firstname,' ',ng.lastname)
where nld.updatebyname is null;

alter table tbltemplatemanagement
    drop column created_on;
alter table tbltemplatemanagement
    drop column lastmodified_on;

#Mohit 24-08-2021 (Already Fired)
ALTER TABLE tblipallocationdtls
    ADD column createbyname  varchar(100)   null,
    ADD column updatebyname  varchar(100) default null;

#Mohit 01-09-2021
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



