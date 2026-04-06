package com.adopt.apigw.modules.purchaseDetails.queryScript;

import lombok.Data;

@Data
public class PurchaseHistoryQueryScript {

    public static final String COMMON_QUERY = " select tpd.purchaseid 'id' from tbl_purchase_details tpd ";
    public static final String ORDER_JOIN = " inner join tbl_order_details tod on tod.orderid = tpd.orderid ";
    public static final String ORDER_EXISTS = " exists ( select * from tbl_order_details tod where tpd.orderid = tod.orderid " +
            "and tod.ordertype = '";
    public static final String PG_JOIN = " inner join tbl_payment_gateway tpg on tpg.pgid = tpd.pgid ";
    public static final String CUST_JOIN = " inner join tblcustomers cust on cust.partnerid =  ";
    public static final String PURCHASE_DATE_BETWEEN = " tpd.purchasedate between ";
    public static final String PAYMENT_STATUS = " tpd.paymentstatus ";
    public static final String PURCHASE_STATUS = " tpd.purchase_status ";
    public static final String PG_ID = " tpd.pgid ";
    public static final String CUST_ID = " tpd.custid ";
    public static final String PARTNER_ID = " tpd.partnerid ";
    public static final String IS_NULL = " is null ";
    public static final String ORDER_TYPE = " tod.ordertype ";
    public static final String WHERE = " WHERE ";
    public static final String AND = " AND ";
    public static final String IS_DELETED = " tpd.is_deleted = 0 ";
    public static final String CONCAT = " concat ";
}
