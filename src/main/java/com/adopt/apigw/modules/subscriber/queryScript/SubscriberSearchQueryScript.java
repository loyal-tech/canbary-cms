package com.adopt.apigw.modules.subscriber.queryScript;

import lombok.Data;

@Data
public class SubscriberSearchQueryScript {

    public static final String COMMON_QUERY = "select cust.custid 'id' from tblcustomers cust ";
    public static final String PACK_REL_INNER_JOIN = " inner join tblcustpackagerel packrel on packrel.custid = cust.custid ";
    public static final String PLAN_INNER_JOIN = " inner join tblmpostpaidplan plan on plan.POSTPAIDPLANID = packrel.planid ";
    public static final String CUST_LEDGER_INNER_JOIN = " inner join tblmcustledger custledger on custledger.CUSTID = cust.custid ";
    public static final String CUST_OUTSTANDING_CONDITION = " (custledger.TOTALDUE  - custledger.TOTALPAID) ";
    public static final String ACTIVE_PLAN_QUERY = " exists ( " +
            " select * from tblcustpackagerel packrel \n" +
            " INNER join tblmpostpaidplan plan on plan.POSTPAIDPLANID = packrel.planid \n" +
            " and lower(packrel.service) = 'data'\n" +
            " and packrel.custid = cust.custid \n" +
            " and packrel.STARTDATE <= sysdate() \n" +
            " and packrel.enddate is null \n" +
            " and packrel.expirydate >= sysdate() \n";

    public static final String AND = " and ";
    public static final String OR = " or ";
    public static final String LIKE = " like ";
    public static final String CUST_USERNAME = " cust.username ";
    public static final String CUST_FIRSTNAME = " cust.firstname ";
    public static final String CUST_SALES_REP = " cust.salesrepid ";
    public static final String CUST_LASTNAME = " cust.lastname ";
    public static final String CUST_MOBILE = " cust.mobile ";
    public static final String CUST_EMAIL = " cust.email ";
    public static final String CUST_ACCT_NUMBER = " cust.accountnumber ";
    public static final String CUST_CSTATUS = " cust.cstatus ";
    public static final String PLAN_NAME = " plan.name ";
    public static final String CUST_SERVICEAREA = " cust.servicearea_id ";
    public static final String CUST_NETWORK_DEVICE = " cust.network_device_id ";
    public static final String CUST_SLOT = " cust.oltslotid ";
    public static final String CUST_PORT = " cust.oltportid ";
    public static final String WHERE = " where ";

    //Operator
    public static final String EQUAL_TO = " = ";
    public static final String IN = " IN ";
    public static final String NOT_EQUAL_TO = " != ";
    public static final String GREATER_THAN = " > ";
    public static final String LESS_THAN = " < ";
    public static final String GREATER_THAN_EQUAL_TO = " >= ";
    public static final String LESS_THAN_EQUAL_TO = " <= ";

}
