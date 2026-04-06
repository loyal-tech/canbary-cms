package com.adopt.apigw.modules.reports.recentrenewal.queryscript;

import lombok.Data;

@Data
public class RecentRenewalReportQueryScript {

    public static final String COMMON_P1 = "select cust.custid 'custid'\n" +
            ", cust.accountnumber 'acctnumber'\n" +
            ", cust.username 'username'\n" +
            ", cust.firstname 'name'\n" +
            ", cust.cstatus 'status' \n" +
            ", if(cust.gst is null,'-',cust.gst) 'gst'\n" +
            ", if(cust.pan is null,'-',cust.pan) 'pan' \n" +
            ", concat(if (addressrel.ADDRESS1 is not null ,addressrel.ADDRESS1, \"\") ,\" \",if(addressrel.landmark is not null,addressrel.landmark,\"\"))  'address'\t\n" +
            ", area.name 'area'\n" +
            ", city.NAME 'city'\n" +
            ", state.NAME 'state'\n" +
            ", cust.email 'email'\n" +
            ", creditdoc.PAYMODE 'paymentmethod'\n" +
            ", pin.pincode 'zip'\n" +
            ", cust.mobile 'mobile'\n" +
            ", plan.DISPLAYNAME 'planname'\n" +
            ", if(packrel.offer_price is not null , packrel.offer_price , 0) + if(packrel.tax_amount is not null , packrel.tax_amount , 0) 'planprice'\n" +
            ", if(packrel.wallet_bal_used is not null ,packrel.wallet_bal_used,0) 'walletbalused'\n" +
            ", pool.pool_name 'poolname'\n" +
            ", if(creditdoc.PAYMENTDATE is null,'-',date_format(creditdoc.PAYMENTDATE ,'%d-%m-%Y %h:%i:%s %p')) 'paymentdate'\n" +
            ", if(packrel.createdate is null,'-',date_format(packrel.createdate ,'%d-%m-%Y %h:%i:%s %p')) 'creationdate'\n" +
            ", if(packrel.startdate is null,'-',date_format(packrel.startdate,'%d-%m-%Y %h:%i:%s %p')) 'activationdate'\n" +
            ", if(packrel.expirydate is null,'-',date_format(packrel.expirydate,'%d-%m-%Y %h:%i:%s %p')) 'expirydate'\n" +
            ", if(quotadtl.timequotaunit = 'Minute',if(quotadtl.timetotalquota = -1,-1,quotadtl.timetotalquota/60),quotadtl.timetotalquota) 'allotedtime'\n" +
            ", if(quotadtl.quotaunit = 'GB',if(quotadtl.totalquota = -1,-1,quotadtl.totalquota * 1024),quotadtl.totalquota) 'allotedtotaldatatransfer'\n" +
            ", if(quotadtl.timequotaunit = 'Minute',if(quotadtl.timetotalquota = -1,-1,quotadtl.timequotaused/60),quotadtl.timequotaused) 'usedtime'\n" +
            ", if(quotadtl.quotaunit = 'GB',if(quotadtl.totalquota = -1,-1,quotadtl.usedquota * 1024),quotadtl.totalquota) 'useddatatransfer' \n" +
            ", partner.PARTNERNAME 'partner'\n" +
            ", packrel.purchase_type 'renewaltype'\n" +
            ", if(staff.partnerid = 1,'Admin','Partner') 'createdfrom'\n" +
            ", packrel.createbyname 'rechargeby' \n" +
            ", if(creditdoc.AMOUNT is not null,creditdoc.AMOUNT ,0) 'paidamount'\n" +
            ", packrel.purchase_from 'purchasefrom'\n";

    public static final String COMMON_P2 =  "from tblcustomers cust\n" +
            "inner join tblmsubscriberaddressrel addressrel\n" +
            "on addressrel.SUBSCRIBERID = cust.custid and addressrel.ADDRESSTYPE = lower('present')\n" +
            "inner join tblmcountry country\n" +
            "on country.COUNTRYID = addressrel.COUNTRYID \n" +
            "inner join tblmstate state  \n" +
            "on state.STATEID = addressrel.STATEID \n" +
            "inner join tblmcity city\n" +
            "on city.CITYID = addressrel.CITYID \n" +
            "inner join tblmpincode pin\n" +
            "on pin.pincodeid = addressrel.PINCODEID \n" +
            "inner join tblmarea area\n" +
            "on area.areaid = addressrel.AREAID\n" +
            "inner join tblippool pool\n" +
            "on pool.pool_id = cust.defaultpoolid \n" +
            "inner join tblcustpackagerel packrel\n" +
            "on packrel.custid  = cust.custid and packrel.createdate between";

    public static final String COMMON_P3 = "inner join tblcustquotadtls quotadtl\n" +
            "on quotadtl.custpackageid = packrel.custpackageid  \n" +
            "inner join tblmpostpaidplan plan\n" +
            "on plan.POSTPAIDPLANID = packrel.planid\n" +
            "inner join tblpartners partner\n" +
            "on cust.partnerid = partner.PARTNERID\n" +
            "left join tblstaffuser staff\n" +
            "on staff.staffid = packrel.createdbystaffid\n" +
            "left join tbltcreditdoc creditdoc\n" +
            "on creditdoc.CREDITDOCID = packrel.creditdocid\n";

    public static final String COMMON_P4 = "left join tbl_purchase_details purchase\n" +
            "on purchase.purchaseid = packrel.online_purchase_id \n";

    public static final String ONLINE_PARAM = ", if(purchase.transid is null , '-'  , purchase.transid) 'transid'\n" +
            ", if(purchase.pgtransid is null , '-' , purchase.pgtransid) 'pgtransid'\n" +
            ", if(purchase.purchase_status is null , '-' , purchase.purchase_status) 'purchasestatus'\n" +
            ", if(purchase.paymentstatus is null , '-' , purchase.paymentstatus) 'paymentstatus' \n";

    public static final String AND = " and ";
    public static final String PARTNERID = " partner.partnerid = ";
    public static final String PAYMENT_STATUS = " purchase.paymentstatus = ";
    public static final String PURCHASE_STATUS = " purchase.purchase_status = ";
    public static final String CUST_ID = " cust.custid = ";

    public static final String PURCHASE_TYPE_ONLINE = "online";
    public static final String PURCHASE_TYPE_OFFLINE = "offline";

    public static final String LIMIT = " LIMIT ";
    public static final String OFFSET = " OFFSET ";

    public static final String ORDER_BY = " order by packrel.createdate desc ";

    public static final String PLAN_PRICE = " and if(packrel.offer_price is not null , packrel.offer_price , 0) + if(packrel.tax_amount is not null , packrel.tax_amount , 0) ";
    public static final String EXACTLY = "Exactly";
    public static final String EXACTLY_OPERATOR = " = ";
    public static final String BETWEEN = "Between";
    public static final String GREATER_THAN = "GreaterThan";
    public static final String GREATER_THAN_OPERATOR = " > ";
    public static final String LESS_THAN = "LessThan";
    public static final String LESS_THAN_OPERATOR = " < ";
    public static final String WHERE = " WHERE ";

    public static final String CONCAT = "  concat ";
}
