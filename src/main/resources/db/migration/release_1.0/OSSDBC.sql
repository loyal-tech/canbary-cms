

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

insert into tblstaffuser(username,password,firstname,lastname,email,phone,failcount,sstatus,last_login_time,created_on,lastmodified_on) values('admin','admin@123','admin','admin','admin@admin.com','9999999999',0,'ACTIVE',current_timestamp,current_timestamp,current_timestamp);

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
  

insert into tblacctprofile(name,status,checkitem,accountcdrstatus,sessionstatus,mappingmasterid,priority,created_on,lastmodified_on) values (
'Default','A','','Y','Y',1,999,current_timestamp,current_timestamp
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

insert into tblstaffuser(username,password,firstname,lastname,email,phone,failcount,sstatus,last_login_time,created_on,lastmodified_on) values('admin','admin@123','admin','admin','admin@admin.com','9999999999',0,'ACTIVE',current_timestamp,current_timestamp,current_timestamp);

insert into tblroles(rolename,rstatus,created_on,lastmodified_on) values ('Admin','ACTIVE',current_timestamp,current_timestamp);

insert into tblroles(rolename,rstatus,created_on,lastmodified_on) values ('Operator','ACTIVE',current_timestamp,current_timestamp);

insert into tblstaffrolerel(staffid,roleid) values(1,1);
alter table tblstaffuser modify password  varchar(500);
update tblstaffuser set password='$2a$10$vZnz0KJY7052BllpxqHNPuEyfszEk6ZkZdgntxTgm8FvvMaxHX4oO' where staffid=1;


create table tblaclclass(
    classid bigint primary key,
    classname varchar(100) not null,
    dispname varchar(100) not null,
    disporder int,
    constraint unique_uk_2 unique(classname)
);

create table tblaclentry(
    aclid SERIAL primary key,
	classid bigint,
	roleid  bigint,
	permit int
);

INSERT INTO tblaclclass  VALUES  (1,'com.adopt.apigw.model.Customers','Customer',1);
INSERT INTO tblaclclass  VALUES  (2,'com.adopt.apigw.model.Clients','Radius Client',2);
INSERT INTO tblaclclass  VALUES  (3,'com.adopt.apigw.model.RadiusProfile','Radius Profile',3);
INSERT INTO tblaclclass  VALUES  (4,'com.adopt.apigw.model.ClientGroup','Client Group',4);
INSERT INTO tblaclclass  VALUES  (5,'com.adopt.apigw.model.RadiusServer','Radius Server',5);
INSERT INTO tblaclclass  VALUES  (6,'com.adopt.apigw.model.DbcdrProcessing','Radius CDR',6);
INSERT INTO tblaclclass  VALUES  (7,'com.adopt.apigw.model.LiveUser','Live Users',7);
INSERT INTO tblaclclass  VALUES  (8,'com.adopt.apigw.model.Role','Role',8);
INSERT INTO tblaclclass  VALUES  (9,'com.adopt.apigw.model.StaffUser','User',9);



/* ADMIN Acess entries
 * Here permit is like this 
 * 1 - Read
 * 2 - Write
 * 4 - Delete
 * 8 - Admin 
*/
INSERT INTO tblaclentry(classid,roleid,permit) values(1,1,4);
INSERT INTO tblaclentry(classid,roleid,permit) values(2,1,4);
INSERT INTO tblaclentry(classid,roleid,permit) values(3,1,4);
INSERT INTO tblaclentry(classid,roleid,permit) values(4,1,4);
INSERT INTO tblaclentry(classid,roleid,permit) values(5,1,4);
INSERT INTO tblaclentry(classid,roleid,permit) values(6,1,4);
INSERT INTO tblaclentry(classid,roleid,permit) values(7,1,4);
INSERT INTO tblaclentry(classid,roleid,permit) values(8,1,4);
INSERT INTO tblaclentry(classid,roleid,permit) values(9,1,4);


alter table tbldbmappingmaster add column created_on timestamp DEFAULT CURRENT_TIMESTAMP;
alter table tbldbmappingmaster add column lastmodified_on timestamp  DEFAULT CURRENT_TIMESTAMP;

/*ACL ENtries for Admin Allow*/
INSERT INTO tblaclclass  VALUES  (10,'com.adopt.apigw.model.DBMappingMaster','DB Mapping',10);
INSERT INTO tblaclclass  VALUES  (11,'com.adopt.apigw.model.AccountingProfile','Accounting Profile',11);
INSERT INTO tblaclentry(classid,roleid,permit) values(10,1,4);
INSERT INTO tblaclentry(classid,roleid,permit) values(11,1,4);





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

insert into tblserverconf(attributename,attributevalue) values('FAILLIMITCOUNT','1000');
insert into tblserverconf(attributename,attributevalue) values('PASSWORDCHANGEVALIDITY','365');
insert into tblserverconf(attributename,attributevalue) values('PROFILEMANDATORY','0');


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

 INSERT INTO TBLMMVNO (NAME,SUFFIX,DESCRIPTION,EMAIL,PHONE,ADDRESS,STATUS,LOGOFILE,MVNOHEADER,MVNOFOOTER,CREATEDATE,LASTMODIFIEDDATE) value('Default','DEF','Default MVNO','admin@default.com','1234567890','INDIA','1',null,null,null,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP());
 
 
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

INSERT INTO TBLPARTNERS VALUES (1,'Default','Y',CURRENT_TIMESTAMP(),1,1,CURRENT_TIMESTAMP());
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
 
insert into TBLCLIENTSERVICE(name,value) value('partnerpdfpath','c:/comission');

  
alter table tblpartners modify country numeric(2);
alter table tblpartners modify state numeric(2);
alter table tblpartners modify city numeric(2);

insert into tblroles(roleid,rolename,rstatus,created_on,lastmodified_on) values (3,'Partner','ACTIVE',current_timestamp,current_timestamp);
insert into tblroles(roleid,rolename,rstatus,created_on,lastmodified_on) values (4,'Partner_Manager','ACTIVE',current_timestamp,current_timestamp);
insert into tblroles(roleid,rolename,rstatus,created_on,lastmodified_on) values (5,'Partner_Operator','ACTIVE',current_timestamp,current_timestamp);
ALTER TABLE tblroles AUTO_INCREMENT=6;

insert into TBLCLIENTSERVICE(name,value) value('partnerroleid','3');
insert into TBLCLIENTSERVICE(name,value) value('partnermanagerroleid','4');
insert into TBLCLIENTSERVICE(name,value) value('partneroperatorroleid','5');

alter table tblpartnercommrel modify comm_rel_value numeric(20,4);




INSERT INTO tblaclclass  VALUES  (12,'com.adopt.apigw.model.temp2','Payment',12);
INSERT INTO tblaclclass  VALUES  (13,'com.adopt.apigw.model.temp3','Dunning',13);
INSERT INTO tblaclclass  VALUES  (14,'com.adopt.apigw.model.temp4','Invoice',14);
INSERT INTO tblaclclass  VALUES  (15,'com.adopt.apigw.model.temp','Plans',15);
INSERT INTO tblaclclass  VALUES  (16,'com.adopt.apigw.model.temp1','Partners',16);


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

insert into TBLCLIENTSERVICE values(15,'trialpdfpath','c:/trialbills');

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

insert into TBLCLIENTSERVICE(name,value) value('paymentpdfpath','c:/payment');
insert into TBLCLIENTSERVICE(name,value) values('paymentmailheader','Payment Receipt');
insert into TBLCLIENTSERVICE(name,value) values('paymentmaildata','Payment Receipt Attached');

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

insert into TBLCLIENTSERVICE(name,value) values('inmode','0');

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

insert into tblcommonlist ( list_text, list_value, list_type, status) values( 'Mr', 'Mr', 'title', 'Active');
insert into tblcommonlist ( list_text, list_value, list_type, status) values('Ms', 'Ms', 'title', 'Active');
insert into tblcommonlist ( list_text, list_value, list_type, status) values('Mrs', 'Mrs', 'title', 'Active');
insert into tblcommonlist ( list_text, list_value, list_type, status) values('Miss', 'Miss', 'title', 'Active');
insert into tblcommonlist ( list_text, list_value, list_type, status) values('IPOE', 'IPOE', 'network', 'Active');
insert into tblcommonlist ( list_text, list_value, list_type, status) values('PPPOE', 'PPPOE', 'network', 'Active');
insert into tblcommonlist ( list_text, list_value, list_type, status) values('PhoneLine', 'PhoneLine', 'voiceService', 'Active');
insert into tblcommonlist ( list_text, list_value, list_type, status) values('ShipTrunk', 'ShipTrunk', 'voiceService', 'Active');
insert into tblcommonlist ( list_text, list_value, list_type, status) values('Intercom', 'Intercom', 'voiceService', 'Active');
insert into tblcommonlist ( list_text, list_value, list_type, status) values('Group1', 'Group1', 'intercomGroup', 'Active');

-- Nilesh | 03-05-2021

/*create table tblcustdocdetails(
    docId SERIAL PRIMARY KEY,
    docType VARCHAR (100) NOT NULL,
    docStatus VARCHAR (100) NOT NULL,
    attachmentPath VARCHAR (100) NOT NULL,
    isDeleted Boolean NOT NULL,
    createDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lastModifiedDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    createdByStaffId BIGINT (100) NOT NULL,
    lastModifiedByStaffId VARCHAR (100) NOT NULL
);

insert into tblcustdocdetails(docId, docType, docStatus, attachmentPath, isDeleted, createdByStaffId, lastModifiedByStaffId) values (100, 'aadhar', 'Active', 'D:\nic.jpg', false, 1, 1);
insert into tblcustdocdetails(docId, docType, docStatus, attachmentPath, isDeleted, createdByStaffId, lastModifiedByStaffId) values (101, 'pan', 'Active', 'D:\pan.jpg', false, 1, 1);

