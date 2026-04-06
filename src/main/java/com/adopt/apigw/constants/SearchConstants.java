package com.adopt.apigw.constants;

import lombok.Data;

@Data
public class SearchConstants {

    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String NOT = "NOT";
    public static final String IN = "IN";

    public static final String EQUAL_TO = "equalto";
    public static final String NOT_EQUAL = "notequal";
    public static final String NULL = "null";
    public static final String NOT_NULL = "notnull";
    public static final String CONTAINS = "contains";
    public static final String STARTS_WITH = "startswith";
    public static final String GREATER_THAN = "greaterthan";
    public static final String GREATER_THAN_EQUAL_TO = "greaterthaneq";
    public static final String LESS_THAN = "lessthan";
    public static final String LESS_THAN_EQUAL_TO = "lessthaneq";

    public static final String ANY = "any";

    //Customer Search Fields
    public static final String CUST_NAME = "name";
    public static final String CUST_USERNAME = "username";

    public static final String CUST_USERNAME_EQUAL = "usernameequalto";
    public static final String CUST_FULLNAME = "fullname";
    public static final String CUST_EMAIL = "email";
    public static final String CUST_MOBILE = "mobile";
    public static final String CUST_ACCT_NUMBER = "accountNumber";
    public static final String CUST_STATUS = "status";
    public static final String CUST_OUTSTANDING = "outstanding";
    public static final String CUST_PLAN_NAME = "plan";
    public static final String PLAN_EXPIRY_DATE = "expirydate";
    public static final String CUST_SERVICE_AREA = "serviceArea";
    public static final String SERVICE_AREA_NETWORK = "serviceNetwork";
    public static final String CUST_PORT = "port";
    public static final String CUST_SLOT = "slot";
    public static final String CUST_SALES_REPRESENTATIVE = "salesRepresentative";
    public static final String CUST_MAC_ADDRESS = "macaddress";
    public static  final String CUST_PARENT = "custparent";
    public static final String CUST_CHILD = "custchild";
    public static final String CUST_INDIVIDUAL ="custindividual";
    public static  final String CUST_TYPE="custtype";
    public static  final String PREPAID ="prepaid";
    public static final String POSTPAID ="postpaid";

    public static final String WARD ="ward";

    //Case Search Fields
    public static final String PORT = "port";
    public static final String SLOT = "slot";
    public static final String OLT = "olt";
    public static final String ONU = "onu";
    public static final String SERVICE_AREA = "servicearea";
    public static final String CURRENT_ASSIGNEE = "currentAssigneeName";
    public static final String REASON = "reason";
    public static final String PRIORITY = "priority";
    public static final String CASE_ORIGIN = "origin";
    public static final String CASE_TYPE = "casetype";
    public static final String CASE_STATUS = "caseStatus";
    public static final String CASE_NUMBER = "caseNumber";

    public static final String PARTNER_NAME = "partnerName";
    public static final String CIRCUIT_NAME = "circuitName";
    public static final String BRANCH_NAME = "branchName";
    public static final String PLAN_GROUP = "planGroup";
    public static final String CAF_STATUS = "cafStatus";
    public static final String CAF_CREATED_DATE = "cafCreatedDate";
    public static final String CAF_CREATED_BY = "createbyname";
    public static final String CAF_Activation_DATE = "firstactivationdate";
    public static final String CAF_ACTIVATION_BY ="activationbyname";
    public static final String CAF_NO = "cafNo";
    public static final String BILL_TO = "billTo";
    public static final String CURRENT_ASSIGNED_TEAM = "currentAssignedTeam";
    public static final String CURRENT_ASSIGNED_TEAM_AND_STAFF = "currentAssignedTeamAndStaff";
    public static final String STATIC_IP = "staticIp";
    public static final String FRAMED_IP = "framedIpAddress";
    public static final String SEARCH_CUST_BY_INVENTORY_SERIAL = "inventorySerial";
    public static final String SEARCH_CUST_BY_PLAN_EXPIRY = "expiryDate";
    public static final String CHARGE_NAME = "name";
    public static final String CHARGE_CATEGORY = "chargecategory";
    public static final String CHARGE_TYPE = "chargetype";

    public static final String PLAN_STATUS ="planstatus";

    public static final String PLAN_VALIDITY ="planvalidity";

    public static final String PLAN_PRICE ="planprice";

    public static final String PLAN_CREATEDBY ="plancreatedby";

    public static final String PLAN_STARTDATE ="planstartdate";

    public static final String PLAN_ENDDATE ="planenddate";

    public static final String PLAN_CREATEDATEFROM ="plancreatedatefrom";

    public static final String PLAN_SERVICEAREA ="planservicearea";

    public static final String PLAN_BRANCH ="planbranch";

    public static final String PLAN_FRANCHISE ="planfranchise";

    public static final String PLAN_STATUS_ACTIVE ="planstatusactive";

    public static final String PLAN_STATUS_NEWACTIVATION ="planstatusnewactivation";

    public static final String PLAN_STATUS_EXPIRED ="planstatusexpired";

    public static final String PLAN_STATUS_REJECTED ="planstatusrejected";

    public static final String PLAN_STATUS_INACTIVE ="planstatusinactive";

    public static final String PLAN_DATE ="plandate";

    public static final String PLAN_CREATEDATETO ="plancreatedateto";

    public  static final String PLANCREATEDATE ="plancreateddate";

    public  static final String PAN ="pan";
    public  static final String ISP_NAME = "ispName";
    public static final String SUBSCRIPTION_MODE = "subscriptionMode";
    public static final String PARAM_1 = "param1";
    public static final String PARAM_2 = "param2";
    public static final String PARAM_3 = "param3";
    public static final String PARAM_4 = "param4";
    public static final String SERVICE = "service";
    public static final String LOCATION_NAME = "locationName";
    public static final String SERVICE_STATUS = "serviceStatus";

}
