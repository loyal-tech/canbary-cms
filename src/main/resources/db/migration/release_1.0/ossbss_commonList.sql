#Jaymin 10-07-2021
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)
VALUES ('Price Book','pricebook', 'partnerCommType', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('1', '1', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('2', '2', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('3', '3', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('4', '4', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('5', '5', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('6', '6', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('7', '7', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('8', '8', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('9', '9', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('10', '10', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('11', '11', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('12', '12', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('13', '13', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('14', '14', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('15', '15', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('16', '16', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('17', '17', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('18', '18', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('19', '19', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('20', '20', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('21', '21', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('22', '22', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('23', '23', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('24', '24', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('25', '25', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('26', '26', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('27', '27', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('28', '28', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('29', '29', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('30', '30', 'billingDay', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('31', '31', 'billingDay', 'Active');

#Jaymin 23-07-2021
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('Add Balance', 'AddBalance', 'partnerTransCategory', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('Reverse Balance', 'ReverseBalance', 'partnerTransCategory', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('Commission', 'Commission', 'partnerTransCategory', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('Renew Balance', 'RenewBalance', 'partnerTransCategory', 'Active');

#Jaymin 13-08-2021
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('Status Change', 'StatusChange', 'subscriberUpdateOperation', 'Active');
INSERT INTO tblcommonlist (list_text, list_value, list_type, status)VALUES ('Plan Change', 'PlanChange', 'subscriberUpdateOperation', 'Active');

#Utsav 20-08-2021
#not for fire in db if exist
INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES('New', 'New', 'planPurchaseType', 'Active');

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES('Renew', 'Renew', 'planPurchaseType', 'Active');

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES('Upgrade', 'Upgrade', 'planPurchaseType', 'Active');

#Need to fire
INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES('Addon', 'Addon', 'planPurchaseType', 'Active');

#Mohit 23-08-2021 (Already Fired)
INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES('Employee', 'employee', 'auditFor', 'Active');

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES('Partner', 'partner', 'auditFor', 'Active');

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES('Customer', 'customer', 'auditFor', 'Active');

INSERT INTO tblcommonlist
(list_text, list_value, list_type, status)
VALUES('Payment Gateway', 'paymentgateway', 'auditFor', 'Active');
