INSERT INTO tblmcountry
(COUNTRYID, NAME, status, CREATEDATE, CREATEDBYSTAFFID, LASTMODIFIEDBYSTAFFID, LASTMODIFIEDDATE, is_delete,
 createbyname, updatebyname)
VALUES (1, 'India', 'Active', '2021-03-07 13:59:32', 1, 1, '2021-08-24 12:04:09', 0, 'admin admin', 'admin admin');


INSERT INTO tblmstate
(STATEID, NAME, COUNTRYID, status, CREATEDATE, CREATEDBYSTAFFID, LASTMODIFIEDBYSTAFFID, LASTMODIFIEDDATE, is_deleted,
 createbyname, updatebyname)
VALUES (1, 'Goa', 1, 'Active', '2021-03-07 13:59:52', 1, 1, '2021-08-24 12:18:38', 0, 'admin admin', 'admin admin');


INSERT INTO tblmcity
(CITYID, NAME, STATEID, status, CREATEDATE, CREATEDBYSTAFFID, LASTMODIFIEDBYSTAFFID, LASTMODIFIEDDATE, countryid,
 is_delete, createbyname, updatebyname)
VALUES (1, 'Goa', 1, 'Active', '2021-03-07 14:00:04', 1, 1, '2021-08-10 11:43:37', 1, 1, 'admin admin',
        'admin admin');

