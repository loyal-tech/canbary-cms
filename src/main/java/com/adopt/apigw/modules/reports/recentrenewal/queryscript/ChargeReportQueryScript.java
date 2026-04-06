package com.adopt.apigw.modules.reports.recentrenewal.queryscript;

import lombok.Data;

@Data
public class ChargeReportQueryScript {

    public static final String COMMON_P1 = "select cust.username 'username'\n" +
            ", cust.mobile 'mobile'\n" +
            ", cust.email 'email'\n" +
            ", cust.accountnumber 'acctnumber'\n" +
            ", if(cust.gst is null,'-',cust.gst) 'gst'\n" +
            ", if(cust.pan is null,'-',cust.pan) 'pan' \n" +
            ", cust.firstname 'name'\n" +
            ", cust.cstatus 'status'\n" +
            ", charge.CHARGENAME 'chargename'\n" +
            ", charge.chargecategory 'category'\n" +
            ", custcharge.cstchargeid 'custchargeid'\n" +
            ", custcharge.chargetype 'chargetype'\n" +
            ", (if(custcharge.price is not null,custcharge.price,0) + if(custcharge.taxamount is not null,custcharge.taxamount,0)) 'chargeprice'\n" +
            ", if(custcharge.charge_date is null,'-',date_format(custcharge.charge_date,'%d-%m-%Y %h:%i:%s %p')) 'chargedate' \n" +
            ", if(pool.pool_name is null,'-',pool.pool_name) 'poolname'\n" +
            ", if(pooldtls.ip_address is null,'-',pooldtls.ip_address) 'ip' \n" +
            ", if(custcharge.validity is null,0,custcharge.validity) 'validity' \n" +
            ", if(custcharge.startdate is null,'-',date_format(custcharge.startdate,'%d-%m-%Y %h:%i:%S %p')) 'startdate'\n" +
            ", if(custcharge.enddate is null,'-',date_format(custcharge.enddate,'%d-%m-%Y %h:%i:%s %p')) 'enddate' \n" +
            ", if(custcharge.is_reversed,'Yes','No') 'reversed' \n" +
            ", if(custcharge.rev_date is null,'-',date_format(custcharge.rev_date,'%d-%m-%Y %h:%i:%s %p')) 'revdate' \n" +
            ", if(custcharge.rev_amt is null,0,custcharge.rev_amt) 'reversedamount' \n" +
            ", custcharge.createbyname 'createbyname' \n" +
            " from tblcustchargedtls custcharge\n" +
            "inner join tblcharges charge\n" +
            "on charge.CHARGEID = custcharge.chargeid \n" +
            "inner join tblcustomers cust \n" +
            "on cust.custid = custcharge.custid \n" +
            "left join tblippooldtls pooldtls\n" +
            "on pooldtls.pool_details_id = custcharge.ippooldtlsid \n" +
            "left join tblippool pool\n" +
            "on pool.pool_id = pooldtls.pool_id  \n";

    public static final String AND = " AND ";
    public static final String CHARGE_TYPE_QUERY = " custcharge.chargetype = ";
    public static final String CHARGE_ID_QUERY = " charge.CHARGEID = ";
    public static final String CHARGE_CATEGORY_QUERY = " charge.chargecategory = ";
    public static final String CUST_ID_QUERY = " custcharge.custid = ";
    public static final String IS_REVERSED_QUERY = " custcharge.is_reversed  =  ";

    public static final String LIMIT = "LIMIT";
    public static final String OFFSET = "OFFSET";

    public static final String REVERSED_ALL = "All";
    public static final String REVERSED_YES = "Yes";
    public static final String REVERSED_NO = "No";

    public static final String CHARGE_CATEGORY = "chargecatgory";
    public static final String CHARGE_TYPE = "chargetype";

    public static final String CHARGE_DATE = " custcharge.CREATEDATE between ";
    public static final String WHERE = " WHERE ";
    public static final String ORDER_BY = " order by custcharge.CREATEDATE desc ";
    public static final String CHARGE_PRICE = " (if(custcharge.price is not null,custcharge.price,0) + if(custcharge.taxamount is not null,custcharge.taxamount,0))  ";
    public static final String EXACTLY = "Exactly";
    public static final String EXACTLY_OPERATOR = " = ";
    public static final String BETWEEN = "Between";
    public static final String GREATER_THAN = "GreaterThan";
    public static final String GREATER_THAN_OPERATOR = " > ";
    public static final String LESS_THAN = "LessThan";
    public static final String LESS_THAN_OPERATOR = " < ";

    public static final String CONCAT = "  concat ";
}
