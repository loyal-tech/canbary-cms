#Mohit //29-04-2021
CREATE TABLE tbl_qos_policy
(
    id                    SERIAL PRIMARY KEY,
    name                  varchar(100) NOT NULL,
    description           varchar(100) NOT NULL,
    baseuploadspeed       varchar(100) NOT NULL,
    basedownloadspeed     varchar(100) NOT NULL,
    thuploadspeed         varchar(100),
    thdownloadspeed       varchar(100),
    thpolicyname          varchar(100),
    baseparam1            varchar(100),
    baseparam2            varchar(100),
    baseparam3            varchar(100),
    thparam1              varchar(100),
    thparam2              varchar(100),
    thparam3              varchar(100),
    is_deleted            Boolean      NOT NULL DEFAULT FALSE,
    createdbystaffid      NUMERIC(20),
    createdate            TIMESTAMP             DEFAULT CURRENT_TIMESTAMP NOT NULL,
    lastmodifiedbystaffid NUMERIC(20),
    lastmodifieddate      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE tblmpostpaidplan
    ADD saccode VARCHAR(100) NOT NULL;

ALTER TABLE tblmpostpaidplan
    ADD quotatype VARCHAR(100) NOT NULL;

ALTER TABLE tblmpostpaidplan
    ADD quotaunittime VARCHAR(100);

ALTER TABLE tblmpostpaidplan
    ADD quotatime VARCHAR(100);

ALTER TABLE tblmpostpaidplan
    ADD maxconcurrentsession NUMERIC(14, 4) NOT NULL;

ALTER TABLE tblmpostpaidplan
    ADD qospolicy_id BIGINT UNSIGNED REFERENCES tbl_qos_policy (id);

ALTER TABLE tblmpostpaidplan
    ADD radiusprofile_id BIGINT UNSIGNED REFERENCES tblradiusprofile (radiusprofileid);

ALTER TABLE tblmpostpaidplan
    ADD offerprice NUMERIC(14, 4) NOT NULL;

ALTER TABLE tblmpostpaidplan
    ADD is_deleted BOOLEAN NOT NULL default FALSE;

/*ALTER TABLE tblradiusprofilecheckitm ADD FOREIGN KEY (radiusprofileid) BIGINT UNSIGNED NOT NULL REFERENCES tblradiusprofile(radiusprofileid);

ALTER TABLE tblradiusprofilereplyitm ADD FOREIGN KEY (radiuscheckitmid) BIGINT UNSIGNED NOT NULL REFERENCES tblradiusprofilecheckitm(radiuscheckitmid);
*/
#05/04/2021
CREATE TABLE tblservicearea
(
    service_area_id       bigint     NOT NULL AUTO_INCREMENT,
    name                  varchar(100)        DEFAULT NULL,
    status                char(1)             DEFAULT NULL,
    is_deleted            tinyint(1) NOT NULL DEFAULT '0',
    CREATEDATE            timestamp  NULL     DEFAULT NULL,
    LASTMODIFIEDDATE      timestamp  NULL     DEFAULT NULL,
    CREATEDBYSTAFFID      decimal(20, 0)      DEFAULT NULL,
    LASTMODIFIEDBYSTAFFID decimal(20, 0)      DEFAULT NULL,
    PRIMARY KEY (service_area_id)
);

INSERT INTO tblservicearea
(name, status, is_deleted, CREATEDATE, LASTMODIFIEDDATE, CREATEDBYSTAFFID, LASTMODIFIEDBYSTAFFID)
VALUES ('It', 'Y', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, null, null);

INSERT INTO tblservicearea
(name, status, is_deleted, CREATEDATE, LASTMODIFIEDDATE, CREATEDBYSTAFFID, LASTMODIFIEDBYSTAFFID)
VALUES ('HR', 'Y', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, null, null);


#--Jaymin:2021/05/03
CREATE TABLE tblnetworkdevices
(
    deviceid              SERIAL PRIMARY KEY,
    name                  varchar(100) NOT NULL,
    servicearea_id        BIGINT UNSIGNED REFERENCES tblservicearea (service_area_id),
    devicetype            char(3)      NOT NULL,
    status                char(1)      NOT NULL,
    CREATEDATE            timestamp             DEFAULT CURRENT_TIMESTAMP NOT NULL,
    LASTMODIFIEDDATE      timestamp    NULL     DEFAULT NULL,
    CREATEDBYSTAFFID      decimal(20, 0),
    LASTMODIFIEDBYSTAFFID decimal(20, 0),
    is_deleted            tinyint(1)   NOT NULL DEFAULT '0'
);

#--devicetype{OLT,ONU}
INSERT INTO tblnetworkdevices
(name, servicearea_id, devicetype, status, CREATEDATE, LASTMODIFIEDDATE, CREATEDBYSTAFFID, LASTMODIFIEDBYSTAFFID,
 is_deleted)
VALUES ('ABC', 1, 'OLT', 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0, 0);

INSERT INTO tblnetworkdevices
(name, servicearea_id, devicetype, status, CREATEDATE, LASTMODIFIEDDATE, CREATEDBYSTAFFID, LASTMODIFIEDBYSTAFFID,
 is_deleted)
VALUES ('XYZ', 1, 'ONU', 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0, 0);


CREATE TABLE tbloltslots
(
    slotid                SERIAL PRIMARY KEY,
    slotname              varchar(100) NOT NULL,
    deviceid              bigint REFERENCES tblnetworkdevices (deviceid),
    status                char(1)      NOT NULL,
    is_deleted            tinyint(1)        DEFAULT NULL,
    CREATEDATE            timestamp         DEFAULT CURRENT_TIMESTAMP NOT NULL,
    LASTMODIFIEDDATE      timestamp    NULL DEFAULT NULL,
    CREATEDBYSTAFFID      decimal(20, 0),
    LASTMODIFIEDBYSTAFFID decimal(20, 0)
);


INSERT INTO tbloltslots
(slotname, deviceid, status, is_deleted, CREATEDATE, LASTMODIFIEDDATE, CREATEDBYSTAFFID, LASTMODIFIEDBYSTAFFID)
VALUES ('P', 2, 'Y', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0);


CREATE TABLE tbloltportdetails
(
    portid                SERIAL PRIMARY KEY,
    portname              varchar(100) NOT NULL,
    slotid                bigint            DEFAULT NULL REFERENCES tbloltslots (slotid),
    deviceid              bigint            DEFAULT NULL REFERENCES tblnetworkdevices (deviceid),
    status                char(1)      NOT NULL,
    CREATEDATE            timestamp         DEFAULT CURRENT_TIMESTAMP NOT NULL,
    LASTMODIFIEDDATE      timestamp    NULL DEFAULT NULL,
    CREATEDBYSTAFFID      decimal(20, 0),
    LASTMODIFIEDBYSTAFFID decimal(20, 0),
    is_deleted            tinyint(1)
);


INSERT INTO tbloltportdetails
(portname, slotid, deviceid, status, CREATEDATE, LASTMODIFIEDDATE, CREATEDBYSTAFFID, LASTMODIFIEDBYSTAFFID, is_deleted)
VALUES ('FTP', 2, 1, 'Y', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, null, null, 0);

#--jaymin 2021/05/04 [CommonList]
/*create table tblcommonlist
(
    list_item_id SERIAL PRIMARY KEY,
    list_text    VARCHAR(100) NOT NULL,
    list_value   VARCHAR(100) NOT NULL,
    list_type    VARCHAR(100) NOT NULL,
    status       VARCHAR(1)   NOT NULL
);*/


INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Cash', 'cash', 'paymentMode', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Online', 'online', 'paymentMode', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Cheque', 'cheque', 'paymentMode', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Approved', 'approved', 'paymentStatus', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Pending', 'pending', 'paymentStatus', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Rejected', 'rejected', 'paymentStatus', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Prepaid', 'prepaid', 'planType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Postpaid', 'postpaid', 'planType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Demo', 'demo', 'planCategory', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Commercial', 'commercial', 'planCategory', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Retention', 'retention', 'planCategory', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Employee', 'employee', 'planCategory', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('GB', 'GB', 'quotaTypeData', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('MB', 'MB', 'quotaTypeData', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Minute', 'Minute', 'quotaTypeTime', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Hour', 'Hour', 'quotaTypeTime', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Renew', 'Renew', 'planGroup', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('BOD', 'BOD', 'planGroup', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Volume Booster', 'Volume Booster', 'planGroup', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Active', 'Active', 'planStatus', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('InActive', 'InActive', 'planStatus', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Stage', 'Stage', 'planStatus', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Live', 'Live', 'planStatus', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Data', 'Data', 'quotaType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Time', 'Time', 'quotaType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Both', 'Both', 'quotaType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Home', 'home', 'addressType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Office', 'office', 'addressType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Other', 'other', 'addressType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Exported', 'Exported', 'billRunStatus', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Generated', 'Generated', 'billRunStatus', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Gold', 'gold', 'dunningCreditClass', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Silver', 'silver', 'dunningCreditClass', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Bronze', 'bronze', 'dunningCreditClass', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Block', 'block', 'dunningAction', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Notify', 'notify', 'dunningAction', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Flat per Customer', 'PERCUSTFLAT', 'partnerCommType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Percentage on Invoice', 'Postpaid', 'partnerCommType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Percentage', 'percentage', 'discType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Value', 'value', 'discType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Recurring', 'RECURRING', 'chargeType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Non Recurring', 'NON_RECURRING', 'chargeType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Advance', 'ADVANCE', 'chargeType', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Refundable', 'REFUNDABLE', 'chargeType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Customer Direct', 'CUSTOMER_DIRECT', 'chargeType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Slab', 'SLAB', 'taxType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Tiered', 'TIER', 'taxType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Tier-1', 'TIER1', 'taxGroup', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Tier-2', 'TIER2', 'taxGroup', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Tier-3', 'TIER3', 'taxGroup', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('DB', '1', 'authDriverType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('LDAP', '2', 'authDriverType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('None', 'None', 'ldapAuthType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('simpe', 'simpe', 'ldapAuthType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('DIGEST-MD5', 'DIGEST-MD5', 'ldapAuthType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('GSSAPI', 'GSSAPI', 'ldapAuthType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('1', '1', 'billingCycle', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('2', '2', 'billingCycle', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('3', '3', 'billingCycle', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('4', '4', 'billingCycle', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('5', '5', 'billingCycle', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('6', '6', 'billingCycle', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('7', '7', 'billingCycle', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('8', '8', 'billingCycle', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('9', '9', 'billingCycle', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('10', '10', 'billingCycle', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('11', '11', 'billingCycle', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('12', '12', 'billingCycle', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('OLT', 'OLT', 'networkDeviceType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('ONU', 'ONU', 'networkDeviceType', 'Active');

#  Nilesh | 03-05-2021

create table tblcustdocdetails
(
    doc_id                SERIAL PRIMARY KEY,
    doc_type              VARCHAR(100) NOT NULL,
    doc_status            VARCHAR(100) NOT NULL,
    attachment_path       VARCHAR(100) NOT NULL,
    is_delete             Boolean      NOT NULL,
    CREATEDATE            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    LASTMODIFIEDDATE      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATEDBYSTAFFID      BIGINT(100)  NOT NULL,
    LASTMODIFIEDBYSTAFFID VARCHAR(100) NOT NULL

);

#Mohit-05-05-2021
create table tblpostpaidplanradiusprofilerel
(
    POSTPAIDPLANID  BIGINT UNSIGNED NOT NULL,
    radiusprofileid BIGINT UNSIGNED NOT NULL
);

ALTER TABLE tblpostpaidplanradiusprofilerel
    ADD FOREIGN KEY (radiusprofileid) REFERENCES tblradiusprofile (radiusprofileid);

ALTER TABLE tblpostpaidplanradiusprofilerel
    ADD FOREIGN KEY (POSTPAIDPLANID) REFERENCES tblmpostpaidplan (POSTPAIDPLANID);

#Jaymin-05-12-2021
create table tblpricebook
(
    bookid                SERIAL PRIMARY KEY,
    bookname              varchar(100) not null,
    validfrom             timestamp    NULL DEFAULT NULL,
    validto               timestamp    NULL DEFAULT NULL,
    status                char(15)     NOT NULL,
    description           varchar(200) not null,
    is_deleted            tinyint(1)        DEFAULT NULL,
    CREATEDATE            timestamp         DEFAULT CURRENT_TIMESTAMP NOT NULL,
    LASTMODIFIEDDATE      timestamp    NULL DEFAULT NULL,
    CREATEDBYSTAFFID      decimal(20, 0),
    LASTMODIFIEDBYSTAFFID decimal(20, 0)
);

INSERT INTO tblpricebook
(bookname, validfrom, validto, status, description, is_deleted, CREATEDATE, LASTMODIFIEDDATE, CREATEDBYSTAFFID,
 LASTMODIFIEDBYSTAFFID)
VALUES ('P2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Active', 'P2 Plan For Device', 0, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 0, 0);

INSERT INTO tblpricebook
(bookname, validfrom, validto, status, description, is_deleted, CREATEDATE, LASTMODIFIEDDATE, CREATEDBYSTAFFID,
 LASTMODIFIEDBYSTAFFID)
VALUES ('P1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Active', 'P1 Plan For Device', 0, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 0, 0);

#Jaymin-05-14-2021
create table tblpricebookplandtls
(
    pbdetailid         SERIAL PRIMARY KEY,
    planid             bigint REFERENCES tblmpostpaidplan (POSTPAIDPLANID),
    bookid             bigint REFERENCES tblpricebook (bookid),
    offerprice         bigint                NOT NULL,
    partnerofficeprice bigint                NOT NULL,
    revsharen          char(3) DEFAULT 'Yes' NOT NULL,
    registration       char(3) DEFAULT 'Yes' NOT null,
    renewal            char(3) DEFAULT 'Yes' NOT NULL
);

INSERT INTO tblpricebookplandtls
(planid, bookid, offerprice, partnerofficeprice, revsharen, registration, renewal)
VALUES (6, 1, 1200, 1000, 'Yes', 'Yes', 'Yes');

CREATE TABLE tblteams
(
    team_id               SERIAL PRIMARY KEY,
    team_name             varchar(100),
    team_status           char(15),
    is_deleted            tinyint(1),
    CREATEDATE            timestamp NULL DEFAULT NULL,
    LASTMODIFIEDDATE      timestamp NULL DEFAULT NULL,
    CREATEDBYSTAFFID      decimal(20, 0) DEFAULT NULL,
    LASTMODIFIEDBYSTAFFID decimal(20, 0) DEFAULT NULL
);

INSERT INTO tblteams (team_name, team_status, is_deleted, CREATEDATE, LASTMODIFIEDDATE, CREATEDBYSTAFFID,
                      LASTMODIFIEDBYSTAFFID)
VALUES ('MNL', 'Active', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 0);


create table tblteamusermapping
(
    mapping_id SERIAL PRIMARY KEY,
    team_id    BIGINT UNSIGNED not NULL,
    staffid    BIGINT UNSIGNED not NULL
);

ALTER TABLE tblteamusermapping
    ADD FOREIGN KEY (team_id) REFERENCES tblteams (team_id);
ALTER TABLE tblteamusermapping
    ADD FOREIGN KEY (staffid) REFERENCES tblstaffuser (staffid);

#jaymin-05-22-2021

ALTER TABLE tblcustomers
    ADD selfcarepwd VARCHAR(100);

#jaymin 26-05-2021

ALTER TABLE tblpricebook
    MODIFY COLUMN is_deleted tinyint(1) DEFAULT FALSE;

ALTER TABLE tblpricebook
    MODIFY COLUMN CREATEDBYSTAFFID BIGINT(10);

ALTER TABLE tblpricebook
    MODIFY COLUMN LASTMODIFIEDBYSTAFFID BIGINT(10);

#Utsav-05-29-2021

ALTER TABLE tbl_qos_policy
    ADD basepolicyname varchar(100) NULL;


#Mohit 26-05-2021 (Modify Some Fields Of tblcustomers)
ALTER TABLE tblcustomers
    MODIFY COLUMN ASNNumber TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN BNGRouterInterface TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN BNGRouterName TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN IPPrefixes TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN IPV6Prefixes TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN LANIP TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN LANIPV6 TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN LLAccountID TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN LLConnectionType TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN LLExpiryDate TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN LLMedium TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN LLServiceID TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN MACADDRESS TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN PeerIP TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN POOLIP TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN QOS TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN RDExport TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN RDValue TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN VLANID TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN VRFName TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN VSIID TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN VSIName TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN WANIP TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN WANIPV6 TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN billentityname TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN purchaseorder TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN remarks TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN addparam1 TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN addparam2 TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN addparam3 TEXT;
ALTER TABLE tblcustomers
    MODIFY COLUMN addparam4 TEXT;

#Mohit 26-05-2021 (Add Some Fields in tblcustomers)
ALTER TABLE tblcustomers
    ADD title varchar(40) not null;
ALTER TABLE tblcustomers
    ADD custname varchar(40) not null;
ALTER TABLE tblcustomers
    ADD contactperson varchar(40) not null;
ALTER TABLE tblcustomers
    ADD cafno varchar(40) not null;
ALTER TABLE tblcustomers
    ADD pan varchar(25);
ALTER TABLE tblcustomers
    ADD gst varchar(25);
ALTER TABLE tblcustomers
    ADD aadhar varchar(25);
ALTER TABLE tblcustomers
    ADD mactelflag boolean default false;
ALTER TABLE tblcustomers
    ADD mobile varchar(10) not null;
ALTER TABLE tblcustomers
    ADD altmobile varchar(10);
ALTER TABLE tblcustomers
    ADD altphone varchar(50);
ALTER TABLE tblcustomers
    ADD altemail varchar(100);
ALTER TABLE tblcustomers
    ADD fax varchar(100);
ALTER TABLE tblcustomers
    ADD resellerid bigint;
ALTER TABLE tblcustomers
    ADD salesrepid bigint;
ALTER TABLE tblcustomers
    ADD deposit DECIMAL(20, 4);
ALTER TABLE tblcustomers
    ADD voicesrvtype varchar(75) not null;
ALTER TABLE tblcustomers
    ADD didno varchar(75) not null;
ALTER TABLE tblcustomers
    ADD childdidno varchar(75) not null;
ALTER TABLE tblcustomers
    ADD intercomno varchar(75) not null;
ALTER TABLE tblcustomers
    ADD intercomgrp varchar(75) not null;
ALTER TABLE tblcustomers
    ADD onlinerenewalflag boolean default false;
ALTER TABLE tblcustomers
    ADD voipenableflag boolean default false;
ALTER TABLE tblcustomers
    ADD custcategory varchar(75);
ALTER TABLE tblcustomers
    ADD walletbalance DECIMAL(20, 4);
ALTER TABLE tblcustomers
    ADD networktype varchar(50);
ALTER TABLE tblcustomers
    ADD defaultpool varchar(100);
ALTER TABLE tblcustomers
    ADD oltslotid bigint;
ALTER TABLE tblcustomers
    ADD oltportid bigint;
ALTER TABLE tblcustomers
    ADD strconntype varchar(75);
ALTER TABLE tblcustomers
    ADD stroltname varchar(75);
ALTER TABLE tblcustomers
    ADD strslotname varchar(75);
ALTER TABLE tblcustomers
    ADD strportname varchar(75);
ALTER TABLE tblcustomers
    ADD createdbystaffid integer;
ALTER TABLE tblcustomers
    ADD lastmodifiedbystaffid integer;
ALTER TABLE tblcustomers
    ADD servicearea_id bigint REFERENCES tblservicearea (service_area_id);
ALTER TABLE tblcustomers
    ADD network_device_id bigint unsigned REFERENCES tblnetworkdevices (deviceid);
ALTER TABLE tblcustpackagerel
    ADD service varchar(100);

#Mohit - 27-05-2021
create table tblcustchargedtls
(
    cstchargeid           serial primary key,
    custid                BIGINT UNSIGNED                     NOT NULL,
    planid                BIGINT UNSIGNED                     NOT NULL,
    chargeid              BIGINT UNSIGNED                     NOT NULL,
    chargetype            varchar(2),
    price                 numeric(20, 4),
    actual_price          numeric(20, 4),
    validity              numeric(20, 4),
    CREATEDATE            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    foreign key (planid) references TBLMPOSTPAIDPLAN (POSTPAIDPLANID),
    foreign key (custid) references tblcustomers (custid),
    foreign key (chargeid) references TBLCHARGES (CHARGEID)
);

#Mohit - 29-05-2021
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('CreditCard', 'creditcard', 'paymentMode', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('DebitCard', 'debitcard', 'paymentMode', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('NEFT/RTGS', 'neft/rtgs', 'paymentMode', 'Active');

ALTER TABLE tbltcreditdoc
    ADD chequedate timestamp NULL DEFAULT NULL;

#Mohit - 31-05-2021
#ALTER TABLE tblcustquotadtls ADD quotaunit VARCHAR(100);

ALTER TABLE tblcustchargedtls
    MODIFY COLUMN chargetype varchar(100);

-- Nilesh 06/05/2021

create table tblippool
(
    pool_id               SERIAL PRIMARY KEY,
    pool_name             VARCHAR(100) NOT NULL,
    pool_type             VARCHAR(100) NOT NULL,
    pool_category         VARCHAR(100) NOT NULL,
    ip_range              VARCHAR(100) NOT NULL,
    net_mask              VARCHAR(100) NOT NULL,
    network_ip            VARCHAR(100) NOT NULL,
    broadcast_ip          VARCHAR(100) NOT NULL,
    first_host            VARCHAR(100) NOT NULL,
    last_host             VARCHAR(100) NOT NULL,
    total_host            BIGINT       NOT NULL,
    is_delete             BOOLEAN      NOT NULL,
    CREATEDATE            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    LASTMODIFIEDDATE      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATEDBYSTAFFID      BIGINT(100)  NOT NULL,
    LASTMODIFIEDBYSTAFFID VARCHAR(100) NOT NULL
);

create table tblippooldtls
(
    pool_details_id       SERIAL PRIMARY KEY,
    pool_id               bigint UNSIGNED,
    ip_address            VARCHAR(100) NOT NULL,
    status                VARCHAR(100) NOT NULL,
    is_delete             BOOLEAN      NOT NULL,
    CREATEDATE            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    LASTMODIFIEDDATE      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATEDBYSTAFFID      BIGINT(100)  NOT NULL,
    LASTMODIFIEDBYSTAFFID VARCHAR(100) NOT NULL,
    FOREIGN KEY (pool_id) REFERENCES tblippool (pool_id)
);

# Nilesh | 08-05-2021

create table tblcasereasons
(
    reason_id             SERIAL PRIMARY KEY,
    name                  VARCHAR(100) NOT NULL,
    status                VARCHAR(100) NOT NULL,
    tat_consideration     VARCHAR(100) NOT NULL,
    is_delete             BOOLEAN      NOT NULL,
    CREATEDATE            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    LASTMODIFIEDDATE      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATEDBYSTAFFID      BIGINT(10)   NOT NULL,
    LASTMODIFIEDBYSTAFFID BIGINT(10)   NOT NULL
);

create table tblcases
(
    case_id               SERIAL PRIMARY KEY,
    reason_id             BIGINT UNSIGNED,
    case_title            VARCHAR(100) NOT NULL,
    case_type             VARCHAR(100) NOT NULL,
    case_number           VARCHAR(100) NOT NULL,
    case_for              VARCHAR(100) NOT NULL,
    case_origin           VARCHAR(100) NOT NULL,
    case_status           VARCHAR(100) NOT NULL,
    priority              VARCHAR(100) NOT NULL,
    case_for_id           BIGINT UNSIGNED,
    case_for_partner      VARCHAR(100) NOT NULL,
    case_for_zone         VARCHAR(100) NOT NULL,
    next_followup_date    DATE         NOT NULL,
    next_followup_time    TIME         NOT NULL,
    case_started_on       DATETIME     NOT NULL,
    first_assigned_on     DATETIME     NOT NULL,
    is_delete             BOOLEAN      NOT NULL,
    CREATEDATE            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    LASTMODIFIEDDATE      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATEDBYSTAFFID      BIGINT(10)   NOT NULL,
    LASTMODIFIEDBYSTAFFID BIGINT(10)   NOT NULL,
    FOREIGN KEY (reason_id) REFERENCES tblcasereasons (reason_id),
    FOREIGN KEY (case_for_id) REFERENCES tblstaffuser (staffid)
);

create table tblcaseassignment
(
    assignment_id SERIAL PRIMARY KEY,
    case_id       BIGINT(10) UNSIGNED,
    assignee_id   BIGINT(10) UNSIGNED,
    assigned_date DATE NOT NULL,
    FOREIGN KEY (case_id) REFERENCES tblcases (case_id),
    FOREIGN KEY (assignee_id) REFERENCES tblstaffuser (staffid)
);

create table tblcaseremarks
(
    remark_id             SERIAL PRIMARY KEY,
    case_id               BIGINT(10) UNSIGNED,
    remarks_logged_by_id  VARCHAR(100) NOT NULL,
    remark_date_time      DATETIME     NOT NULL,
    attachment            VARCHAR(100) NOT NULL,
    is_delete             BOOLEAN      NOT NULL,
    CREATEDATE            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    LASTMODIFIEDDATE      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CREATEDBYSTAFFID      BIGINT(10)   NOT NULL,
    LASTMODIFIEDBYSTAFFID BIGINT(10)   NOT NULL,
    FOREIGN KEY (case_id) REFERENCES tblcases (case_id)
);


-- Nilesh 17-05-2021
create table tbl_payu_payments
(
    id             SERIAL PRIMARY KEY,
    email          VARCHAR(100) NOT NULL,
    name           VARCHAR(100) NOT NULL,
    phone          VARCHAR(10)  NOT NULL,
    product_info   VARCHAR(100) NOT NULL,
    amount         VARCHAR(100) NOT NULL,
    payment_status VARCHAR(100) NOT NULL,
    txn_id         VARCHAR(100) NOT NULL,
    mihpay_id      VARCHAR(100),
    mode           VARCHAR(100),
    command        VARCHAR(100),
    payment_date   DATE
);

# Nilesh 23-05-2021
alter table TBLCUSTPACKAGEREL
    add qospolicyid bigint UNSIGNED;
alter table TBLCUSTPACKAGEREL
    add uploadqos varchar(120);
alter table TBLCUSTPACKAGEREL
    add downloadqos varchar(120);
alter table TBLCUSTPACKAGEREL
    add uploadts varchar(120);
alter table TBLCUSTPACKAGEREL
    add downloadts varchar(120);
alter table TBLCUSTPACKAGEREL
    add createdbystaffid NUMERIC(20);
alter table TBLCUSTPACKAGEREL
    add createdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL;
alter table TBLCUSTPACKAGEREL
    add lastmodifiedbystaffid NUMERIC(20);
alter table TBLCUSTPACKAGEREL
    add lastmodifieddate TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
alter table TBLCUSTPACKAGEREL
    add is_delete BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE TBLCUSTPACKAGEREL
    add FOREIGN KEY (qospolicyid) REFERENCES tbl_qos_policy (id);

alter table tblcustquotadtls
    add quotaunit varchar(100);
alter table tblcustquotadtls
    add timetotalquota decimal(20, 4);
alter table tblcustquotadtls
    add timequotaused decimal(20, 4);
alter table tblcustquotadtls
    add timequotaunit varchar(20);
alter table tblcustquotadtls
    add createdbystaffid NUMERIC(20);
alter table tblcustquotadtls
    add createdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL;
alter table tblcustquotadtls
    add lastmodifiedbystaffid NUMERIC(20);
alter table tblcustquotadtls
    add lastmodifieddate TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
alter table tblcustquotadtls
    add is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

#Nilesh 27-05-2021
#ALTER TABLE tblpartners DROP COLUMN parentpartnerid;
#ALTER TABLE tblpartners DROP COLUMN PARTNERID;
#ALTER TABLE tblpartners DROP COLUMN PARTNERNAME;
#ALTER TABLE tblmpostpaidplan DROP COLUMN is_deleted ;

#Nilesh 27-05-2021alter table tblpartners add is_delete BOOLEAN NOT NULL DEFAULT FALSE;


alter table tblmtax
    add is_delete BOOLEAN NOT NULL DEFAULT FALSE;
alter table tblmtiertax
    add is_delete BOOLEAN NOT NULL DEFAULT FALSE;
alter table tblroles
    add is_delete BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE tbl_partner_customer_rel
(
    id              SERIAL PRIMARY KEY,
    custid          BIGINT UNSIGNED NOT NULL,
    partnerid       BIGINT UNSIGNED NOT NULL,
    created_on      timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lastmodified_on timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (custid) REFERENCES tblcustomers (custid),
    FOREIGN KEY (partnerid) REFERENCES tblpartners (partnerid)
);

# Nilesh 31-05-2021
ALTER TABLE TBLMBILLRUN
    ADD is_delete BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE TBLCHARGES
    ADD is_delete BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE TBLMCOUNTRY
    ADD is_delete BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE TBLTCREDITDOC
    ADD is_delete BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE TBLMSUBSCRIBERADDRESSREL
    ADD is_delete BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE TBLTDEBITDOCUMENT
    ADD is_delete BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE TBLMDISCOUNT
    ADD is_delete BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE tbldunningrules
    ADD is_delete BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE tblserverdetail
    ADD is_delete BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE tbllocation
    ADD is_delete BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE tblmpartnerbillrun
    ADD is_delete BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE tblpartnercreditdoc
    ADD is_delete BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE TBLMPOSTPAIDPLAN
    ADD is_delete BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE TBLMTRIALBILLRUN
    ADD is_delete BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE TBLTTRIALDEBITDOCUMENT
    ADD is_delete BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE tbltemplatemanagement
    ADD is_delete BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE tblstaffuser
    ADD is_delete BOOLEAN NOT NULL DEFAULT FALSE;

# Nilesh 31-05-2021
alter table tblcustquotadtls
    add totalquotakb numeric(20, 4);
alter table tblcustquotadtls
    add usedquotakb numeric(20, 4);
alter table tblcustquotadtls
    add timeusedquotasec numeric(20, 4);
alter table tblcustquotadtls
    add timetotalquotasec numeric(20, 4);
alter table tblcustquotadtls
    add custpackageid integer references TBLCUSTPACKAGEREL (custpackageid);

#Utsav 01-06-2021

CREATE TABLE tbltemplate
(
    id            SERIAL PRIMARY KEY,
    template_name varchar(100) NOT NULL,
    template_type varchar(100) NOT NULL,
    template_file longtext,
    status        varchar(8)   NOT NULL
);

CREATE TABLE tbl_coomunication_emailjob
(
    id            SERIAL PRIMARY KEY,
    email         varchar(100) NOT NULL,
    subject       varchar(100) NOT NULL,
    body          varchar(100) DEFAULT NULL,
    schedule_time datetime     DEFAULT NULL,
    status        boolean      DEFAULT false,
    job_id        varchar(100) NOT NULL,
    job_grp       varchar(100) DEFAULT NULL,
    sended_at     datetime     DEFAULT NULL,
    is_sended     boolean      DEFAULT false,
    error         text
);

#Mohit - 01-06-2021
ALTER TABLE tblmpostpaidplan
    MODIFY COLUMN quotatime DECIMAL(20, 4) NULL;


#Jaymin-03-06-2021

alter table tblcharges
    add chargecategory varchar(50);

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES ('Installation Charge', 'INSTALLATION', 'chargeCategory', 'Active');

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES ('IP Charge', 'IP', 'chargeCategory', 'Active');

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES ('Termination Charge', 'TERMINATATION', 'chargeCategory', 'Active');

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES ('Transfer Charge', 'TRANSFER', 'chargeCategory', 'Active');

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES ('Plan Charge', 'PLAN', 'chargeCategory', 'Active');

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES ('Creation', 'Creation', 'TATConsideration', 'Active');

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES ('Assignment', 'Assignment', 'TATConsideration', 'Active');

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES ('Active', 'Active', 'commonStatus', 'Active');

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES ('Inactive', 'Inactive', 'commonStatus', 'Active');

#Utsav 09-06-2021
RENAME TABLE tbl_coomunication_emailjob TO tbl_communication_emailjob;


CREATE TABLE tblbroadcast
(
    broadcast_id          SERIAL PRIMARY KEY,
    createdbystaffid      int      NOT NULL,
    createdate            datetime NOT NULL,
    lastmodifiedbystaffid int      NOT NULL,
    lastmodifieddate      datetime     DEFAULT NULL,
    body                  varchar(255) DEFAULT NULL,
    custcondition         varchar(255) DEFAULT NULL,
    custstatus            varchar(255) DEFAULT NULL,
    emailsubject          varchar(255) DEFAULT NULL,
    expirydate            varchar(255) DEFAULT NULL,
    is_deleted            tinyint(1)   DEFAULT '0',
    networkdeviceid       bigint       DEFAULT NULL,
    planid                bigint       DEFAULT NULL,
    priority              varchar(255) DEFAULT NULL,
    serviceareaid         bigint       DEFAULT NULL,
    slotid                bigint       DEFAULT NULL,
    status                varchar(255) DEFAULT NULL,
    templateid            bigint       DEFAULT NULL,
    type                  varchar(255) DEFAULT NULL,
    customer_id           bigint       DEFAULT NULL,
    expiry_condition      varchar(25)  DEFAULT NULL,
    expirydate1           date         DEFAULT NULL,
    expirydate2           date         DEFAULT NULL,
    expirywithin          varchar(10)  DEFAULT NULL
);


CREATE TABLE tblbroadcastports
(
    broadportid  SERIAL PRIMARY KEY,
    is_deleted   bit(1) DEFAULT NULL,
    portid       bigint DEFAULT NULL,
    broadcast_id bigint UNSIGNED REFERENCES tblbroadcast (broadcast_id)
);


CREATE TABLE tbl_communication_smsjob
(
    id            SERIAL PRIMARY KEY,
    destination   varchar(14) NOT NULL,
    source        varchar(100)     DEFAULT NULL,
    message       varchar(1000)    DEFAULT NULL,
    schedule_time datetime         DEFAULT NULL,
    status        tinyint(1)       DEFAULT NULL,
    job_id        varchar(100)     DEFAULT NULL,
    job_grp       varchar(100)     DEFAULT NULL,
    sended_at     timestamp   NULL DEFAULT NULL,
    is_sended     tinyint(1)       DEFAULT NULL,
    error         text
);


#Jaymin 08-06-2021
create table tblsubscriberupdates
(
    id               SERIAL PRIMARY key,
    custid           BIGINT UNSIGNED references tblcustomers (custid),
    operation        varchar(200),
    oldval           longtext,
    newval           longtext,
    remarks          varchar(200),
    createdate       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    createdbystaffid BIGINT(100) NOT null,
    is_deleted       Boolean     NOT NULL DEFAULT false
);
# Nilesh 08-06-2021
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Add', 'Add', 'operation', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Replace', 'Replace', 'operation', 'Active');

ALTER TABLE tblippool
    ADD is_static_ip_pool BOOLEAN NOT NULL default FALSE;

# Nilesh 09-06-2021
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Issue', 'Issue', 'caseType', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Request', 'Request', 'caseType', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Inquiry', 'Inquiry', 'caseType', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Customer', 'Customer', 'caseFor', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Partner', 'Partner', 'caseFor', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Reseller', 'Reseller', 'caseFor', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Whatsapp', 'Whatsapp', 'origin', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Phone', 'Phone', 'origin', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('IVR', 'IVR', 'origin', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Self Care', 'Self Care', 'origin', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Mobile App', 'Mobile App', 'origin', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Unassinged', 'Unassinged', 'caseStatus', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Assigned', 'Assigned', 'caseStatus', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Closed', 'Closed', 'caseStatus', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Completed', 'Completed', 'caseStatus', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('On Hold', 'On Hold', 'caseStatus', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('In Progress', 'In Progress', 'caseStatus', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Low', 'Low', 'priority', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Medium', 'Medium', 'priority', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('High', 'High', 'priority', 'Active');

ALTER TABLE tblcustchargedtls
    ADD validity DECIMAL(20, 4);

ALTER TABLE tblmpostpaidplan
    add FOREIGN KEY (serviceid) REFERENCES tblmservices (serviceid);

ALTER TABLE tblmpostpaidplan
    ADD serviceid BIGINT UNSIGNED REFERENCES tblmservices (serviceid);

#Nilesh 10-06-2021
alter table tblippool
    add status varchar(50) not null;

#Mohit 10-06-2021

ALTER TABLE tblroles AUTO_INCREMENT = 51;

insert into tblroles(roleid, rolename, rstatus, created_on, lastmodified_on)
values (6, 'Sales Representative', 'ACTIVE', current_timestamp, current_timestamp);

ALTER TABLE tblmpostpaidplan MODIFY COLUMN STATUS char(100) DEFAULT 'ACTIVE' NOT NULL;

#ALTER TABLE tblcustpackagerel DROP COLUMN remark;

update tblcommonlist set list_value = 'TERMINATION'where list_value = 'TERMINATATION';

alter table tblcustomers add onuid varchar(100);


INSERT INTO tblippool (pool_id,pool_name,pool_type,pool_category,ip_range,net_mask,network_ip,broadcast_ip,first_host,last_host,total_host,is_delete,CREATEDATE,LASTMODIFIEDDATE,CREATEDBYSTAFFID,LASTMODIFIEDBYSTAFFID,is_static_ip_pool,status)
VALUES (1,'Pool 10','public','ipv4','10.10.0.1 - 10.10.0.3','255.255.255.252','10.10.0.1/30','10.10.0.3','10.10.0.1','10.10.0.2',2,0,'2021-05-08 11:40:44','2021-05-10 10:16:29',1,'1',0,'active');

INSERT INTO tblippooldtls (pool_details_id,pool_id,ip_address,status,is_delete,CREATEDATE,LASTMODIFIEDDATE,CREATEDBYSTAFFID,LASTMODIFIEDBYSTAFFID)
VALUES (1, 1,'10.10.0.1','Free',0,'2021-06-10 15:18:37','2021-06-10 15:18:37',1,'1');

INSERT INTO tblippooldtls (pool_details_id, pool_id,ip_address,status,is_delete,CREATEDATE,LASTMODIFIEDDATE,CREATEDBYSTAFFID,LASTMODIFIEDBYSTAFFID)
VALUES (2, 1,'10.10.0.2','Free',0,'2021-06-10 15:18:37','2021-06-10 15:18:37',1,'1');

ALTER TABLE TBLCUSTDOCDETAILS ADD cust_id bigint UNSIGNED;
ALTER TABLE TBLCUSTDOCDETAILS add FOREIGN KEY (cust_id) REFERENCES tblcustomers (custid);

# Nilesh 11-06-2021
alter table tblippool modify status varchar(50) ;

# Mohit 11-06-2021
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Proof of Identity', 'poi', 'docType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Proof Of Address', 'poa', 'docType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('KYC Signature', 'kycsign', 'docType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Cust Passport Image', 'custimage', 'docType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Employee Passport Image', 'empimage', 'docType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Employee Signature', 'empsign', 'docType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Quotation', 'quotation', 'docType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('CAF Form', 'cafform', 'docType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Other', 'other', 'docType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Driving License', 'poi_license', 'poi', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Voter Id', 'poi_voterid', 'poi', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('PAN Card', 'poi_pan', 'poi', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Aadhar Card', 'poi_aadhar', 'poi', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Leave & License Agreement', 'poi_rentagreement', 'poi', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('CAF', 'poi_caf', 'poi', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Driving License', 'poa_license', 'poa', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Voter Id', 'poa_voterid', 'poa', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Aadhar Card', 'poa_aadhar', 'poa', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Leave & License Agreement', 'poa_rentagreement', 'poa', 'Active');


# Jaymin 11-06-2021
Create table tblcaseresolutions
(
    res_id SERIAL PRIMARY KEY,
    res_name varchar(200),
    res_status varchar(10),
    is_delete BOOLEAN NOT NULL DEFAULT false,
    CREATEDATE            timestamp NULL DEFAULT NULL,
    LASTMODIFIEDDATE      timestamp NULL DEFAULT NULL,
    CREATEDBYSTAFFID      decimal(20, 0) DEFAULT NULL,
    LASTMODIFIEDBYSTAFFID decimal(20, 0) DEFAULT NULL
);

alter table tblippool add remark varchar(100);

#Mohit 14-06-2021 (No need to fire in test server,already there)

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Verified', 'verified', 'docStatus', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Verification Pending', 'pending', 'docStatus', 'Active');

#Utsav 15-06-2021

ALTER TABLE tblcustchargedtls ADD remarks varchar(100) DEFAULT NULL;

ALTER TABLE tblcustchargedtls ADD charge_date datetime NOT NULL;

ALTER TABLE tblcustchargedtls ADD taxamount decimal(20,4) DEFAULT NULL;

ALTER TABLE tblcustchargedtls ADD is_reversed tinyint(1) DEFAULT '0';

ALTER TABLE tblcustchargedtls ADD rev_date date DEFAULT NULL;

ALTER TABLE tblcustchargedtls ADD rev_amt decimal(20,4) DEFAULT NULL;

ALTER TABLE tblcustchargedtls ADD rev_remarks varchar(100) DEFAULT NULL;

ALTER TABLE tblcustchargedtls ADD startdate date DEFAULT NULL;

ALTER TABLE tblcustchargedtls ADD enddate date DEFAULT NULL;


# Nilesh 12-06-2021

alter table tblippool modify pool_name VARCHAR(100);
alter table tblippool modify pool_type VARCHAR(100);
alter table tblippool modify pool_category VARCHAR(100);
alter table tblippool modify ip_range  VARCHAR(100);
alter table tblippool modify net_mask  VARCHAR(100);
alter table tblippool modify network_ip VARCHAR(100);
alter table tblippool modify broadcast_ip  VARCHAR(100);
alter table tblippool modify first_host VARCHAR(100);
alter table tblippool modify last_host VARCHAR(100);
alter table tblippool modify total_host BIGINT ;
alter table tblippool modify is_delete BOOLEAN ;
alter table tblippool modify total_host BIGINT;
alter table tblippool modify is_delete BOOLEAN;

#Jaymin 17-06-2021
alter table tblcustomers modify didno varchar(75);
alter table tblcustomers modify childdidno varchar(75);
ALTER TABLE tblcustomers ADD intercomno varchar(75);
ALTER TABLE tblcustomers ADD intercomgrp varchar(75);

#Jaymin 17-06-2021
alter table tblcustomers modify didno varchar(75);
alter table tblcustomers modify childdidno varchar(75);
ALTER TABLE tblcustomers modify intercomno varchar(75);
ALTER TABLE tblcustomers modify intercomgrp varchar(75);

# Nilesh 16-06-2021

alter table TBLMSUBSCRIBERADDRESSREL add landmark varchar(100);
alter table TBLMSUBSCRIBERADDRESSREL add area varchar(100);

# Nilesh 18-06-2021
alter table tblcustmacmapping add is_deleted BOOLEAN DEFAULT false;

#INSERT INTO tblcommonlist (list_text, list_value, list_type, status) VALUES ('Active', 'Active', 'status', 'Active');
#INSERT INTO tblcommonlist (list_text, list_value, list_type, status) VALUES ('InActive', 'InActive', 'status', 'Active');
#INSERT INTO tblcommonlist (list_text, list_value, list_type, status) VALUES ('Suspend', 'Suspend', 'status', 'Active');

#Jaymin 21-06-2021
#INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('New Activation', 'NewActivation', 'status', 'Active');
#INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('Active', 'Active', 'status', 'Active');
#INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('In Active', 'InActive', 'status', 'Active');
#INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('Suspend', 'Suspend', 'status', 'Active');
#INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('Terminate', 'Terminate', 'status', 'Active');

# Mohit 22-06-2021

ALTER TABLE tblcases add current_assignee_id BIGINT UNSIGNED REFERENCES tblstaffuser (staffid);
ALTER TABLE tblcases add final_resolved_by_id BIGINT UNSIGNED REFERENCES tblstaffuser (staffid);
ALTER TABLE tblcases add final_closed_by_id BIGINT UNSIGNED REFERENCES tblstaffuser (staffid);
ALTER TABLE tblcases add final_resolution_id BIGINT UNSIGNED REFERENCES tblcaseresolutions (res_id);
alter table tblcases add column final_resolution_date timestamp NULL DEFAULT NULL;
alter table tblcases add column final_closed_date timestamp NULL DEFAULT NULL;

Create table tblcaseupdates
(
    updateid SERIAL PRIMARY KEY,
    caseid BIGINT unsigned,
    CREATEDATE            timestamp NULL DEFAULT NULL,
    LASTMODIFIEDDATE      timestamp NULL DEFAULT NULL,
    CREATEDBYSTAFFID      BIGINT(10)NOT NULL,
    LASTMODIFIEDBYSTAFFID BIGINT(10)not null,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    FOREIGN KEY (caseid) REFERENCES tblcases(case_id)
);

Create table tblcaseupdatedetails
(
    updatedtlsid SERIAL PRIMARY KEY,
    updateid BIGINT unsigned,
    operation varchar(100),
    entitytype varchar(100),
    oldvalue varchar(100),
    newvalue varchar(100),
    remarktype varchar(100),
    resolutionid BIGINT unsigned,
    attachment varchar(100),
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    FOREIGN KEY (updateid) REFERENCES tblcaseupdates(updateid),
    FOREIGN KEY (resolutionid) REFERENCES tblcaseresolutions(res_id)
);

update tblcommonlist set list_value = 'Unassigned' where list_value = 'Unassinged';
update tblcommonlist set list_text = 'Unassigned' where list_text = 'Unassinged';

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Resolved', 'Resolved', 'caseStatus', 'Active');

ALTER TABLE tblcases MODIFY COLUMN case_title varchar(100)  NULL;
ALTER TABLE tblcases MODIFY COLUMN case_number varchar(100)  NULL;
ALTER TABLE tblcases MODIFY COLUMN case_for_partner varchar(100)  NULL;
ALTER TABLE tblcases MODIFY COLUMN case_for_zone varchar(100)  NULL;
ALTER TABLE tblcases MODIFY COLUMN case_started_on datetime NULL;
ALTER TABLE tblcases MODIFY COLUMN first_assigned_on datetime NULL;
ALTER TABLE tblcases MODIFY COLUMN is_delete tinyint(1) DEFAULT 0 NOT NULL;

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Private', 'internal', 'remarkType', 'Active');

INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Public', 'external', 'remarkType', 'Active');

#NO Need To Fire Already There In Production
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('New Activation', 'NewActivation', 'custStatus', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('Active', 'Active', 'custStatus', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('In Active', 'InActive', 'custStatus', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('Suspend', 'Suspend', 'custStatus', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('Terminate', 'Terminate', 'custStatus', 'Active');

#Mohit 22-06-2021 //New
ALTER TABLE tblcustomers ADD COLUMN is_deleted tinyint(1) DEFAULT 0 NOT NULL;
ALTER TABLE tblcases ADD COLUMN first_remark text;
ALTER TABLE tblcaseupdates ADD COLUMN comment_by varchar(50);

#Vyom 23-06-2021
INSERT INTO tblcommonlist
(status, list_text, list_type, list_value)
VALUES('Active', 'Full', 'reversableType', 'Full');
INSERT INTO tblcommonlist
(status, list_text, list_type, list_value)
VALUES('Active', 'Prorated', 'reversableType', 'Prorated');

ALTER TABLE tblsubscriberupdates ADD textval LONGTEXT NULL;

#Mohit 23-06-2021

#View //Already Fired In Production
create or replace view vwlivsersnetwork as
select lv.* , cust.servicearea_id 'serviceAreaId', cust.network_device_id 'oltId'
, cust.oltslotid 'slotId'
, cust.oltportid 'portId'
from tblcustomers cust
inner join tblliveuser lv
on lv.UserName = cust.username ;

#Index //Already Fired In Production
create index ind_cust_username on tblcustomers(username);
create index ind_liveuser_username on tblliveuser (UserName);

alter table tblcustpackagerel CHANGE column is_deleted is_delete tinyint(1) ;

#Utsav 22-06-2021
ALTER TABLE tblsubscriberupdates ADD textval LONGTEXT NULL;
ALTER TABLE tbltcreditdoc ADD tdsflag BOOL NULL;
ALTER TABLE tbltcreditdoc ADD tdsamount DECIMAL(20,0) NULL;
ALTER TABLE tbltcreditdoc ADD is_reversed BOOL NULL;
ALTER TABLE tbltcreditdoc ADD resevrsed_date DATETIME NULL;
ALTER TABLE tbltcreditdoc ADD resverse_debitdoc_id BIGINT NULL;
ALTER TABLE tbltcreditdoc ADD tds_received BOOL NULL;
ALTER TABLE tbltcreditdoc ADD tds_received_date DATETIME NULL;
ALTER TABLE tbltcreditdoc ADD tds_credit_doc_id BIGINT NULL;
ALTER TABLE tbltdebitdocument ADD is_credit_reversal BOOL NULL;
ALTER TABLE tbltdebitdocument ADD credit_doc_id BIGINT NULL;

INSERT INTO tblcommonlist (status, list_text, list_type, list_value) VALUES('Active', 'TDS', 'paymentMode', 'TDS');
INSERT INTO tblcommonlist (status, list_text, list_type, list_value) VALUES('Active', 'RTGS/NEFT', 'paymentMode', 'RTGS/NEFT');
INSERT INTO tblcommonlist (status, list_text, list_type, list_value) VALUES('Active', 'Cheque', 'paymentMode', 'Cheque');
INSERT INTO tblcommonlist (status, list_text, list_type, list_value) VALUES('Active', 'Cash', 'paymentMode', 'Cash');
INSERT INTO tblcommonlist (status, list_text, list_type, list_value) VALUES('Active', 'Credit/Debit Card', 'paymentMode', 'Credit/Debit Card');

#Mohit 24-06-2021
ALTER TABLE tblcustomers DROP COLUMN defaultpool;
ALTER TABLE tblcustomers add defaultpoolid BIGINT UNSIGNED REFERENCES tblippool (pool_id);

#Jaymin 25-06-2021
ALTER TABLE tbltdebitdocument ADD COLUMN plan_id BIGINT UNSIGNED;
ALTER TABLE tbltdebitdocument add FOREIGN KEY (plan_id) references tblmpostpaidplan (POSTPAIDPLANID);

#Mohit - 25-06-2021 (No Need to fire in production)
alter table tblcaseupdatedetails add column filename varchar(100);

#Mohit - 29-06-2021
alter table tblcaseupdates add column createby varchar(100);
alter table tblcaseupdates add column updateby varchar(100);

#jaymin -29-06-2021
ALTER TABLE tblmcity
    ADD is_delete BOOLEAN NOT NULL default FALSE;
ALTER TABLE tblclientgroup
    ADD is_delete BOOLEAN NOT NULL default FALSE;
ALTER TABLE tblacctprofile
    ADD is_delete BOOLEAN NOT NULL default FALSE;
ALTER TABLE tblradiusprofile
    ADD is_delete BOOLEAN NOT NULL default FALSE;

#Mohit - 30-06-2021 (Already Fired In Demo Server)
ALTER TABLE tblcustomers MODIFY COLUMN voicesrvtype varchar(75) NULL;
insert into tblroles(roleid, rolename, rstatus, created_on, lastmodified_on)
values (7, 'Back Office Staff', 'ACTIVE', current_timestamp, current_timestamp);

#Mohit - 02-07-2021
CREATE TABLE tblpartnerservicearearel
(
	serviceareaid BIGINT  NOT NULL,
	partnerid BIGINT UNSIGNED NOT NULL,
	created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY(serviceareaid) REFERENCES tblservicearea(service_area_id),
	FOREIGN KEY(partnerid) REFERENCES tblpartners(partnerid)
);

#utsav 02-07-2021
ALTER TABLE tbl_communication_smsjob ADD template_id varchar(50) NULL;

CREATE TABLE tblnotifications (
  notification_id SERIAL PRIMARY KEY,
  name varchar(40) NOT NULL,
  email_enabled tinyint(1) NOT NULL DEFAULT '0',
  sms_enabled tinyint(1) NOT NULL DEFAULT '0',
  status varchar(11) DEFAULT NULL,
  category varchar(40) DEFAULT NULL,
  email_body varchar(1000) DEFAULT NULL,
  sms_body varchar(1000) DEFAULT NULL,
  createdbystaffid bigint DEFAULT NULL,
  createdate datetime DEFAULT NULL,
  lastmodifiedbystaffid bigint DEFAULT NULL,
  lastmodifieddate datetime DEFAULT NULL,
  is_deleted tinyint(1) DEFAULT '0',
  template_id varchar(100) NOT NULL
);

CREATE TABLE tblnotifcation_config (
  noti_config_id SERIAL PRIMARY KEY,
  notification_id bigint unsigned DEFAULT NULL,
  config_entity varchar(50) DEFAULT NULL,
  config_attribute varchar(50) DEFAULT NULL,
  config_atrr_type varchar(50) DEFAULT NULL,
  atrr_condi varchar(25) DEFAULT NULL,
  attr_value varchar(25) DEFAULT NULL,
  KEY tblnotifcation_config_FK (notification_id),
  CONSTRAINT tblnotifcation_config_FK FOREIGN KEY (notification_id) REFERENCES tblnotifications (notification_id)
);


#Mohit 05-06-2021
INSERT INTO tblnotifications (name,email_enabled,sms_enabled,status,category,email_body,sms_body,createdbystaffid,createdate,lastmodifiedbystaffid,lastmodifieddate,is_deleted,template_id) VALUES
	 ('Complain Resolution',0,1,'active','generic','','Dear%20$userName%2C%0A%0AYour%20complaint%206666%20has%20been%20Resolved.Kindly%20give%20your%20feedback%20regarding%20your%20interaction%20with%20our%20Customer%20Service%20%26%20Implementation%20Team%2C%20for%20any%20further%20Assistance%20kindly%20contact%20on%20our%20toll%20free%2018002664986.%0A%0A',4,'2021-06-30 18:51:04',4,'2021-06-30 18:51:04',0,'1207161786295759631'),
	 ('Registration',0,1,'active','generic','','UserName%20%3A%20$userName%20Password%20%3A%20$password%20%0A%0A%0',4,'2021-06-30 18:51:04',4,'2021-06-30 18:51:04',0,'1207160974476390926');


#Mohit 06-06-2021
ALTER TABLE tblcustdocdetails DROP COLUMN attachment_path;
alter table tblcustdocdetails add column filename varchar(200);
alter table tblcustdocdetails add column uniquename varchar(200);

#Utsav 07-07-2021

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES('DID', 'DID', 'voiceQuotaType', 'Active');

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES('Intercom', 'Intercom', 'voiceQuotaType', 'Active');

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES('Both', 'VoiceBoth', 'voiceQuotaType', 'Active');

ALTER TABLE tblmpostpaidplan ADD quotadid DECIMAL(50) NULL;
ALTER TABLE tblmpostpaidplan ADD quotaintercom DECIMAL(50) NULL;

ALTER TABLE tblcustquotadtls ADD didtotalquota DECIMAL(50) NULL;
ALTER TABLE tblcustquotadtls ADD didusedquota DECIMAL(50) NULL;
ALTER TABLE tblcustquotadtls ADD intercomtotalquota DECIMAL(50) NULL;
ALTER TABLE tblcustquotadtls ADD intercomusedquota DECIMAL(50) NULL;

#Utsav 06-07-2021
INSERT INTO tblnotifications
(name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)
VALUES('Wind and Rainfall', 0, 1, 'active', 'generic', '', 'Dear%20EthernetXpress%20Customer%21%21%20Due%20to%20heavy%20winds%20and%20rainfall%20our%20main%20fibre%20connectivity%20has%20been%20affected%20in%20your%20area.%0AOur%20team%20is%20working%20on%20the%20same.%0AInconvenience%20caused%20is%20highly%20regretted.%20%0A%0', 1, '2021-07-08 13:48:00', 1, '2021-07-08 13:48:00', 0, '1207162200637799409');

#Jaymin 10-07-2021
alter table tblpartners add column balance BIGINT(20);

#Jaymin 13-07-2021
alter table tblpartners add column pricebookid bigint(20) unsigned;
alter table tblpartners add foreign key (pricebookid) references tblpricebook(bookid);

#Utsav 15-07-2021


# Vyom 11-08-2021
ALTER TABLE tblcustomers add column voiceprovision boolean default false;
