CREATE TABLE tblcustomers(
    custid SERIAL PRIMARY KEY,
    username varchar(100) UNIQUE,
    password varchar(100),
    firstname varchar(100),
	lastname varchar(100),
    email varchar(100) ,
    created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tblradiuscustomerreply(
    attributeid SERIAL PRIMARY KEY,
	custid INTEGER NOT NULL REFERENCES tblcustomers(custid),   
    attribute VARCHAR(150),
    attributevalue VARCHAR(150)
 );
 
 CREATE TABLE tblclients(
   clientid SERIAL PRIMARY KEY,
   clientip VARCHAR (100) NOT NULL,
   sharedkey VARCHAR (50) NOT NULL,
   timeout VARCHAR (5)NOT NULL,
   created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP
 );

  CREATE TABLE tblradiusserver(
   radiusid SERIAL PRIMARY KEY,
   radiusip VARCHAR (100) NOT NULL,
   webport VARCHAR (50) NOT NULL,
   authport VARCHAR (5)NOT NULL,
   acctport VARCHAR (5)NOT NULL,
   status VARCHAR (1) NOT NULL,
   created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP
 );
   

  CREATE TABLE  tblliveuser(
CDRID                     SERIAL PRIMARY KEY,
UserName                     VARCHAR(150),
UserPassword                 VARCHAR(150),
CHAPPassword                 VARCHAR(150),
NASIPAddress                VARCHAR(150),
NASPort                      VARCHAR(150),
ServiceType                  VARCHAR(150),
FramedProtocol               VARCHAR(150),
FramedIPAddress             VARCHAR(150),
FramedIPNetmask             VARCHAR(150),
FramedRouting                VARCHAR(150),
FilterId                     VARCHAR(150),
FramedMTU                    VARCHAR(150),
FramedCompression            VARCHAR(150),
LoginIPHost                 VARCHAR(150),
LoginService                 VARCHAR(150),
LoginTCPPort                VARCHAR(150),
ReplyMessage                 VARCHAR(150),
CallbackNumber               VARCHAR(150),
CallbackId                   VARCHAR(150),
FramedRoute                  VARCHAR(150),
FramedIPXNetwork            VARCHAR(150),
State                         VARCHAR(150),
Class                         VARCHAR(150),
VendorSpecific               VARCHAR(150),
SessionTimeout               VARCHAR(150),
IdleTimeout                  VARCHAR(150),
TerminationAction            VARCHAR(150),
CalledStationId             VARCHAR(150),
CallingStationId            VARCHAR(150),
NASIdentifier                VARCHAR(150),
ProxyState                   VARCHAR(150),
LoginLATService             VARCHAR(150),
LoginLATNode                VARCHAR(150),
LoginLATGroup               VARCHAR(150),
FramedAppleTalkLink         VARCHAR(150),
FramedAppleTalkNetwork      VARCHAR(150),
FramedAppleTalkZone         VARCHAR(150),
AcctStatusType              VARCHAR(150),
AcctDelayTime               VARCHAR(150),
AcctInputOctets             VARCHAR(150),
AcctOutputOctets            VARCHAR(150),
AcctSessionId               VARCHAR(150),
AcctAuthentic		          VARCHAR(150),
AcctSessionTime             VARCHAR(150),
AcctInputPackets            VARCHAR(150),
AcctOutputPackets	          VARCHAR(150),
AcctTerminateCause	      VARCHAR(150),
AcctMultiSessionId	      VARCHAR(150),
AcctLinkCount	              VARCHAR(150),
AcctInputGigawords          VARCHAR(150),
AcctOutputGigawords         VARCHAR(150),
EventTimestamp               VARCHAR(150),
CHAPChallenge	              VARCHAR(150),
NASPortType                  VARCHAR(150),
PortLimit                    VARCHAR(150),
LoginLATPort                 VARCHAR(150),
AcctTunnelConnection	      VARCHAR(150),
ARAPPassword                 VARCHAR(150),
ARAPFeatures                 VARCHAR(150),
ARAPZoneAccess              VARCHAR(150),
ARAPSecurity                 VARCHAR(150),
ARAPSecurityData            VARCHAR(150),
PasswordRetry                VARCHAR(150),
Prompt                        VARCHAR(150),
ConnectInfo			      VARCHAR(150),
ConfigurationToken		      VARCHAR(150),
EAPMessage                   VARCHAR(150),
MessageAuthenticator	      VARCHAR(150),
ARAPChallengeResponse	      VARCHAR(150),
AcctInterimInterval         VARCHAR(150),
NASPortId                   VARCHAR(150),
FramedPool                   VARCHAR(150),
NASIPv6Address	          VARCHAR(150),
FramedInterfaceId	          VARCHAR(150),
FramedIPv6Prefix	          VARCHAR(150),
LoginIPv6Host	              VARCHAR(150),
FramedIPv6Route	          VARCHAR(150),
FramedIPv6Pool	          VARCHAR(150),
DigestResponse	              VARCHAR(150),
DigestAttributes		      VARCHAR(150),
CREATE_DATE               	  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
LAST_MODIFIED_DATE        	  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE tblacctcdr(
CDRID                     SERIAL PRIMARY KEY,
UserName                     VARCHAR(150),
UserPassword                 VARCHAR(150),
CHAPPassword                 VARCHAR(150),
NASIPAddress                VARCHAR(150),
NASPort                      VARCHAR(150),
ServiceType                  VARCHAR(150),
FramedProtocol               VARCHAR(150),
FramedIPAddress             VARCHAR(150),
FramedIPNetmask             VARCHAR(150),
FramedRouting                VARCHAR(150),
FilterId                     VARCHAR(150),
FramedMTU                    VARCHAR(150),
FramedCompression            VARCHAR(150),
LoginIPHost                 VARCHAR(150),
LoginService                 VARCHAR(150),
LoginTCPPort                VARCHAR(150),
ReplyMessage                 VARCHAR(150),
CallbackNumber               VARCHAR(150),
CallbackId                   VARCHAR(150),
FramedRoute                  VARCHAR(150),
FramedIPXNetwork            VARCHAR(150),
State                         VARCHAR(150),
Class                         VARCHAR(150),
VendorSpecific               VARCHAR(150),
SessionTimeout               VARCHAR(150),
IdleTimeout                  VARCHAR(150),
TerminationAction            VARCHAR(150),
CalledStationId             VARCHAR(150),
CallingStationId            VARCHAR(150),
NASIdentifier                VARCHAR(150),
ProxyState                   VARCHAR(150),
LoginLATService             VARCHAR(150),
LoginLATNode                VARCHAR(150),
LoginLATGroup               VARCHAR(150),
FramedAppleTalkLink         VARCHAR(150),
FramedAppleTalkNetwork      VARCHAR(150),
FramedAppleTalkZone         VARCHAR(150),
AcctStatusType              VARCHAR(150),
AcctDelayTime               VARCHAR(150),
AcctInputOctets             VARCHAR(150),
AcctOutputOctets            VARCHAR(150),
AcctSessionId               VARCHAR(150),
AcctAuthentic		          VARCHAR(150),
AcctSessionTime             VARCHAR(150),
AcctInputPackets            VARCHAR(150),
AcctOutputPackets	          VARCHAR(150),
AcctTerminateCause	      VARCHAR(150),
AcctMultiSessionId	      VARCHAR(150),
AcctLinkCount	              VARCHAR(150),
AcctInputGigawords          VARCHAR(150),
AcctOutputGigawords         VARCHAR(150),
EventTimestamp               VARCHAR(150),
CHAPChallenge	              VARCHAR(150),
NASPortType	              VARCHAR(150),
PortLimit                    VARCHAR(150),
LoginLATPort	              VARCHAR(150),
AcctTunnelConnection	      VARCHAR(150),
ARAPPassword                 VARCHAR(150),
ARAPFeatures                 VARCHAR(150),
ARAPZoneAccess              VARCHAR(150),
ARAPSecurity                 VARCHAR(150),
ARAPSecurityData            VARCHAR(150),
PasswordRetry                VARCHAR(150),
Prompt                        VARCHAR(150),
ConnectInfo			      VARCHAR(150),
ConfigurationToken		      VARCHAR(150),
EAPMessage                   VARCHAR(150),
MessageAuthenticator	      VARCHAR(150),
ARAPChallengeResponse	      VARCHAR(150),
AcctInterimInterval         VARCHAR(150),
NASPortId                   VARCHAR(150),
FramedPool                   VARCHAR(150),
NASIPv6Address	          VARCHAR(150),
FramedInterfaceId	          VARCHAR(150),
FramedIPv6Prefix	          VARCHAR(150),
LoginIPv6Host	              VARCHAR(150),
FramedIPv6Route	          VARCHAR(150),
FramedIPv6Pool	          VARCHAR(150),
DigestResponse	              VARCHAR(150),
DigestAttributes		      VARCHAR(150),
CREATE_DATE               	  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
LAST_MODIFIED_DATE        	  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);


create table tbldbmappingmaster(
	mappingmasterid  SERIAL PRIMARY KEY,
	mappingname VARCHAR (100) NOT NULL
);

create table tbldbmapping(
	mappingid  SERIAL PRIMARY KEY,
	mappingmasterid INTEGER NOT NULL REFERENCES tbldbmappingmaster(mappingmasterid),   
	radiusname  VARCHAR (100) NOT NULL,
	dbcolumnname  VARCHAR (100) NOT NULL
);

alter table tblcustomers add cstatus varchar(100);
alter table tblcustomers add last_login_time timestamp DEFAULT CURRENT_TIMESTAMP;

alter table tblcustomers add failcount integer;

alter table tblclients add iptype VARCHAR (100);


CREATE TABLE tblradiusclientreply(
    attributeid SERIAL PRIMARY KEY,
	clientid INTEGER NOT NULL REFERENCES tblclients(clientid),   
    attribute VARCHAR(150),
    attributevalue VARCHAR(150)
);
 
 
CREATE TABLE tblradiusprofile(
   radiusprofileid SERIAL PRIMARY KEY,
   name VARCHAR (100) NOT NULL,
   status VARCHAR (1) NOT NULL,
   created_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP,
   lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP
);
 
  
CREATE TABLE tblradiusprofilecheckitm(
    radiuscheckitmid SERIAL PRIMARY KEY,
    checkitem VARCHAR(150),
	radiusprofileid INTEGER NOT NULL REFERENCES tblradiusprofile(radiusprofileid),   
    created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lastmodified_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

 CREATE TABLE tblradiusprofilereplyitm(
    radiusreplyitmid SERIAL PRIMARY KEY,
    attribute VARCHAR(150),
    attributevalue VARCHAR(150),
	radiusprofileid INTEGER NOT NULL REFERENCES tblradiusprofile(radiusprofileid),   
	radiuscheckitmid INTEGER NOT NULL REFERENCES tblradiusprofilecheckitm(radiuscheckitmid),   
	created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
   lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP
);


  CREATE TABLE tblclientgroup(
    clientgroupid SERIAL PRIMARY KEY,
    name VARCHAR(150),
    created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	lastmodified_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
 );
	
 CREATE UNIQUE INDEX clientgroup_name_unq ON tblclientgroup (name);

 alter table tblclientgroup add cgstatus varchar(50);
 
 update  tblclientgroup set cgstatus='ACTIVE' where name='Default';
 
 insert into tblclientgroup(name,created_on,lastmodified_on,cgstatus) values('Default',current_timestamp,current_timestamp,'Active');
 alter table tblclients add clientgroupid INTEGER REFERENCES tblclientgroup(clientgroupid);
 
 create table tblradiusprocustrel(
  radiusprocustrelid SERIAL PRIMARY KEY,
  custid INTEGER NOT NULL REFERENCES tblcustomers(custid),
  radiusprofileid INTEGER NOT NULL REFERENCES tblradiusprofile(radiusprofileid)
 );
  
 drop table tblradiusprocustrel;
 create table tblradiusprocustrel(
  custid INTEGER NOT NULL REFERENCES tblcustomers(custid),
  radiusprofileid INTEGER NOT NULL REFERENCES tblradiusprofile(radiusprofileid),
  primary key (custid,radiusprofileid)
 );
 
create table tblstaffuser(
  staffid SERIAL PRIMARY KEY,
  username VARCHAR(100),
  password VARCHAR(100),
  firstname varchar(100),
  lastname varchar(100),
  email varchar(100) ,
  phone varchar(20) ,
  failcount integer,
  sstatus varchar(100),
  last_login_time timestamp,
  created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP
);

 alter table tblcustomers add last_password_change timestamp DEFAULT CURRENT_TIMESTAMP not null;
 
 CREATE TABLE tblacctprofile(
   acctprofileid SERIAL PRIMARY KEY,
   name VARCHAR (100) NOT NULL,
   status VARCHAR (1) NOT NULL,
   checkitem VARCHAR(150),
   accountcdrstatus VARCHAR(1),
   sessionstatus VARCHAR(1),
   mappingmasterid INTEGER NOT NULL REFERENCES tbldbmappingmaster(mappingmasterid),
   priority integer,
   created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
   lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP
);
  



create table tblroles(
	roleid SERIAL PRIMARY KEY,
	rolename varchar(50),
	rstatus varchar(50),
	created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lastmodified_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

create table tblstaffrolerel(
	staffrolerelid SERIAL PRIMARY KEY,
	staffid INTEGER NOT NULL REFERENCES tblstaffuser(staffid),
	roleid INTEGER NOT NULL REFERENCES tblroles(roleid)
);
alter table tblstaffuser modify password  varchar(500);


create table tblaclclass(
    classid bigint primary key,
    classname varchar(100) not null,
    dispname varchar(100) not null,
    disporder int
);

create table tblaclentry(
    aclid SERIAL primary key,
	classid bigint,
	roleid  bigint,
	permit int
);



alter table tbldbmappingmaster add column created_on timestamp DEFAULT CURRENT_TIMESTAMP;
alter table tbldbmappingmaster add column lastmodified_on timestamp  DEFAULT CURRENT_TIMESTAMP;

create table tblauthdrivers
(
	driverid SERIAL PRIMARY KEY,
	name varchar(50),
	drivertype varchar(1),
	dstatus varchar(100),
	created_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP,
	lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP,
	ldap_url varchar(500),
	ldap_authtype varchar(50),
	ldap_username varchar(500),
	ldap_password varchar(100),
	ldap_searchparams varchar(1000)
);

create table tblradiusauthconfig
(
	authconfigid SERIAL PRIMARY KEY,
	radserver integer references tblradiusserver(radiusid),
	checkitem varchar(150),
	authdriver integer references tblauthdrivers(driverid) 	
);


create table tblauthresponse
(
	username varchar(500),
	replymessage varchar(1000),
	packettype varchar(3),
	clientip varchar(50),
	clientgroup varchar(500),
	created_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP,
	lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP
);

alter table tblauthresponse add column authres_id int(10) unsigned primary KEY AUTO_INCREMENT;


create table tblserverconf
(
    serverconf SERIAL PRIMARY KEY,
	attributename varchar(100),
	attributevalue varchar(1000),
	created_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP,
	lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP
);


#OSS BSS

create table tblmplanmaster(
  plid SERIAL PRIMARY KEY,
  name VARCHAR(100),
  planTYpe VARCHAR(100),
  validity integer,
  stml varchar(100) ,
  quota varchar(20) ,
  startdate timestamp DEFAULT CURRENT_TIMESTAMP,
  enddate timestamp DEFAULT CURRENT_TIMESTAMP
  );
  
  CREATE TABLE tblvouchermaster
 (
	 vcid SERIAL PRIMARY KEY,
	 vcname VARCHAR (100) NOT NULL,
	 vcqty VARCHAR (50) NOT NULL,
	 uppercase VARCHAR (100),
	 lowercase VARCHAR (100),
	 voucherlength VARCHAR (100) NOT NULL,
	 vouchervalidity VARCHAR (100) NOT NULL
 );
 
alter table tblvouchermaster add plid INTEGER REFERENCES tblmplanmaster(plid);

alter table tblvouchermaster add numericval VARCHAR (100);

CREATE TABLE tblvoucherbatch
(   vbid SERIAL PRIMARY KEY,
    vouchercode VARCHAR (100) NOT NULL,
    vcid integer NOT NULL,
    planid VARCHAR (50) NOT NULL,
    validity timestamp
);



CREATE TABLE TBLMMVNO
  (
    MVNOID           serial,
    NAME             VARCHAR(64) NOT NULL,
    SUFFIX           VARCHAR(16),
    DESCRIPTION      VARCHAR(255),
    EMAIL            VARCHAR(255),
    PHONE            VARCHAR(255),
    ADDRESS          VARCHAR(255),
    STATUS           CHAR(1),
    LOGOFILE         VARCHAR(255),
    MVNOHEADER       VARCHAR(255),
    MVNOFOOTER       VARCHAR(255),
    CREATEDATE       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (MVNOID)
  );

  CREATE TABLE TBLMTAX
  (
    TAXID                 serial,
    NAME                  VARCHAR(64) NOT NULL,
    DESCRIPTION           VARCHAR(255),
    TAXTYPE               VARCHAR(8),
    STATUS                CHAR(1) DEFAULT 'Y' NOT NULL,
    CREATEDATE            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    MVNOID                bigint UNSIGNED,
    PRIMARY KEY (TAXID),
    FOREIGN KEY (MVNOID) REFERENCES TBLMMVNO (MVNOID)
  );
  
CREATE TABLE TBLMTIERTAX
  (
    TIERTAXID serial,
    NAME      VARCHAR(64) NOT NULL,
    TAXGROUP  VARCHAR(10),
    RATE      NUMERIC(10,2),
    TAXID     bigint UNSIGNED,
    PRIMARY KEY (TIERTAXID),	
    FOREIGN KEY (TAXID) REFERENCES TBLMTAX (TAXID)
  );
  
CREATE TABLE TBLMSLABTAX
  (
    SLABTAXID serial,
    NAME      VARCHAR(64) NOT NULL,
    RANGEFROM NUMERIC(16,4),
    RANGEUPTO NUMERIC(16,4),
    RATE      NUMERIC(10,2),
    TAXID     bigint UNSIGNED,
    PRIMARY KEY (SLABTAXID),
    FOREIGN KEY (TAXID) REFERENCES TBLMTAX (TAXID)
  );
  
CREATE TABLE TBLMPOSTPAIDPLAN
  (
    POSTPAIDPLANID        serial,
    NAME                  VARCHAR(64)   NOT NULL,
    DISPLAYNAME           VARCHAR(128)  NOT NULL,
    PLANCODE              VARCHAR(8)    NOT NULL,
    DESCRIPTION           VARCHAR(255)  NOT NULL,
    PLANCATEGORY          VARCHAR(32)   NOT NULL,
    MAXALLOWEDCHILD       NUMERIC(8)    NOT NULL,
    STARTDATE             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ENDDATE               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    QUOTA                 NUMERIC(14,4) ,
    QUOTAUNIT             VARCHAR(8),
    UPLOADQOS             VARCHAR(128),
    DOWNLOADQOS           VARCHAR(128),
    STATUS                CHAR(1) DEFAULT 'Y' NOT NULL,
    PLANSTATUS            VARCHAR(32) DEFAULT 'INACTIVE' NOT NULL,
    CHILDQUOTA            NUMERIC(14,4),
    CHILDQUOTAUNIT        VARCHAR(8),
    SLICE                 NUMERIC(14,4),
    SLICEUNIT             VARCHAR(8),
    PARAM1                VARCHAR(4000),
    PARAM2                VARCHAR(4000),
    PARAM3                VARCHAR(4000),
    ATTACHEDTOALLHOTSPOT  VARCHAR(8) DEFAULT 'false',
    CREATEDATE            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    MVNOID                bigint UNSIGNED,
	TAXID                 bigint UNSIGNED,
    PRIMARY KEY (POSTPAIDPLANID),
    FOREIGN KEY (MVNOID) REFERENCES TBLMMVNO (MVNOID),
    FOREIGN KEY (TAXID) REFERENCES TBLMTAX (TAXID)
  );

  CREATE TABLE TBLMPOSTPAIDPLANCHARGEREL
  (
	POSTPAIDPLANCHARGERELID serial,
	NAME VARCHAR(64),
	DESCRIPTION VARCHAR(255),
	PRICE NUMERIC(16,4),
	CHARGETYPE VARCHAR(32),
	BILLINGCYCLE NUMERIC(2,0),
	CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	POSTPAIDPLANID bigint UNSIGNED,
	PRIMARY KEY (POSTPAIDPLANCHARGERELID),
	FOREIGN KEY (POSTPAIDPLANID) REFERENCES TBLMPOSTPAIDPLAN (POSTPAIDPLANID)
  );
 
 
 CREATE TABLE TBLMDISCOUNT
  (
    DISCOUNTID  serial,
    NAME        VARCHAR(255) NOT NULL,
    DESCRIPTION VARCHAR(255),
    STATUS      CHAR(1) DEFAULT 'Y' NOT NULL,
    CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    MVNOID bigint UNSIGNED,
    PRIMARY KEY (DISCOUNTID),
    FOREIGN KEY (MVNOID) REFERENCES TBLMMVNO (MVNOID)
  );
  
CREATE TABLE TBLMDISCOUNTFIELDMAPPING
  (
    DISCOUNTFIELDMAPPINGID serial,
    VALIDFROM              TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    VALIDUPTO              TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    DISCOUNTTYPE           VARCHAR(16),
    DISCOUNT               NUMERIC(16,4),
    DISCOUNTID             bigint UNSIGNED,
    PRIMARY KEY (DISCOUNTFIELDMAPPINGID),
    FOREIGN KEY (DISCOUNTID) REFERENCES TBLMDISCOUNT (DISCOUNTID)
  );
  
CREATE TABLE TBLMDISCOUNTPOSTPAIDPLANREL
  (
    DISCOUNTPLANRELID serial,
  	DISCOUNTID     bigint UNSIGNED,
    POSTPAIDPLANID bigint UNSIGNED,
    PRIMARY KEY (DISCOUNTPLANRELID),
    FOREIGN KEY (DISCOUNTID) REFERENCES TBLMDISCOUNT (DISCOUNTID),
    FOREIGN KEY (POSTPAIDPLANID) REFERENCES TBLMPOSTPAIDPLAN (POSTPAIDPLANID)
  );
  
  
  
  
 alter table tblcustomers add accountnumber varchar(100);
 alter table tblcustomers add accounttype varchar(100);
 alter table tblcustomers add birthdate timestamp DEFAULT CURRENT_TIMESTAMP;
 alter table tblcustomers add country varchar(100);
 alter table tblcustomers add cui varchar(100);
 alter table tblcustomers add customertype varchar(100);
 alter table tblcustomers add gender varchar(10);
 alter table tblcustomers add imsi varchar(20);
 alter table tblcustomers add phone varchar(20);
 alter table tblcustomers add subscriberpackage varchar(20);
 alter table tblcustomers add subscriberpackageid varchar(20); 
 
alter table tblcustomers add createdate timestamp DEFAULT CURRENT_TIMESTAMP;
alter table tblcustomers add expirydate timestamp DEFAULT CURRENT_TIMESTAMP;
alter table tblcustomers add laststatuschangedate timestamp DEFAULT CURRENT_TIMESTAMP;

alter table tblcustomers add NEXTBILLDATE timestamp DEFAULT CURRENT_TIMESTAMP;
alter table tblcustomers add LASTBILLDATE timestamp NULL DEFAULT NULL;
alter table tblcustomers add BILLDAY NUMERIC(20);


CREATE TABLE TBLMCOUNTRY
  (
    COUNTRYID serial,
    NAME      VARCHAR(64) NOT NULL,
    STATUS    CHAR(1) DEFAULT 'Y' NOT NULL,
    CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_MCOUNTRY PRIMARY KEY (COUNTRYID)
  );
  
CREATE TABLE TBLMSTATE
  (
    STATEID   serial,
    NAME      VARCHAR(64) NOT NULL,
    COUNTRYID NUMERIC(20),
    STATUS    CHAR(1) DEFAULT 'Y' NOT NULL,
    CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_MSTATE PRIMARY KEY (STATEID)
  );
  
CREATE TABLE TBLMCITY
  (
    CITYID  serial,
    NAME    VARCHAR(64) NOT NULL,
    STATEID NUMERIC(20),
    STATUS  CHAR(1) DEFAULT 'Y' NOT NULL,
    CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_MCITY PRIMARY KEY (CITYID)
  );
  
CREATE TABLE TBLMSUBSCRIBERADDRESSREL
  (
  	ADDRESSID SERIAL,
    SUBSCRIBERID bigint UNSIGNED,
    ADDRESSTYPE  VARCHAR(16),
    ADDRESS1     VARCHAR(255),
    ADDRESS2     VARCHAR(255),
    CITYID       bigint UNSIGNED,
    STATEID      bigint UNSIGNED,
    COUNTRYID    bigint UNSIGNED,
    PINCODE      NUMERIC(10),
    CONSTRAINT PK_MSUBSCRIBERADDRESS PRIMARY KEY (SUBSCRIBERID, ADDRESSTYPE),
	FOREIGN KEY (SUBSCRIBERID) REFERENCES tblcustomers (custid),
    FOREIGN KEY (CITYID) REFERENCES TBLMCITY (CITYID),
    FOREIGN KEY (STATEID) REFERENCES TBLMSTATE (STATEID),
    FOREIGN KEY (COUNTRYID) REFERENCES TBLMCOUNTRY (COUNTRYID)
);
  
CREATE INDEX IDX_SUBSCRIBERADDRESSREL ON TBLMSUBSCRIBERADDRESSREL (SUBSCRIBERID);

CREATE TABLE TBLCLIENTSERVICE 
(
    SERVICEID serial, 
	NAME VARCHAR(64) NOT NULL,
	VALUE VARCHAR(255),
	PRIMARY KEY (SERVICEID)
);


  CREATE TABLE TBLNEXTBILLDATE 
   (	 SUBSCRIBERID  serial, 
	 NEXTBILLDATE  TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
	 LASTBILLDATE  TIMESTAMP NULL DEFAULT NULL, 
	 BILLDAY  NUMERIC(20),
	FOREIGN KEY (SUBSCRIBERID) REFERENCES tblcustomers (custid)
   ) ;
   

  

create table TBLMBILLRUN
(
	billrunid serial,
	billruncreatedate TIMESTAMP  DEFAULT CURRENT_TIMESTAMP, 
	billrundate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	billruncount NUMERIC(10), 
	amount NUMERIC(20,4),
	status varchar(15),
	billruncompletedate TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (billrunid )
);

 alter table TBLMBILLRUN add SUCCESSCOUNT  NUMERIC(20);
 alter table TBLMBILLRUN add failcount  NUMERIC(20);



create table TBLTDEBITDOCUMENT
(
	debitdocumentid serial,
	debitdocumentnumber varchar(200),
	subscriberid BIGINT UNSIGNED,
	billdate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP, 
	createdate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	startdate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	enddate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	duedate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	latepaymentdate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	subtotal NUMERIC(20,4) default 0,
	tax NUMERIC(20,4) default 0,
	discount NUMERIC(20,4) default 0,
	totalamount NUMERIC(20,4) default 0,
	previousbalance NUMERIC(20,4) default 0,
	latepaymentfee NUMERIC(20,4) default 0,
	currentpayment NUMERIC(20,4) default 0,
	currentdebit NUMERIC(20,4) default 0,
	currentcredit NUMERIC(20,4) default 0,
	totaldue NUMERIC(20,4) default 0,
	totalamountinwords varchar(200),
	totaldueinwords varchar(200),
	billrunid BIGINT UNSIGNED,
	billrunstatus varchar(200),
	xmldocument LONGTEXT,
	PRIMARY KEY (debitdocumentid ),
	FOREIGN KEY (subscriberid) REFERENCES tblcustomers (custid),
	FOREIGN KEY (billrunid) REFERENCES TBLMBILLRUN (billrunid)
);

alter table TBLTDEBITDOCUMENT add email varchar(200);

alter table TBLTDEBITDOCUMENT add phone varchar(200);

create table TBLTDEBITDOCUMENTDETAIL
(
	debitdocdetailid serial primary key,
	debitdocumentid BIGINT UNSIGNED NOT NULL,
	chargeid numeric(20) NOT NULL,
	chargename varchar(400),
	description varchar(400),
	chargetype varchar(200),
	chargecycle varchar(200),
	subtotal NUMERIC(20,4) default 0,
	tax NUMERIC(20,4) default 0,
	discount NUMERIC(20,4) default 0,
	totalamount NUMERIC(20,4) default 0,
	startdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	enddate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	prorationtype varchar(100),
	noofcycle NUMERIC(20,4) default 0,
	FOREIGN KEY (debitdocumentid) REFERENCES TBLTDEBITDOCUMENT (debitdocumentid)
);



create table TBLTDEBITDOCUMENTTAXREL
(
	debitdoctaxid serial primary key,
	debitdocumentid BIGINT UNSIGNED NOT NULL,
	taxid BIGINT UNSIGNED NOT NULL,
	taxname varchar(255),
	description varchar(255),
	percentage NUMERIC(20,4) default 0,
	taxlevel NUMERIC(20,4) default 0,
	startdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	enddate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	amount NUMERIC(20,4) default 0,
	FOREIGN KEY (debitdocumentid) REFERENCES TBLTDEBITDOCUMENT (debitdocumentid),
	FOREIGN KEY (taxid) REFERENCES tblmtax (taxid)
);



create table TBLTDEBITDOCUMENTADDRESSREL
(
	debitdocaddrid serial primary key,
	debitdocumentid BIGINT UNSIGNED NOT NULL,
	addresstype varchar(25),
	address1 varchar(255),
	address2 varchar(255),
	city varchar(30),
	state varchar(30),
	country varchar(30),
	pincode varchar(30),
	FOREIGN KEY (debitdocumentid) REFERENCES TBLTDEBITDOCUMENT (debitdocumentid)
);

	
CREATE TABLE TBLUSERAUDIT 
(	 
   	 ACCOUNTNUMBER VARCHAR(64), 
	 BILLRUNID  NUMERIC(20), 
	 CURRDATE  TIMESTAMP (6), 
	 COMPLETEDSTATUS  NUMERIC(20)
);

CREATE TABLE TBLCUSTPACKAGEREL
(
	custpackageid SERIAL PRIMARY KEY,
	custid BIGINT UNSIGNED NOT NULL,
	planid BIGINT UNSIGNED NOT NULL,
	startdate timestamp not null,
	enddate timestamp,
	expirydate timestamp,
	status char(1),
	FOREIGN KEY(custid) REFERENCES tblcustomers(custid),
	FOREIGN KEY(planid) REFERENCES TBLMPOSTPAIDPLAN(postpaidplanid)
);

alter table tblMCITY add countryid bigint unsigned not null references tblmcountry(countryid);


alter table tblcustomers add outstandingbalance NUMERIC(20,4) default 0;

CREATE TABLE TBLMSERVICES
(
	serviceid SERIAL PRIMARY KEY,
	servicename varchar(255),
	CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

alter table TBLMPOSTPAIDPLAN add serviceid bigint UNSIGNED REFERENCES TBLMSERVICES(serviceid);

create table TBLPARTNERS
(
	PARTNERID SERIAL PRIMARY KEY,
	PARTNERNAME VARCHAR(255),
	STATUS CHAR(1),
	CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE TBLPARTNERS AUTO_INCREMENT=2;

alter table tblcustomers add partnerid BIGINT UNSIGNED not null references tblpartners(partnerid); 
update tblcustomers set partnerid=1 where custid > 0;

alter table tblstaffuser add partnerid BIGINT UNSIGNED not null references tblpartners(partnerid); 
update tblstaffuser set partnerid=1 where staffid > 0;

drop table TBLMPOSTPAIDPLANCHARGEREL;

create table TBLCHARGES
(
	CHARGEID serial,
	CHARGENAME varchar(255),
	DESCRIPTION VARCHAR(255),
	CHARGETYPE VARCHAR(32),
	PRICE NUMERIC(16,4),
	TAXID BIGINT UNSIGNED, 
	DISCOUNTID BIGINT UNSIGNED, 
	CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (TAXID) REFERENCES TBLMTAX(TAXID),
    FOREIGN KEY (DISCOUNTID) REFERENCES TBLMDISCOUNT(DISCOUNTID)
);

CREATE TABLE TBLMPOSTPAIDPLANCHARGEREL
  (
	POSTPAIDPLANCHARGERELID serial,
	CHARGEID BIGINT UNSIGNED NOT NULL,
	BILLINGCYCLE NUMERIC(2,0),
	CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	POSTPAIDPLANID bigint UNSIGNED,
	PRIMARY KEY (POSTPAIDPLANCHARGERELID),
	FOREIGN KEY (POSTPAIDPLANID) REFERENCES TBLMPOSTPAIDPLAN (POSTPAIDPLANID),
	FOREIGN KEY (CHARGEID) REFERENCES TBLCHARGES(CHARGEID)
  );
  
 alter table TBLMPOSTPAIDPLAN add plantype varchar(20) NOT NULL;


  CREATE TABLE tblserverdetail(
   serverid SERIAL PRIMARY KEY,
   serverip VARCHAR (100) NOT NULL,
   webport VARCHAR (50) NOT NULL,
   status VARCHAR (1) NOT NULL,
   created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP
 );
 


alter table tblpartners add COMM_TYPE VARCHAR(100);
alter table tblpartners add COMM_REL_VALUE NUMERIC(20,4);
alter table tblpartners add COMM_DUE_DAY  NUMERIC(2);
alter table tblpartners add NEXTBILLDATE timestamp NULL DEFAULT CURRENT_TIMESTAMP;
alter table tblpartners add LASTBILLDATE timestamp NULL DEFAULT CURRENT_TIMESTAMP;
alter table tblpartners add taxid BIGINT UNSIGNED references tblmtax (taxid);
alter table tblpartners add addresstype varchar(25);
alter table tblpartners add address1 varchar(255);
alter table tblpartners add address2 varchar(255);
alter table tblpartners add city varchar(30);
alter table tblpartners add state varchar(30);
alter table tblpartners add country varchar(30);
alter table tblpartners add pincode varchar(30);
alter table tblpartners add mobile varchar(30);
alter table tblpartners add email varchar(150);
ALTER TABLE tblpartners
    ADD is_delete BOOLEAN NOT NULL default FALSE;



create table TBLPARTNERCOMMREL
(
	PARNTERCOMMRELID SERIAL PRIMARY KEY,
	CUSTOMERID BIGINT UNSIGNED,
	PARTNERID BIGINT UNSIGNED,
	COMM_TYPE VARCHAR(100),	
	COMM_REL_VALUE NUMERIC(2),
	COMM_VALUE NUMERIC(20,4),
	CREATEDATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	BILLDATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PROCESS_STATUS VARCHAR(50),
	FOREIGN KEY (PARTNERID) REFERENCES TBLPARTNERS(PARTNERID),
	FOREIGN KEY (CUSTOMERID) REFERENCES TBLCUSTOMERS(CUSTID)
);

CREATE TABLE tblmpartnerbillrun (
  partnerbillrunid SERIAL primary key,
  billruncreatedate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  billrundate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  billruncount decimal(10,0) DEFAULT NULL,
  amount decimal(20,4) DEFAULT NULL,
  status varchar(15) DEFAULT NULL,
  billruncompletedate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  SUCCESSCOUNT decimal(20,0) DEFAULT NULL,
  failcount decimal(20,0) DEFAULT NULL
);

CREATE TABLE tblpartnercreditdoc (
  creditdocumentid serial primary key,
  creditdocumentnumber varchar(200),
  partnerid bigint unsigned DEFAULT NULL,
  billdate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  createdate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  startdate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  enddate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  duedate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  latepaymentdate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  subtotal decimal(20,4) DEFAULT '0.0000',
  tax decimal(20,4) DEFAULT '0.0000',
  discount decimal(20,4) DEFAULT '0.0000',
  totalamount decimal(20,4) DEFAULT '0.0000',
  previousbalance decimal(20,4) DEFAULT '0.0000',
  latepaymentfee decimal(20,4) DEFAULT '0.0000',
  currentpayment decimal(20,4) DEFAULT '0.0000',
  currentdebit decimal(20,4) DEFAULT '0.0000',
  currentcredit decimal(20,4) DEFAULT '0.0000',
  totaldue decimal(20,4) DEFAULT '0.0000',
  totalamountinwords varchar(200) DEFAULT NULL,
  totaldueinwords varchar(200) DEFAULT NULL,
  partnerbillrunid bigint unsigned DEFAULT NULL,
  billrunstatus varchar(200) DEFAULT NULL,
  xmldocument longtext ,
  email varchar(200) DEFAULT NULL,
  phone varchar(200) DEFAULT NULL,
  FOREIGN KEY(PARTNERID) REFERENCES TBLPARTNERS(PARTNERID),
  FOREIGN KEY(PARTNERBILLRUNID) REFERENCES tblmpartnerbillrun(PARTNERBILLRUNID)
);

CREATE TABLE tbltpartnercreditdocdtls (
  creditdocdetailid SERIAL PRIMARY KEY,
  creditdocumentid bigint unsigned NOT NULL,
	CUSTOMERID BIGINT UNSIGNED,
	PARTNERID BIGINT UNSIGNED,
	COMM_TYPE VARCHAR(100),	
	COMM_REL_VALUE NUMERIC(20,4),
	COMM_VALUE NUMERIC(20,4), 
  tax decimal(20,4) DEFAULT '0.0000',
  totalamount decimal(20,4) DEFAULT '0.0000',
  startdate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  enddate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  prorationtype varchar(100) DEFAULT NULL,
  noofcycle decimal(20,4) DEFAULT '0.0000',
  FOREIGN KEY (CUSTOMERID) REFERENCES TBLCUSTOMERS(CUSTID),
  FOREIGN KEY(PARTNERID) REFERENCES TBLPARTNERS(PARTNERID),
  FOREIGN KEY(creditdocumentid) REFERENCES tblpartnercreditdoc(creditdocumentid)
);

CREATE TABLE tbltpartnercreditdoctaxrel (
  creditdoctaxid SERIAL PRIMARY KEY,
  creditdocumentid bigint unsigned NOT NULL,
  taxid bigint unsigned NOT NULL,
  taxname varchar(255) DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  percentage decimal(20,4) DEFAULT '0.0000',
  taxlevel decimal(20,4) DEFAULT '0.0000',
  startdate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  enddate timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  amount decimal(20,4) DEFAULT '0.0000',
  FOREIGN KEY(CREDITDOCUMENTID) REFERENCES tbltpartnercreditdocdtls(CREDITDOCUMENTID),
  FOREIGN KEY(TAXID) REFERENCES TBLMTAX(TAXID)
  );
  
  CREATE TABLE tbltpartnercreditdocuaddressrel (
  creditdocaddrid SERIAL PRIMARY KEY,
  creditdocumentid bigint unsigned NOT NULL,
  addresstype varchar(25) DEFAULT NULL,
  address1 varchar(255) DEFAULT NULL,
  address2 varchar(255) DEFAULT NULL,
  city varchar(30) DEFAULT NULL,
  state varchar(30) DEFAULT NULL,
  country varchar(30) DEFAULT NULL,
  pincode varchar(30) DEFAULT NULL,
  FOREIGN KEY(creditdocumentid) REFERENCES tblpartnercreditdoc(creditdocumentid)
 );
 
  
alter table tblpartners modify country numeric(2);
alter table tblpartners modify state numeric(2);
alter table tblpartners modify city numeric(2);

ALTER TABLE tblroles AUTO_INCREMENT=6;


alter table tblpartnercommrel modify comm_rel_value numeric(20,4);



CREATE TABLE TBLTCREDITDOC
(
	CREDITDOCID serial primary key,
	CUSTID bigint unsigned not null,
	PAYMENTDATE TIMESTAMP,
	PAYMODE varchar(50),
	PAYDETAILS1 varchar(200),
	PAYDETAILS2 varchar(200),
	PAYDETAILS3 varchar(200),
	PAYDETAILS4 varchar(200),
	AMOUNT numeric (20,4),
	STATUS 	varchar(50), 
	APPROVEDBYSTAFFID NUMERIC(20),
	REMARKS VARCHAR(150),	
	CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	CREATEDBYSTAFFID NUMERIC(20),
	LASTMODIFIEDBYSTAFFID NUMERIC(20),
	LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (CUSTID) REFERENCES tblcustomers(custid)
);


CREATE TABLE TBLMCUSTLEDGER
(
	CUSTLEDGERID SERIAL PRIMARY KEY,
	TOTALDUE NUMERIC(20,4),
	TOTALPAID NUMERIC(20,4),
	CUSTID BIGINT UNSIGNED,
	CREATEDATE	TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
   	LASTMODIFIEDDATE	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  	FOREIGN KEY (CUSTID) REFERENCES TBLCUSTOMERS(CUSTID)
);

CREATE UNIQUE INDEX idx_ledger_cust_uni ON TBLMCUSTLEDGER(CUSTID);

CREATE TABLE TBLTCUSTLEDGERDETAILS
(
	CUSTLEDGERDTLSID SERIAL PRIMARY KEY,
	CREATEDATE	TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	TRANSTYPE VARCHAR(10), 
	TRANSCATEGORY VARCHAR(10),
	AMOUNT NUMERIC(20,4),
	CUSTID BIGINT UNSIGNED,
	CREDITDOCID bigint unsigned,
	DEBITDOCID bigint unsigned,
	DESCRIPTION VARCHAR(300),
    FOREIGN KEY(CREDITDOCID) REFERENCES TBLTCREDITDOC(CREDITDOCID),
	FOREIGN KEY(DEBITDOCID) REFERENCES tbltdebitdocument(DEBITDOCUMENTID),
  	FOREIGN KEY (CUSTID) REFERENCES TBLCUSTOMERS(CUSTID)
);

  CREATE TABLE TBLTRIALNEXTBILLDATE 
   (	 SUBSCRIBERID  serial, 
	 TRIALNEXTBILLDATE  TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
	 LASTBILLDATE  TIMESTAMP NULL DEFAULT NULL, 
	 BILLDAY  NUMERIC(20),
	FOREIGN KEY (SUBSCRIBERID) REFERENCES tblcustomers (custid)
   ) ;


create table TBLMTRIALBILLRUN
(
	trialbillrunid serial,
	billruncreatedate TIMESTAMP  DEFAULT CURRENT_TIMESTAMP, 
	billrundate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	billruncount NUMERIC(10), 
	amount NUMERIC(20,4),
	status varchar(15),
	billruncompletedate TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (trialbillrunid)
);

 alter table TBLMTRIALBILLRUN add SUCCESSCOUNT  NUMERIC(20);
 alter table TBLMTRIALBILLRUN add failcount  NUMERIC(20);



create table TBLTTRIALDEBITDOCUMENT
(
	trialdebitdocumentid serial,
	trialdebitdocumentnumber varchar(200),
	subscriberid BIGINT UNSIGNED,
	billdate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP, 
	createdate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	startdate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	enddate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	duedate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	latepaymentdate TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
	subtotal NUMERIC(20,4) default 0,
	tax NUMERIC(20,4) default 0,
	discount NUMERIC(20,4) default 0,
	totalamount NUMERIC(20,4) default 0,
	previousbalance NUMERIC(20,4) default 0,
	latepaymentfee NUMERIC(20,4) default 0,
	currentpayment NUMERIC(20,4) default 0,
	currentdebit NUMERIC(20,4) default 0,
	currentcredit NUMERIC(20,4) default 0,
	totaldue NUMERIC(20,4) default 0,
	totalamountinwords varchar(200),
	totaldueinwords varchar(200),
	trialbillrunid BIGINT UNSIGNED,
	billrunstatus varchar(200),
	xmldocument LONGTEXT,
	PRIMARY KEY (trialdebitdocumentid ),
	FOREIGN KEY (subscriberid) REFERENCES tblcustomers (custid),
	FOREIGN KEY (trialbillrunid) REFERENCES TBLMTRIALBILLRUN (trialbillrunid)
);

alter table TBLTTRIALDEBITDOCUMENT add email varchar(200);

alter table TBLTTRIALDEBITDOCUMENT add phone varchar(200);

create table TBLTTRIALDEBITDOCUMENTDETAIL
(
	trialdebitdocdetailid serial primary key,
	trialdebitdocumentid BIGINT UNSIGNED NOT NULL,
	chargeid numeric(20) NOT NULL,
	chargename varchar(400),
	description varchar(400),
	chargetype varchar(200),
	chargecycle varchar(200),
	subtotal NUMERIC(20,4) default 0,
	tax NUMERIC(20,4) default 0,
	discount NUMERIC(20,4) default 0,
	totalamount NUMERIC(20,4) default 0,
	startdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	enddate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	prorationtype varchar(100),
	noofcycle NUMERIC(20,4) default 0,
	FOREIGN KEY (trialdebitdocumentid) REFERENCES TBLTTRIALDEBITDOCUMENT (trialdebitdocumentid)
);



create table TBLTTRIALDEBITDOCUMENTTAXREL
(
	trialdebitdoctaxid serial primary key,
	trialdebitdocumentid BIGINT UNSIGNED NOT NULL,
	taxid BIGINT UNSIGNED NOT NULL,
	taxname varchar(255),
	description varchar(255),
	percentage NUMERIC(20,4) default 0,
	taxlevel NUMERIC(20,4) default 0,
	startdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	enddate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	amount NUMERIC(20,4) default 0,
	FOREIGN KEY (trialdebitdocumentid) REFERENCES TBLTTRIALDEBITDOCUMENT (trialdebitdocumentid),
	FOREIGN KEY (taxid) REFERENCES tblmtax (taxid)
);



create table TBLTTRIALDEBITDOCUMENTADDRESSREL
(
	trialdebitdocaddrid serial primary key,
	trialdebitdocumentid BIGINT UNSIGNED NOT NULL,
	addresstype varchar(25),
	address1 varchar(255),
	address2 varchar(255),
	city varchar(30),
	state varchar(30),
	country varchar(30),
	pincode varchar(30),
	FOREIGN KEY (trialdebitdocumentid) REFERENCES TBLTTRIALDEBITDOCUMENT (trialdebitdocumentid)
);

alter table tblcustomers add ASNNumber varchar(500);
alter table tblcustomers add BNGRouterInterface varchar(500);
alter table tblcustomers add BNGRouterName varchar(500);
alter table tblcustomers add IPPrefixes varchar(500);
alter table tblcustomers add IPV6Prefixes varchar(500);
alter table tblcustomers add LANIP varchar(500);
alter table tblcustomers add LANIPV6 varchar(500);
alter table tblcustomers add LLAccountID varchar(500);
alter table tblcustomers add LLConnectionType varchar(500);  
alter table tblcustomers add LLExpiryDate varchar(500);
alter table tblcustomers add LLMedium varchar(500);
alter table tblcustomers add LLServiceID varchar(500);
alter table tblcustomers add MACADDRESS varchar(500);
alter table tblcustomers add PeerIP varchar(500);
alter table tblcustomers add POOLIP varchar(500);
alter table tblcustomers add QOS varchar(500);
alter table tblcustomers add RDExport varchar(500);
alter table tblcustomers add RDValue varchar(500);
alter table tblcustomers add VLANID varchar(500);
alter table tblcustomers add VRFName varchar(500);
alter table tblcustomers add VSIID varchar(500);
alter table tblcustomers add VSIName varchar(500);
alter table tblcustomers add WANIP varchar(500);
alter table tblcustomers add WANIPV6 varchar(500);


alter table TBLTCREDITDOC add referenceno varchar(100); 

CREATE TABLE tbltemplatemanagement(
    templateid SERIAL PRIMARY KEY,
    templatename varchar(100),
    templatetype varchar(100),
    jrxmlfile LONGTEXT,
	status varchar(1),
    created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP
);

alter table TBLTCREDITDOC add 	xmldocument LONGTEXT; 


alter table TBLTDEBITDOCUMENT add firstbill varchar(1);

create table tblcustquotadtls
(
	quotadtlsid serial primary key,
	custid BIGINT UNSIGNED NOT NULL,
	planid BIGINT UNSIGNED NOT NULL,
	quotatype varchar(50),
	totalquota numeric(20,4),
	usedquota numeric(20,4),
    created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP,
	foreign key (custid) references tblcustomers(custid),
	foreign key (planid) references TBLMPOSTPAIDPLAN(POSTPAIDPLANID)
);

#alter table tblcharges add actual_price numeric(20,4);

create table tbldunningrules
(
	druleid serial primary key,
	name varchar(100),
	fromemail varchar(200),
	bccemail varchar(200),
	comm_email varchar(2),
	comm_sms varchar(2),
	internal_pay_email varchar(200),
	esc_staff_email varchar(200),
	coll_agency_email varchar(200),
	creditclass varchar(50),
	rulestatus varchar(1),
	CREATEDATE	TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	CREATEDBYSTAFFID	NUMERIC(20),
	LASTMODIFIEDBYSTAFFID	NUMERIC(20),
	LASTMODIFIEDDATE	TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table tbldunruleaction
(
	actionid serial primary key,
	druleid BIGINT UNSIGNED NOT NULL,
	days numeric(4),
	emailsub varchar(200),
	daction varchar(50),
	foreign key (druleid) references tbldunningrules(druleid)
);

CREATE TABLE TBLPLANQOSDTLS
(
	planqosid serial primary key,
	planid BIGINT UNSIGNED NOT NULL,
	uploadqos numeric(20),
 	downloadqos numeric(20),
	CREATEDATE	TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	CREATEDBYSTAFFID	NUMERIC(20),
	LASTMODIFIEDBYSTAFFID	NUMERIC(20),
	LASTMODIFIEDDATE	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	foreign key (planid) references TBLMPOSTPAIDPLAN(POSTPAIDPLANID)
);

#09-Feb-2021
alter table tblcharges add actual_price numeric(20,4);
update tblcharges set actual_price=price where chargeid > 0;
alter table tblmpostpaidplan add dbr numeric(20,4) default 0.0;

alter table tblcustomers add billentityname varchar(200);
alter table tblcustomers add purchaseorder varchar(200);
alter table tblcustomers add remarks varchar(200);

alter table tblcustomers add addparam1 varchar(500);
alter table tblcustomers add addparam2 varchar(500);
alter table tblcustomers add addparam3 varchar(500);
alter table tblcustomers add addparam4 varchar(500);


CREATE TABLE tbllocation(
    locationid SERIAL PRIMARY KEY,
    name varchar(100),
   	status VARCHAR (1) NOT NULL,  
    created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tblpartnerlocationrel
(
	partnerlocid SERIAL PRIMARY KEY,
	locationid BIGINT UNSIGNED NOT NULL,
	partnerid BIGINT UNSIGNED NOT NULL,
	created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY(locationid) REFERENCES tbllocation(locationid),
	FOREIGN KEY(partnerid) REFERENCES tblpartners(partnerid)
);

alter table TBLPARTNERS add column parentpartnerid integer references TBLPARTNERS(PARTNERID);


#04-March-2021
alter table tblcustomers add column parentcustid integer references tblcustomers(custid);
alter table tblcustomers add invoiceoption varchar(1);
#05-March-2021
alter table TBLCHARGES add dbr numeric(20,4) default 0.0;


 alter table tblstaffuser add oldpassword1 VARCHAR(100);
 alter table tblstaffuser add oldpassword2 VARCHAR(100);
 alter table tblstaffuser add oldpassword3 VARCHAR(100);

 alter table tblcustomers add oldpassword1 VARCHAR(100);
 alter table tblcustomers add oldpassword2 VARCHAR(100);
 alter table tblcustomers add oldpassword3 VARCHAR(100);
 
 #18-march-2021
 alter table TBLMPOSTPAIDPLAN add column plangroup VARCHAR(100);
 alter table TBLMPOSTPAIDPLAN add column validity NUMERIC(4) default 0;
 alter table tblcustomers add column firstactivationdate timestamp DEFAULT CURRENT_TIMESTAMP; 
 
 #20-march-2021
alter table TBLMPOSTPAIDPLAN add column UPLOADTS VARCHAR(128);
alter table TBLMPOSTPAIDPLAN add column DOWNLOADTS VARCHAR(128);
alter table TBLMPOSTPAIDPLAN add column allowoverusage BOOLEAN DEFAULT FALSE;
alter table tblcustomers add column  allowedipaddrs VARCHAR(100);
 
create table tblcustmacmapping
(
   custmacmapid serial primary key,
   custid BIGINT UNSIGNED NOT NULL,
   macaddress varchar(100),
   created_on timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
   lastmodified_on timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP,
   foreign key (custid) references tblcustomers(custid)
);


-- Nilesh / 28-04-2021
create table tblcommonlist(
    list_item_id SERIAL PRIMARY KEY,
    list_text VARCHAR (100) NOT NULL,
    list_value VARCHAR (100) NOT NULL,
    list_type VARCHAR (100) NOT NULL,
    status VARCHAR (15) NOT NULL
);


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

ALTER TABLE tblippool
    ADD is_static_ip_pool BOOLEAN NOT NULL default FALSE;

#Nilesh 10-06-2021
alter table tblippool
    add status varchar(50) not null;

#Mohit 10-06-2021

ALTER TABLE tblroles AUTO_INCREMENT = 51;

ALTER TABLE tblmpostpaidplan MODIFY COLUMN STATUS char(100) DEFAULT 'ACTIVE' NOT NULL;

alter table tblcustomers add onuid varchar(100);

ALTER TABLE TBLCUSTDOCDETAILS ADD cust_id bigint UNSIGNED;
ALTER TABLE TBLCUSTDOCDETAILS add FOREIGN KEY (cust_id) REFERENCES tblcustomers (custid);

# Nilesh 11-06-2021
alter table tblippool modify status varchar(50) ;


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

# Nilesh 16-06-2021

alter table TBLMSUBSCRIBERADDRESSREL add landmark varchar(100);
alter table TBLMSUBSCRIBERADDRESSREL add area varchar(100);

# Nilesh 18-06-2021
alter table tblcustmacmapping add is_deleted BOOLEAN DEFAULT false;

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


ALTER TABLE tblcases MODIFY COLUMN case_title varchar(100)  NULL;
ALTER TABLE tblcases MODIFY COLUMN case_number varchar(100)  NULL;
ALTER TABLE tblcases MODIFY COLUMN case_for_partner varchar(100)  NULL;
ALTER TABLE tblcases MODIFY COLUMN case_for_zone varchar(100)  NULL;
ALTER TABLE tblcases MODIFY COLUMN case_started_on datetime NULL;
ALTER TABLE tblcases MODIFY COLUMN first_assigned_on datetime NULL;
ALTER TABLE tblcases MODIFY COLUMN is_delete tinyint(1) DEFAULT 0 NOT NULL;

#Mohit 22-06-2021 //New
ALTER TABLE tblcustomers ADD COLUMN is_deleted tinyint(1) DEFAULT 0 NOT NULL;
ALTER TABLE tblcases ADD COLUMN first_remark text;
ALTER TABLE tblcaseupdates ADD COLUMN comment_by varchar(50);


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

#Mohit 06-06-2021
ALTER TABLE tblcustdocdetails DROP COLUMN attachment_path;
alter table tblcustdocdetails add column filename varchar(200);
alter table tblcustdocdetails add column uniquename varchar(200);

#Utsav 07-07-2021

ALTER TABLE tblmpostpaidplan ADD quotadid DECIMAL(50) NULL;
ALTER TABLE tblmpostpaidplan ADD quotaintercom DECIMAL(50) NULL;

ALTER TABLE tblcustquotadtls ADD didtotalquota DECIMAL(50) NULL;
ALTER TABLE tblcustquotadtls ADD didusedquota DECIMAL(50) NULL;
ALTER TABLE tblcustquotadtls ADD intercomtotalquota DECIMAL(50) NULL;
ALTER TABLE tblcustquotadtls ADD intercomusedquota DECIMAL(50) NULL;

#Utsav 06-07-2021

#Jaymin 10-07-2021
alter table tblpartners add column balance BIGINT(20);

#Jaymin 13-07-2021
alter table tblpartners add column pricebookid bigint(20) unsigned;
alter table tblpartners add foreign key (pricebookid) references tblpricebook(bookid);

#Utsav 15-07-2021


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

#Already Fired
ALTER TABLE tbl_order_details
    MODIFY COLUMN partner_id bigint NULL;
ALTER TABLE tbl_order_details
    MODIFY COLUMN cust_id bigint NULL;


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
alter table tblstaffuser add column sysstaff Boolean not null default false;

ALTER TABLE tblstaffuser AUTO_INCREMENT = 51;

update tblstaffuser
set sysstaff = TRUE
where staffid < 51;

ALTER TABLE tblstaffuser MODIFY COLUMN last_login_time timestamp NULL;

#Utsav 19-08-2021
ALTER TABLE tblipallocationdtls ADD pool_details_id BIGINT UNSIGNED NULL;
ALTER TABLE tblipallocationdtls ADD CONSTRAINT tblipallocationdtls_FK FOREIGN KEY (pool_details_id) REFERENCES tblippooldtls(pool_details_id);
ALTER TABLE tblcustchargedtls MODIFY COLUMN charge_date DATETIME NULL;
ALTER TABLE tblcustchargedtls MODIFY COLUMN startdate DATETIME NULL;
ALTER TABLE tblcustchargedtls MODIFY COLUMN enddate DATETIME NULL;

ALTER TABLE tblcases MODIFY COLUMN next_followup_date date NULL;
ALTER TABLE tblcases MODIFY COLUMN next_followup_time time NULL;

#Utsav 20-08-2021
ALTER TABLE tblcustpackagerel ADD offer_price DOUBLE NULL;
ALTER TABLE tblcustpackagerel ADD tax_amount DOUBLE NULL;

ALTER TABLE tblcustchargedtls ADD ippooldtlsid BIGINT UNSIGNED NULL;
ALTER TABLE tblcustchargedtls ADD CONSTRAINT tblcustchargedtls_FK FOREIGN KEY (ippooldtlsid) REFERENCES tblippooldtls(pool_details_id);


create table tblauditlog
(
    audit_id          SERIAL PRIMARY KEY,
    auditdate		  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	user_name		  VARCHAR(255),
	user_id		  	  bigint,
	employee_name	  VARCHAR(255),
	employee_id		  bigint,
	module			  VARCHAR(255),
	operation		  VARCHAR(255),
	ip_address		  VARCHAR(255),
	remark			  LONGTEXT,
	entity_ref_id	  BIGINT,
	partner_id	  BIGINT
);

# Add Classes
alter table tblaclclass
    add column operallid bigint;


INSERT INTO tblaclclass (classid, classname, dispname, disporder, operallid)
VALUES (1, 'com.adopt.apigw.model.postpaid.City', 'City', 1, 1);
INSERT INTO tblaclclass (classid, classname, dispname, disporder, operallid)
VALUES (2, 'com.adopt.apigw.model.postpaid.State', 'State', 2, 6);
INSERT INTO tblaclclass (classid, classname, dispname, disporder, operallid)
VALUES (3, 'com.adopt.apigw.model.postpaid.Country', 'Country', 3, 11);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (4, "com.adopt.apigw.model.postpaid.Partner","Partner",4,16);
INSERT INTO tblaclclass (classid, classname, dispname, disporder, operallid)
VALUES (5, 'com.adopt.apigw.model.common.Customers', 'Subscribers', 5, 21);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (6, "com.adopt.apigw.model.radius.Clients","Clients",6,59);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (7, "com.adopt.apigw.model.radius.ClientGroup","Client Group",7,64);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (8, "com.adopt.apigw.model.radius.AccountingProfile","Accounting Profile",8,69);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (9, "com.adopt.apigw.model.radius.RadiusProfile","Policies",9,74);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (10, "com.adopt.apigw.model.radius.AuthResponse","Audit",10,79);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (11, "com.adopt.apigw.model.radius.DbcdrProcessing","Subscriber CDRs",11,84);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (12, "com.adopt.apigw.model.radius.LiveUser","Connected Subscriber",12,89);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (13, "com.adopt.apigw.model.postpaid.PostpaidPlan","Packages",13,94);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (14, "com.adopt.apigw.model.postpaid.Tax","Tax",14,99);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (15, "com.adopt.apigw.model.postpaid.Charge","Charge",15,104);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (16, "com.adopt.apigw.model.postpaid.Discount","Discount",16,109);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (17, "com.adopt.apigw.model.postpaid.PlanService","Service",17,114);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (18, "com.adopt.apigw.modules.qosPolicy.domain.QOSPolicy","QOS Policy",18,119);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (19, "com.adopt.apigw.modules.PriceGroup.domain.PriceBook","Price Book",19,124);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (20, "com.adopt.apigw.modules.ServiceArea.domain.ServiceArea","Service Area",20,129);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (21, "com.adopt.apigw.modules.NetworkDevices.domain.NetworkDevices","Network Devices",21,134);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (22, "com.adopt.apigw.modules.ippool.domain.IPPool","IP Pool",22,139);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (23, "com.adopt.apigw.modules.tickets.domain.Case","Case",23,144);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (24, "com.adopt.apigw.modules.tickets.domain.CaseReason","Case Reason",24,149);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (25, "com.adopt.apigw.model.common.StaffUser","Staff",25,154);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (26, "com.adopt.apigw.modules.Broadcast.domain.Broadcast","Broadcast",26,159);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (27, "com.adopt.apigw.model.postpaid.XsltManagement","XsltManagement",27,164);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (28, "com.adopt.apigw.modules.role.domain.Role","ACL",28,169);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (29, "com.adopt.apigw.modules.Teams.domain.Teams","Teams",29,187);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (30, "com.adopt.apigw.modules.Notification.domain.Notification","Notification",30,192);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (31, "com.adopt.apigw.modules.NetworkDevices.domain.NetworkDevices","Bulk Upload Network Devices",31, 197);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (32, "com.adopt.apigw.modules.tickets.domain.Case","Bulk Upload Case",32, 198);
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (33, "com.adopt.apigw.model.common.StaffUser","Manage Profile",33, 199);


# Add Menus
drop table if exists tblaclmenus;
create table tblaclmenus(
    menuid bigint PRIMARY KEY,
    name varchar(100) not null,
    dispname varchar(100) not null,
    classid bigint,
    parentid bigint
);

insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (1,"Masters","Masters",NULL,NULL);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (2,"Subscriber","Subscriber",5,1);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (3,"Franchise","Franchise",4,1);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (4,"Country","Country",3,1);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (5,"State","State",2,1);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (6,"City","City",1,1);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (7,"Radius","Radius",NULL,NULL);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (8,"Clients","Clients",6,7);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (9,"Client Group","Client Group",7,7);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (10,"Subscriber CDRs","Subscriber CDRs",11,7);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (11,"Connected Subscriber","Connected Subscriber",12,7);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (12,"Accounting Profile","Accounting Profile",8,7);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (13,"Policies","Policies",9,7);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (14,"Audit","Audit",10,7);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (15,"Packages","Packages",NULL,NULL);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (16,"Packages","Packages",13,15);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (17,"Tax","Tax",14,15);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (18,"Charge","Charge",15,15);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (19,"Discount","Discount",16,15);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (20,"Service","Service",17,15);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (21,"QOS Policy","QOS Policy",18,15);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (22,"Price Block","Price Block",19,15);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (23,"Networks","Networks",NULL,NULL);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (24,"Service Area","Service Area",20,23);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (25,"Network Devices","Network Devices",21,23);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (26,"IP Pool","IP Pool",22,23);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (27,"Customer Care","Customer Care",NULL,NULL);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (28,"Cases","Cases",23,27);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (29,"Case Reason","Case Reason",24,27);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (30,"Operations","Operations",NULL,NULL);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (34,"Broadcast","Broadcast",26,30);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (40,"Notifications","Notifications",30,30);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (35,"Reports","Reports",NULL,NULL);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (36,"Partner Reports","Partner Reports",28,35);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (37,"Revenue Reports","Revenue Reports",29,35);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (38,"Subscriber Report","Subscriber Report",30,35);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (39,"System Report","System Report",31,35);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (31,"HR","HR",NULL,NULL);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (32,"ACL","ACL",28,31);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (33,"Staff","Staff",25,31);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (41,"Teams","Teams",29,31);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (42,"Bulk Operations","Bulk Operations",NULL,NULL);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (43,"Network Devices","Network Devices",NULL,42);
insert into tblaclmenus (menuid, name, dispname, classid, parentid) values (44,"Case","Case",NULL,42);



# Add Operations
drop table if exists tblacloperations;
create table tblacloperations
(
    opid    SERIAL PRIMARY KEY,
    opname  varchar(100) not null,
    classid bigint       not null,
    foreign key (classid) references tblaclclass (classid)
);


# City Operations
insert into tblacloperations (opid,classid,opname) values (1, 1, "City All");
insert into tblacloperations (opid,classid,opname) values (2, 1, "City View");
insert into tblacloperations (opid,classid,opname) values (3, 1, "City Add");
insert into tblacloperations (opid,classid,opname) values (4, 1, "City Edit");
insert into tblacloperations (opid,classid,opname) values (5, 1, "City Delete");


# State Operations
insert into tblacloperations (opid,classid,opname) values (6, 2, "State All");
insert into tblacloperations (opid,classid,opname) values (7, 2, "State View");
insert into tblacloperations (opid,classid,opname) values (8, 2, "State Add");
insert into tblacloperations (opid,classid,opname) values (9, 2, "State Edit");
insert into tblacloperations (opid,classid,opname) values (10, 2, "State Delete");

# Country Operations

insert into tblacloperations (opid,classid,opname) values (11, 3, "Country All");
insert into tblacloperations (opid,classid,opname) values (12, 3, "Country View");
insert into tblacloperations (opid,classid,opname) values (13, 3, "Country Add");
insert into tblacloperations (opid,classid,opname) values (14, 3, "Country Edit");
insert into tblacloperations (opid,classid,opname) values (15, 3, "Country Delete");

# Partner Operations

insert into tblacloperations (opid,classid,opname) values (16, 4, "Partner All");
insert into tblacloperations (opid,classid,opname) values (17, 4, "Partner View");
insert into tblacloperations (opid,classid,opname) values (18, 4, "Partner Add");
insert into tblacloperations (opid,classid,opname) values (19, 4, "Partner Edit");
insert into tblacloperations (opid,classid,opname) values (20, 4, "Partner Delete");

# Customer Operations
insert into tblacloperations (opid,classid,opname) values (21, 5, "Customer All");
insert into tblacloperations (opid,classid,opname) values (22, 5, "Customer View");
insert into tblacloperations (opid,classid,opname) values (23, 5, "Customer Add");
insert into tblacloperations (opid,classid,opname) values (24, 5, "Customer Edit");
insert into tblacloperations (opid,classid,opname) values (25, 5, "Customer Delete");

insert into tblacloperations (opid,classid,opname) values (26, 5, "Customer View QOS");
insert into tblacloperations (opid,classid,opname) values (27, 5, "Customer Update QOS");
insert into tblacloperations (opid,classid,opname) values (28, 5, "Customer View Quota");
insert into tblacloperations (opid,classid,opname) values (29, 5, "Customer Update Quota");
insert into tblacloperations (opid,classid,opname) values (30, 5, "Customer View Expiry");
insert into tblacloperations (opid,classid,opname) values (31, 5, "Customer Update Expiry");
insert into tblacloperations (opid,classid,opname) values (32, 5, "Customer View Status");
insert into tblacloperations (opid,classid,opname) values (33, 5, "Customer Update Status");
insert into tblacloperations (opid,classid,opname) values (34, 5, "Customer View Voice Details");
insert into tblacloperations (opid,classid,opname) values (35, 5, "Customer Update Voice Details");
insert into tblacloperations (opid,classid,opname) values (36, 5, "Customer Apply Charge");
insert into tblacloperations (opid,classid,opname) values (37, 5, "Customer Rollback Charge");
insert into tblacloperations (opid,classid,opname) values (38, 5, "Customer Purchase IP");
insert into tblacloperations (opid,classid,opname) values (39, 5, "Customer Allocate IP");
insert into tblacloperations (opid,classid,opname) values (40, 5, "Customer Renew IP");
insert into tblacloperations (opid,classid,opname) values (41, 5, "Customer Release IP");
insert into tblacloperations (opid,classid,opname) values (42, 5, "Customer Manage IP Purchase");
insert into tblacloperations (opid,classid,opname) values (43, 5, "Customer Record Payment");
insert into tblacloperations (opid,classid,opname) values (44, 5, "Customer Rollback Payment");
insert into tblacloperations (opid,classid,opname) values (45, 5, "Customer Update Payment");
insert into tblacloperations (opid,classid,opname) values (46, 5, "Customer Adjustments");
insert into tblacloperations (opid,classid,opname) values (47, 5, "Customer Update Contact Details");
insert into tblacloperations (opid,classid,opname) values (48, 5, "Customer Update Basic Details");
insert into tblacloperations (opid,classid,opname) values (49, 5, "Customer Update Network Details");
insert into tblacloperations (opid,classid,opname) values (50, 5, "Customer Update Address Details");
insert into tblacloperations (opid,classid,opname) values (51, 5, "Customer Update Other Details");
insert into tblacloperations (opid,classid,opname) values (52, 5, "Customer Change Selfcare Password");
insert into tblacloperations (opid,classid,opname) values (53, 5, "Customer Reset Selfcare Password");
insert into tblacloperations (opid,classid,opname) values (54, 5, "Customer Change CPE Password");
insert into tblacloperations (opid,classid,opname) values (55, 5, "Customer Reset CPE Password");
insert into tblacloperations (opid,classid,opname) values (56, 5, "Customer Reset MAC");
insert into tblacloperations (opid,classid,opname) values (57, 5, "Customer Manage MAC");
insert into tblacloperations (opid,classid,opname) values (58, 5, "Customer Create Ticket");
insert into tblacloperations (opid,classid,opname) values (174, 5, "Customer View Plans");
insert into tblacloperations (opid,classid,opname) values (175, 5, "Customer Change Plans");
insert into tblacloperations (opid,classid,opname) values (176, 5, "Customer Cancel Plan");
insert into tblacloperations (opid,classid,opname) values (177, 5, "Customer Update Profile");
insert into tblacloperations (opid,classid,opname) values (178, 5, "Customer View Payment History");
insert into tblacloperations (opid,classid,opname) values (179, 5, "Customer View Purchase History");
insert into tblacloperations (opid,classid,opname) values (180, 5, "Customer Get Payment Receipt");
insert into tblacloperations (opid,classid,opname) values (181, 5, "Customer Get Purchase Invoice");
insert into tblacloperations (opid,classid,opname) values (182, 5, "Customer Get Document");
insert into tblacloperations (opid,classid,opname) values (183, 5, "Customer Replace IP");
insert into tblacloperations (opid,classid,opname) values (184, 5, "Customer Rollback IP");
insert into tblacloperations (opid,classid,opname) values (185, 5, "Customer Change IP Expiry");


#Clients
insert into tblacloperations (opid,classid,opname) values (59, 6, "Clients All");
insert into tblacloperations (opid,classid,opname) values (60, 6, "Clients View");
insert into tblacloperations (opid,classid,opname) values (61, 6, "Clients Add");
insert into tblacloperations (opid,classid,opname) values (62, 6, "Clients Edit");
insert into tblacloperations (opid,classid,opname) values (63, 6, "Clients Delete");

#ClientGroup
insert into tblacloperations (opid,classid,opname) values (64, 7, "ClientGroup All");
insert into tblacloperations (opid,classid,opname) values (65, 7, "ClientGroup View");
insert into tblacloperations (opid,classid,opname) values (66, 7, "ClientGroup Add");
insert into tblacloperations (opid,classid,opname) values (67, 7, "ClientGroup Edit");
insert into tblacloperations (opid,classid,opname) values (68, 7, "ClientGroup Delete");

#AccountingProfile
insert into tblacloperations (opid,classid,opname) values (69, 8, "AccountingProfile All");
insert into tblacloperations (opid,classid,opname) values (70, 8, "AccountingProfile View");
insert into tblacloperations (opid,classid,opname) values (71, 8, "AccountingProfile Add");
insert into tblacloperations (opid,classid,opname) values (72, 8, "AccountingProfile Edit");
insert into tblacloperations (opid,classid,opname) values (73, 8, "AccountingProfile Delete");

#RadiusProfile
insert into tblacloperations (opid,classid,opname) values (74, 9, "RadiusProfile All");
insert into tblacloperations (opid,classid,opname) values (75, 9, "RadiusProfile View");
insert into tblacloperations (opid,classid,opname) values (76, 9, "RadiusProfile Add");
insert into tblacloperations (opid,classid,opname) values (77, 9, "RadiusProfile Edit");
insert into tblacloperations (opid,classid,opname) values (78, 9, "RadiusProfile Delete");

#AuthResponse
insert into tblacloperations (opid,classid,opname) values (79, 10, "AuthResponse All");
insert into tblacloperations (opid,classid,opname) values (80, 10, "AuthResponse View");
insert into tblacloperations (opid,classid,opname) values (81, 10, "AuthResponse Add");
insert into tblacloperations (opid,classid,opname) values (82, 10, "AuthResponse Edit");
insert into tblacloperations (opid,classid,opname) values (83, 10, "AuthResponse Delete");

#DbcdrProcessing
insert into tblacloperations (opid,classid,opname) values (84, 11, "DbcdrProcessing All");
insert into tblacloperations (opid,classid,opname) values (85, 11, "DbcdrProcessing View");
insert into tblacloperations (opid,classid,opname) values (86, 11, "DbcdrProcessing Add");
insert into tblacloperations (opid,classid,opname) values (87, 11, "DbcdrProcessing Edit");
insert into tblacloperations (opid,classid,opname) values (88, 11, "DbcdrProcessing Delete");

#LiveUser
insert into tblacloperations (opid,classid,opname) values (89, 12, "LiveUser All");
insert into tblacloperations (opid,classid,opname) values (90, 12, "LiveUser View");
insert into tblacloperations (opid,classid,opname) values (91, 12, "LiveUser Add");
insert into tblacloperations (opid,classid,opname) values (92, 12, "LiveUser Edit");
insert into tblacloperations (opid,classid,opname) values (93, 12, "LiveUser Delete");

#PostpaidPlan
insert into tblacloperations (opid,classid,opname) values (94, 13, "PostpaidPlan All");
insert into tblacloperations (opid,classid,opname) values (95, 13, "PostpaidPlan View");
insert into tblacloperations (opid,classid,opname) values (96, 13, "PostpaidPlan Add");
insert into tblacloperations (opid,classid,opname) values (97, 13, "PostpaidPlan Edit");
insert into tblacloperations (opid,classid,opname) values (98, 13, "PostpaidPlan Delete");

#Tax
insert into tblacloperations (opid,classid,opname) values (99, 14, "Tax All");
insert into tblacloperations (opid,classid,opname) values (100, 14, "Tax View");
insert into tblacloperations (opid,classid,opname) values (101, 14, "Tax Add");
insert into tblacloperations (opid,classid,opname) values (102, 14, "Tax Edit");
insert into tblacloperations (opid,classid,opname) values (103, 14, "Tax Delete");

#Charge
insert into tblacloperations (opid,classid,opname) values (104, 15, "Charge All");
insert into tblacloperations (opid,classid,opname) values (105, 15, "Charge View");
insert into tblacloperations (opid,classid,opname) values (106, 15, "Charge Add");
insert into tblacloperations (opid,classid,opname) values (107, 15, "Charge Edit");
insert into tblacloperations (opid,classid,opname) values (108, 15, "Charge Delete");

#Discount
insert into tblacloperations (opid,classid,opname) values (109, 16, "Discount All");
insert into tblacloperations (opid,classid,opname) values (110, 16, "Discount View");
insert into tblacloperations (opid,classid,opname) values (111, 16, "Discount Add");
insert into tblacloperations (opid,classid,opname) values (112, 16, "Discount Edit");
insert into tblacloperations (opid,classid,opname) values (113, 16, "Discount Delete");

#PlanService
insert into tblacloperations (opid,classid,opname) values (114, 17, "PlanService All");
insert into tblacloperations (opid,classid,opname) values (115, 17, "PlanService View");
insert into tblacloperations (opid,classid,opname) values (116, 17, "PlanService Add");
insert into tblacloperations (opid,classid,opname) values (117, 17, "PlanService Edit");
insert into tblacloperations (opid,classid,opname) values (118, 17, "PlanService Delete");

#QOSPolicy
insert into tblacloperations (opid,classid,opname) values (119, 18, "QOSPolicy All");
insert into tblacloperations (opid,classid,opname) values (120, 18, "QOSPolicy View");
insert into tblacloperations (opid,classid,opname) values (121, 18, "QOSPolicy Add");
insert into tblacloperations (opid,classid,opname) values (122, 18, "QOSPolicy Edit");
insert into tblacloperations (opid,classid,opname) values (123, 18, "QOSPolicy Delete");

#PriceBook
insert into tblacloperations (opid,classid,opname) values (124, 19, "PriceBook All");
insert into tblacloperations (opid,classid,opname) values (125, 19, "PriceBook View");
insert into tblacloperations (opid,classid,opname) values (126, 19, "PriceBook Add");
insert into tblacloperations (opid,classid,opname) values (127, 19, "PriceBook Edit");
insert into tblacloperations (opid,classid,opname) values (128, 19, "PriceBook Delete");

#ServiceArea
insert into tblacloperations (opid,classid,opname) values (129, 20, "ServiceArea All");
insert into tblacloperations (opid,classid,opname) values (130, 20, "ServiceArea View");
insert into tblacloperations (opid,classid,opname) values (131, 20, "ServiceArea Add");
insert into tblacloperations (opid,classid,opname) values (132, 20, "ServiceArea Edit");
insert into tblacloperations (opid,classid,opname) values (133, 20, "ServiceArea Delete");

#NetworkDevices
insert into tblacloperations (opid,classid,opname) values (134, 21, "NetworkDevices All");
insert into tblacloperations (opid,classid,opname) values (135, 21, "NetworkDevices View");
insert into tblacloperations (opid,classid,opname) values (136, 21, "NetworkDevices Add");
insert into tblacloperations (opid,classid,opname) values (137, 21, "NetworkDevices Edit");
insert into tblacloperations (opid,classid,opname) values (138, 21, "NetworkDevices Delete");

#IPPool
insert into tblacloperations (opid,classid,opname) values (139, 22, "IPPool All");
insert into tblacloperations (opid,classid,opname) values (140, 22, "IPPool View");
insert into tblacloperations (opid,classid,opname) values (141, 22, "IPPool Add");
insert into tblacloperations (opid,classid,opname) values (142, 22, "IPPool Edit");
insert into tblacloperations (opid,classid,opname) values (143, 22, "IPPool Delete");

#Case
insert into tblacloperations (opid,classid,opname) values (144, 23, "Case All");
insert into tblacloperations (opid,classid,opname) values (145, 23, "Case View");
insert into tblacloperations (opid,classid,opname) values (146, 23, "Case Add");
insert into tblacloperations (opid,classid,opname) values (147, 23, "Case Edit");
insert into tblacloperations (opid,classid,opname) values (148, 23, "Case Delete");

#CaseReason
insert into tblacloperations (opid,classid,opname) values (149, 24, "CaseReason All");
insert into tblacloperations (opid,classid,opname) values (150, 24, "CaseReason View");
insert into tblacloperations (opid,classid,opname) values (151, 24, "CaseReason Add");
insert into tblacloperations (opid,classid,opname) values (152, 24, "CaseReason Edit");
insert into tblacloperations (opid,classid,opname) values (153, 24, "CaseReason Delete");

#StaffUser
insert into tblacloperations (opid,classid,opname) values (154, 25, "StaffUser All");
insert into tblacloperations (opid,classid,opname) values (155, 25, "StaffUser View");
insert into tblacloperations (opid,classid,opname) values (156, 25, "StaffUser Add");
insert into tblacloperations (opid,classid,opname) values (157, 25, "StaffUser Edit");
insert into tblacloperations (opid,classid,opname) values (158, 25, "StaffUser Delete");
insert into tblacloperations (opid,classid,opname) values (186, 25, "StaffUser Change Password");

#Broadcast
insert into tblacloperations (opid,classid,opname) values (159, 26, "Broadcast All");
insert into tblacloperations (opid,classid,opname) values (160, 26, "Broadcast View");
insert into tblacloperations (opid,classid,opname) values (161, 26, "Broadcast Add");
insert into tblacloperations (opid,classid,opname) values (162, 26, "Broadcast Edit");
insert into tblacloperations (opid,classid,opname) values (163, 26, "Broadcast Delete");

#XsltManagement
insert into tblacloperations (opid,classid,opname) values (164, 27, "XsltManagement All");
insert into tblacloperations (opid,classid,opname) values (165, 27, "XsltManagement View");
insert into tblacloperations (opid,classid,opname) values (166, 27, "XsltManagement Add");
insert into tblacloperations (opid,classid,opname) values (167, 27, "XsltManagement Edit");
insert into tblacloperations (opid,classid,opname) values (168, 27, "XsltManagement Delete");

#Role
insert into tblacloperations (opid,classid,opname) values (169, 28, "Role All");
insert into tblacloperations (opid,classid,opname) values (170, 28, "Role View");
insert into tblacloperations (opid,classid,opname) values (171, 28, "Role Add");
insert into tblacloperations (opid,classid,opname) values (172, 28, "Role Edit");
insert into tblacloperations (opid,classid,opname) values (173, 28, "Role Delete");

#Teams
insert into tblacloperations (opid,classid,opname) values (187, 29, "Teams All");
insert into tblacloperations (opid,classid,opname) values (188, 29, "Teams View");
insert into tblacloperations (opid,classid,opname) values (189, 29, "Teams Add");
insert into tblacloperations (opid,classid,opname) values (190, 29, "Teams Edit");
insert into tblacloperations (opid,classid,opname) values (191, 29, "Teams Delete");

#Notification
insert into tblacloperations (opid,classid,opname) values (192, 30, "Notification All");
insert into tblacloperations (opid,classid,opname) values (193, 30, "Notification View");
insert into tblacloperations (opid,classid,opname) values (194, 30, "Notification Add");
insert into tblacloperations (opid,classid,opname) values (195, 30, "Notification Edit");
insert into tblacloperations (opid,classid,opname) values (196, 30, "Notification Delete");

#Bulk Upload Network Devices
insert into tblacloperations (opid,classid,opname) values (197, 31, "Bulk Upload Network Devices");

#Bulk Upload Case
insert into tblacloperations (opid,classid,opname) values (198, 32, "Bulk Upload Case");

#Manage Profile
insert into tblacloperations (opid,classid,opname) values (199, 33, "Manage Profile All");
insert into tblacloperations (opid,classid,opname) values (200, 33, "Manage Password");
insert into tblacloperations (opid,classid,opname) values (201, 33, "Manage Details");
insert into tblacloperations (opid,classid,opname) values (202, 33, "Top up balance");
insert into tblacloperations (opid,classid,opname) values (203, 33, "Associate Plans");
insert into tblacloperations (opid,classid,opname) values (204, 33, "Commision");
insert into tblacloperations (opid,classid,opname) values (205, 33, "Payments");


# TRUNCATE TABLE tblaclentry;

# ACL entries for admin role
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 1, 1);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 2, 6);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 3, 11);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 4, 16);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 5, 21);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 6, 59);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 7, 64);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 8, 69);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 9, 74);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 10, 79);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 11, 84);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 12, 89);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 13, 94);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 14, 99);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 15, 104);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 16, 109);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 17, 114);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 18, 119);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 19, 124);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 20, 129);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 21, 134);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 22, 139);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 23, 144);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 24, 149);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 25, 154);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 26, 159);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 27, 164);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 28, 169);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 29, 187);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 30, 192);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 31, 197);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 32, 198);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 33, 199);

# ACL entries for subscriber role
insert into tblaclentry (roleid , permit, classid ) values (8, 22, 5);
insert into tblaclentry (roleid , permit, classid ) values (8, 36, 5);
insert into tblaclentry (roleid , permit, classid ) values (8, 52, 5);
insert into tblaclentry (roleid , permit, classid ) values (8, 53, 5);
insert into tblaclentry (roleid , permit, classid ) values (8, 95, 5);
insert into tblaclentry (roleid , permit, classid ) values (8, 174, 5);
insert into tblaclentry (roleid , permit, classid ) values (8, 177, 5);
insert into tblaclentry (roleid , permit, classid ) values (8, 178, 5);
insert into tblaclentry (roleid , permit, classid ) values (8, 179, 5);
insert into tblaclentry (roleid , permit, classid ) values (8, 85, 11);
insert into tblaclentry (roleid , permit, classid ) values (8, 145, 23);
insert into tblaclentry (roleid , permit, classid ) values (8, 146, 23);
insert into tblaclentry (roleid , permit, classid ) values (8, 147, 23);
insert into tblaclentry (roleid , permit, classid ) values (8, 150, 24);

insert into tblroles(roleid,rolename,rstatus,sysrole) values (1, 'Admin','ACTIVE',1);
insert into tblroles(roleid,rolename,rstatus,sysrole) values (2, 'Operator','ACTIVE',1);
insert into tblroles(roleid,rolename,rstatus,sysrole) values (3,'Partner','ACTIVE',1);
insert into tblroles(roleid,rolename,rstatus,sysrole) values (4,'Partner_Manager','ACTIVE',1);
insert into tblroles(roleid,rolename,rstatus,sysrole) values (5,'Partner_Operator','ACTIVE',1);
insert into tblroles(roleid, rolename, rstatus,sysrole) values (6, 'Sales Representative', 'ACTIVE',1);
insert into tblroles(roleid, rolename, rstatus,sysrole) values (7, 'Back Office Staff', 'ACTIVE', 1);
insert into tblroles (roleid, rolename, rstatus,  sysrole) values (8, "Subscriber", "Active", 1);
insert into tblroles (roleid, rolename, rstatus, sysrole) values (9, 'PGUser', 'Active', 1);


insert into tblstaffuser(staffid,username,password,firstname,lastname,email,phone,failcount,sstatus,sysstaff,partnerid) 
values(1,'admin','admin@123','admin','admin','admin@admin.com','9999999999',0,'ACTIVE',1, 1);
update tblstaffuser set password='$2a$10$vZnz0KJY7052BllpxqHNPuEyfszEk6ZkZdgntxTgm8FvvMaxHX4oO' where staffid=1;
insert into tblstaffrolerel(staffid,roleid) values(1,1);
insert into tblstaffuser (staffid,username,password,firstname,lastname,email,phone,sstatus,sysstaff,partnerid)
values (2,'pguser','$2a$10$vZnz0KJY7052BllpxqHNPuEyfszEk6ZkZdgntxTgm8FvvMaxHX4oO','PG','USER','pguser@gmail.com','9876543210','Active',1,1);

insert into tblstaffrolerel (staffid,roleid) values ((select staffid from tblstaffuser t where t.username = 'pguser'),
(select roleid from tblroles where roleid  = 9));



INSERT INTO TBLPARTNERS(PARTNERID, PARTNERNAME, status) VALUES (1,'Default','Y');


-- server conf
insert into tblserverconf(attributename,attributevalue) values('FAILLIMITCOUNT','1000');
insert into tblserverconf(attributename,attributevalue) values('PASSWORDCHANGEVALIDITY','365');
insert into tblserverconf(attributename,attributevalue) values('PROFILEMANDATORY','0');


INSERT INTO TBLMMVNO (NAME,SUFFIX,DESCRIPTION,EMAIL,PHONE,ADDRESS,STATUS,LOGOFILE,MVNOHEADER,MVNOFOOTER,CREATEDATE,LASTMODIFIEDDATE) value('Default','DEF','Default MVNO','admin@default.com','1234567890','INDIA','1',null,null,null,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP());

insert into TBLCLIENTSERVICE values(1,'engineid','e1');
insert into TBLCLIENTSERVICE values(2,'enginelastrun','22/12/2016 11:00:00.06 AM');
insert into TBLCLIENTSERVICE values(3,'pdfpath','//var/billpdf');
insert into TBLCLIENTSERVICE values(4,'mailfrom','aa.aa@gmail.com');
insert into TBLCLIENTSERVICE values(5,'mailheader','THIS IS HEADER');
insert into TBLCLIENTSERVICE values(6,'maildata','THIS IS BODY');
insert into TBLCLIENTSERVICE values(7,'smsheader',',your bill amount Rs.');
insert into TBLCLIENTSERVICE values(8,'smsdata','is due on');
insert into TBLCLIENTSERVICE values(9,'smppip','192.168.11.27');
insert into TBLCLIENTSERVICE values(10,'smppport','13013');
insert into TBLCLIENTSERVICE(name,value) value('partnerpdfpath','c:/comission');
insert into TBLCLIENTSERVICE(name,value) value('partnerroleid','3');
insert into TBLCLIENTSERVICE(name,value) value('partnermanagerroleid','4');
insert into TBLCLIENTSERVICE(name,value) value('partneroperatorroleid','5');
insert into TBLCLIENTSERVICE values(15,'trialpdfpath','c:/trialbills');
insert into TBLCLIENTSERVICE(name,value) value('paymentpdfpath','c:/payment');
insert into TBLCLIENTSERVICE(name,value) values('paymentmailheader','Payment Receipt');
insert into TBLCLIENTSERVICE(name,value) values('paymentmaildata','Payment Receipt Attached');
insert into TBLCLIENTSERVICE(name,value) values('inmode','0');


insert into tbldbmappingmaster(mappingname) values('CDRMAPPING');

insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'User-Name','UserName');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'NAS-IP-Address','NASIPAddress');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'NAS-Port','NASPort');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Service-Type','ServiceType');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Framed-Protocol','FramedProtocol');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Framed-IP-Address','FramedIPAddress');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Framed-IP-Netmask','FramedIPNetmask');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Framed-Routing','FramedRouting');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Filter-Id','FilterId');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Framed-MTU','FramedMTU');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Framed-Compression','FramedCompression');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Login-IP-Host','LoginIPHost');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Login-Service','LoginService');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Login-TCP-Port','LoginTCPPort');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Reply-Message','ReplyMessage');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Callback-Number','CallbackNumber');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Callback-Id','CallbackId');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Framed-Route','FramedRoute');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Framed-IPX-Network','FramedIPXNetwork');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'State','State');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Class','Class');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Vendor-Specific','VendorSpecific');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Session-Timeout','SessionTimeout');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Idle-Timeout','IdleTimeout');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Termination-Action','TerminationAction');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Called-Station-Id','CalledStationId');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Calling-Station-Id','CallingStationId');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'NAS-Identifier','NASIdentifier');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Proxy-State','ProxyState');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Login-LAT-Service','LoginLATService');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Login-LAT-Node','LoginLATNode');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Login-LAT-Group','LoginLATGroup');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Framed-AppleTalk-Link','FramedAppleTalkLink');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Framed-AppleTalk-Network','FramedAppleTalkNetwork');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Framed-AppleTalk-Zone','FramedAppleTalkZone');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Acct-Status-Type','AcctStatusType');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Acct-Delay-Time','AcctDelayTime');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Acct-Input-Octets','AcctInputOctets');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Acct-Output-Octets','AcctOutputOctets');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Acct-Session-Id','AcctSessionId');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Acct-Authentic','AcctAuthentic');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Acct-Session-Time','AcctSessionTime');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Acct-Input-Packets','AcctInputPackets');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Acct-Output-Packets','AcctOutputPackets');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Acct-Terminate-Cause','AcctTerminateCause');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Acct-Multi-Session-Id','AcctMultiSessionId');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Acct-Link-Count','AcctLinkCount');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Acct-Input-Gigawords','AcctInputGigawords');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Acct-Output-Gigawords','AcctOutputGigawords');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Event-Timestamp','EventTimestamp');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'CHAP-Challenge','CHAPChallenge');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'NAS-Port-Type','NASPortType');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Port-Limit','PortLimit');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Login-LAT-Port','LoginLATPort');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Acct-Tunnel-Connection','AcctTunnelConnection');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'ARAP-Features','ARAPFeatures');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'ARAP-Zone-Access','ARAPZoneAccess');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'ARAP-Security','ARAPSecurity');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'ARAP-Security-Data','ARAPSecurityData');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Password-Retry','PasswordRetry');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Prompt','Prompt');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Configuration-Token','ConfigurationToken');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'EAP-Message','EAPMessage');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Message-Authenticator','MessageAuthenticator');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'ARAP-Challenge-Response','ARAPChallengeResponse');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Acct-Interim-Interval','AcctInterimInterval');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'NAS-Port-Id','NASPortId');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Framed-Pool','FramedPool');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'NAS-IPv6-Address','NASIPv6Address');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Framed-Interface-Id','FramedInterfaceId');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Framed-IPv6-Prefix','FramedIPv6Prefix');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Login-IPv6-Host','LoginIPv6Host');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Framed-IPv6-Route','FramedIPv6Route');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Framed-IPv6-Pool','FramedIPv6Pool');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Digest-Response','DigestResponse');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Digest-Attributes','DigestAttributes');
insert into tbldbmapping(mappingmasterid,radiusname,dbcolumnname)values(1,'Connect-Info','ConnectInfo');

insert into tblacctprofile(name,status,checkitem,accountcdrstatus,sessionstatus,mappingmasterid,priority,created_on,lastmodified_on) values (
'Default','A','','Y','Y',1,999,current_timestamp,current_timestamp
);

-- INSERT INTO tblnotifications (name,email_enabled,sms_enabled,status,category,email_body,sms_body,createdbystaffid,createdate,lastmodifiedbystaffid,lastmodifieddate,is_deleted,template_id) VALUES
-- 	 ('Complain Resolution',0,1,'active','generic','','Dear%20$userName%2C%0A%0AYour%20complaint%206666%20has%20been%20Resolved.Kindly%20give%20your%20feedback%20regarding%20your%20interaction%20with%20our%20Customer%20Service%20%26%20Implementation%20Team%2C%20for%20any%20further%20Assistance%20kindly%20contact%20on%20our%20toll%20free%2018002664986.%0A%0',4,'2021-06-30 18:51:04',4,'2021-06-30 18:51:04',0,'1207161786295759631'),
-- 	 ('Registration',0,1,'active','generic','','UserName%20%3A%20$userName%20Password%20%3A%20$password%20%0A%0A%0',4,'2021-06-30 18:51:04',4,'2021-06-30 18:51:04',0,'1207160974476390926');
-- 
-- INSERT INTO tblnotifications
-- (name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)
-- VALUES('Wind and Rainfall', 0, 1, 'active', 'generic', '', 'Dear%20EthernetXpress%20Customer%21%21%20Due%20to%20heavy%20winds%20and%20rainfall%20our%20main%20fibre%20connectivity%20has%20been%20affected%20in%20your%20area.%0AOur%20team%20is%20working%20on%20the%20same.%0AInconvenience%20caused%20is%20highly%20regretted.%20%0A%0', 1, '2021-07-08 13:48:00', 1, '2021-07-08 13:48:00', 0, '1207162200637799409');

INSERT INTO tbl_payment_gateway
values (1, '', 'https://test.payu.in/_payment', 'Payu-Money', 0, 1, 0, 1, '2021-07-29', 1, '2021-07-29', 'payu',
        'Active');

INSERT INTO tbl_payment_gateway
values (2, '', 'https://test.ccavenue.com', 'CCAvenue', 1, 0, 1, 1, '2021-07-29', 1, '2021-07-29', 'ccavenue',
        'Active');

INSERT INTO tblnotifications(name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)VALUES('Complain Resolution', 0, 1, 'active', 'generic', 'Dear $userName,

Your complaint $complainNo has been Resolved.Kindly give your feedback regarding your interaction with our Customer Service & Implementation Team, for any further Assistance kindly contact on our toll free 18002664986.

', 'Dear%20$userName%2C%0A%0AYour%20complaint%20$complainNo%20has%20been%20Resolved.Kindly%20give%20your%20feedback%20regarding%20your%20interaction%20with%20our%20Customer%20Service%20%26%20Implementation%20Team%2C%20for%20any%20further%20Assistance%20kindly%20contact%20on%20our%20toll%20free%2018002664986.%0A%0', 4, '2021-06-30 18:51:04', 4, '2021-06-30 18:51:04', 0, '1207161786295759631');

INSERT INTO tblnotifications(name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)VALUES('Registration', 0, 1, 'active', 'generic', 'UserName : $userName Password : $password


', 'UserName%20%3A%20$userName%20Password%20%3A%20$password%20%0A%0A%0', 4, '2021-06-30 18:51:04', 4, '2021-06-30 18:51:04', 0, '1207160974476390926');

INSERT INTO tblnotifications(name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)VALUES('Wind and Rainfall', 0, 1, 'active', 'generic', 'Dear EthernetXpress Customer!! Due to heavy winds and rainfall our main fibre connectivity has been affected in your area.
Our team is working on the same.
Inconvenience caused is highly regretted.

', 'Dear%20EthernetXpress%20Customer%21%21%20Due%20to%20heavy%20winds%20and%20rainfall%20our%20main%20fibre%20connectivity%20has%20been%20affected%20in%20your%20area.%0AOur%20team%20is%20working%20on%20the%20same.%0AInconvenience%20caused%20is%20highly%20regretted.%20%0A%0', 1, '2021-07-08 13:49:30', 1, '2021-07-08 13:49:30', 0, '1207162200637799409');

INSERT INTO tblnotifications (name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id) VALUES('Ticket Closed', 0, 1, 'active', 'generic', 'Dear $userName, your complaint $complainNo has been Resolved.Kindly give your feedback regarding your interaction with our Customer Service & Implementation Team, for any further Assistance kindly contact on $contactNo, or can drop SMS on our whatsapp no. $whatsappNo1 and $whatsappNo2.', 'Dear%20$userName%2C%20your%20complaint%20$complainNo%20has%20been%20Resolved.Kindly%20give%20your%20feedback%20regarding%20your%20interaction%20with%20our%20Customer%20Service%20%26%20Implementation%20Team%2C%20for%20any%20further%20Assistance%20kindly%20contact%20on%20$contactNo%2C%20or%20can%20drop%20SMS%20on%20our%20whatsapp%20no.%20$whatsappNo1%20and%20$whatsappNo2.', NULL, NULL, NULL, NULL, 0, '1207159809799727673');

INSERT INTO tblnotifications(name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)VALUES('Speed Reduced', 0, 1, 'active', 'generic', 'Dear Subscriber, your speed of $planName will be reduced after usage of $usage for the rest of current month. You can renew your plan to avail high speed.', 'Dear%20Subscriber%2C%20your%20speed%20of%20$planName%20will%20be%20reduced%20after%20usage%20of%20$usage%20for%20the%20rest%20of%20current%20month.%20You%20can%20renew%20your%20plan%20to%20avail%20high%20speed.', NULL, NULL, NULL, NULL, 0, '1207159868378865978');

INSERT INTO tblnotifications(name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)VALUES('Package Renewed', 0, 1, 'active', 'generic', 'Dear Customer, Your account has been renewed with package $planName on $date.', 'Dear%20Customer%2C%20Your%20account%20has%20been%20renewed%20with%20package%20$planName%20on%20$date.', NULL, NULL, NULL, NULL, 0, '1207159868388079301');

INSERT INTO tblnotifications(name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)VALUES('Internet Enabled', 0, 1, 'active', 'generic', 'We are pleased to inform that your Internet Service has been enabled. Enjoy Browsing.

', 'We%20are%20pleased%20to%20inform%20that%20your%20Internet%20Service%20has%20been%20enabled.%20Enjoy%20Browsing.%0A%0', NULL, NULL, NULL, NULL, 0, '1207160974676966969');

INSERT INTO tblnotifications(name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)VALUES('Partner Aggreement', 0, 1, 'active', 'generic', 'Dear Partner,

Your Partner Agreement initiated. Please submit.

', 'Dear%20Partner%2C%0A%0AYour%20Partner%20Agreement%20initiated.%20Please%20submit.%0A%0', NULL, NULL, NULL, NULL, 0, '1207160974454015288');

INSERT INTO tblnotifications(name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)VALUES('Static Ip Expire', 0, 1, 'active', 'generic', 'Dear $userName,

Your Static IP $ip for account $userName is going to expire on $expiry.

', 'Dear%20$userName%2C%0A%0AYour%20Static%20IP%20$ip%20for%20account%20$userName%20is%20going%20to%20expire%20on%20$expiry.%0A%0', NULL, NULL, NULL, NULL, 0, '1207160974470771754');

INSERT INTO tblnotifications(name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)VALUES('Profile Update', 0, 1, 'active', 'generic', 'Dear Customer,

Your profile information has been updated. For your records, here is a copy of the information you submitted to us... Email Address: $email, Mobile No.: $mobile, GSTIN: $gstin

', 'Dear%20Customer%2C%0A%0AYour%20profile%20information%20has%20been%20updated.%20For%20your%20records%2C%20here%20is%20a%20copy%20of%20the%20information%20you%20submitted%20to%20us...%20Email%20Address%3A%20$email%2C%20Mobile%20No.%3A%20$mobile%2C%20GSTIN%3A%20$gstin%20%0A%0', NULL, NULL, NULL, NULL, 0, '1207160974400885347');

INSERT INTO tblnotifications(name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)VALUES('Acc Suspended', 0, 1, 'active', 'generic', 'Dear $userName

We regret to inform that the account with user id : $userName is suspended. Please contact us for queries.

', 'Dear%20$userName%0A%0AWe%20regret%20to%20inform%20that%20the%20account%20with%20user%20id%20%3A%20$userName%20is%20suspended.%20Please%20contact%20us%20for%20queries.%20%0A%0', NULL, NULL, NULL, NULL, 0, '1207160974486198147');

INSERT INTO tblnotifications(name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)VALUES('Charge Recieved', 0, 1, 'active', 'generic', 'Thank you we have received the Charge of $amount Rs and Applied Charge $chargeName.

', 'Thank%20you%20we%20have%20received%20the%20Charge%20of%20$amount%20Rs%20and%20Applied%20Charge%20$chargeName%20.%0A%0', NULL, NULL, NULL, NULL, 0, '1207160974503347684');

INSERT INTO tblnotifications(name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)VALUES('Internet Disabled', 0, 1, 'active', 'generic', 'We regret to inform that your Internet Service has been disabled. Please contact us for queries.

', 'We%20regret%20to%20inform%20that%20your%20Internet%20Service%20has%20been%20disabled.%20Please%20contact%20us%20for%20queries.%20%0A%0', NULL, NULL, NULL, NULL, 0, '1207160974664277469');

INSERT INTO tblnotifications(name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)VALUES('Acc Terminated', 0, 1, 'active', 'generic', 'We regret to inform you that your Internet Service is Terminated, please contact us.

', 'We%20regret%20to%20inform%20you%20that%20your%20Internet%20Service%20is%20Terminated%2C%20please%20contact%20us.%0A%0', NULL, NULL, NULL, NULL, 0, '1207160974671718782');

INSERT INTO tblnotifications(name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)VALUES('Acc Registered', 0, 1, 'active', 'generic', 'Dear Customer,

Your account has been regd with: ID: $userName password: $password Regi.Dt: $registerDate Plan:$planName.

', 'Dear%20Customer%2C%0A%0AYour%20account%20has%20been%20regd%20with%3A%20ID%3A%20$userName%20password%3A%20$password%20Regi.Dt%3A%20$registerDate%20Plan%3A$planName.%0A%0', NULL, NULL, NULL, NULL, 0, '1207160974685994178');

INSERT INTO tblnotifications(name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)VALUES('Package Renewed', 0, 1, 'active', 'generic', 'Dear  $userName,

Your account with user ID : $userName has been renewed with package $planName on $date .

', 'Dear%20%20$userName%2C%20%0A%0AYour%20account%20with%20user%20ID%20%3A%20$userName%20has%20been%20renewed%20with%20package%20$planName%20on%20$date%20.%0A%0', NULL, NULL, NULL, NULL, 0, '1207160974690447517');

INSERT INTO tblnotifications(name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)VALUES('Open Complaint', 0, 1, 'active', 'generic', 'Dear $userName,

We Have Received Your Complaint with complaint id $complainNo, Resolution Process has been started..

', 'Dear%20$userName%2C%0A%0AWe%20Have%20Received%20Your%20Complaint%20with%20complaint%20id%20$complainNo%2C%20Resolution%20Process%20has%20been%20started..%0A%0', NULL, NULL, NULL, NULL, 0, '1207161786235660465');

INSERT INTO tblnotifications(name, email_enabled, sms_enabled, status, category, email_body, sms_body, createdbystaffid, createdate, lastmodifiedbystaffid, lastmodifieddate, is_deleted, template_id)VALUES('Forgot Password', 1, 0, 'active', 'generic', 'Your OTP For Forgot Password is $otp', NULL, NULL, NULL, NULL, NULL, 0, '0');

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
    add column LASTMODIFIEDBYSTAFFID NUMERIC(20)  default 1 not null,
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
