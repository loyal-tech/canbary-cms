#Mohit 20-08-2021
insert into tblclientservice (name, value) values("nexg_server", "http://voice1.client.in:8080");
insert into tblclientservice (name, value) values("nexg_api_key", "zypHHgyh.ucq9WE69E9fTGhd6vlX3USDD8hzlSamPEm7");
insert into tblclientservice (name, value) values("nexg_auth_userid", "admin");
insert into tblclientservice (name, value) values("nexg_auth_password", "sgrfhjSDFSDVG");
insert into tblclientservice (name, value) values("nexg_req_parent", "expl");

ALTER TABLE tblcustomers add column voiceProvision boolean default false;