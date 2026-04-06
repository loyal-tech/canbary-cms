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
insert into tblaclclass (classid,classname,dispname,disporder,operallid)
values (34, "com.adopt.apigw.model.postpaid.CustomerAddress","CustomerAddress",34, 206);

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

#CustomerAddress
insert into tblacloperations (opid,classid,opname) values (206, 34, "CustomerAddress All");
insert into tblacloperations (opid,classid,opname) values (207, 34, "CustomerAddress View");
insert into tblacloperations (opid,classid,opname) values (208, 34, "CustomerAddress Add");
insert into tblacloperations (opid,classid,opname) values (209, 34, "CustomerAddress Edit");
insert into tblacloperations (opid,classid,opname) values (210, 34, "CustomerAddress Delete");

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
values (1, 28, 164);
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 29, 169);
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
INSERT INTO tblaclentry (roleid, classid, permit)
values (1, 34, 206);

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

