package com.adopt.apigw.modules.subscriber.queryScript;

public class IpExpiryScript {
    public static String getIpExpiredUser(String endDate) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT t.username,t4.ip_address,t2.enddate,t.mobile,t.email \n" +
                "                FROM  tblcustomers t inner join tblcustchargedtls t2 on t2.custid = t.custid \n" +
                "                INNER JOIN tblipallocationdtls t3 on t2.purchase_entity_id = t3.id \n" +
                "                INNER JOIN tblippooldtls t4 on t4.allocated_id  = t3.id \n" +
                "                inner join tblcharges t5 on t5.CHARGEID  = t2.chargeid\n" +
                "                where t.is_deleted = 0 and t5.chargecategory = 'IP' and Date(t2.enddate) = Date('" + endDate + "');");
        return builder.toString();
    }

    public static String getPlanExpiredUser(String expiryDate1, String expiryDate2) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT t.username,t3.NAME as planname,t.mobile,t.email FROM  tblcustomers t \n" +
                "inner join tblcustpackagerel t2 on t2.custid = t.custid \n" +
                "INNER join tblmpostpaidplan t3 on t2.planid  = t3.POSTPAIDPLANID \n" +
                "where t.is_deleted = 0 and t2.expirydate BETWEEN  '" + expiryDate1 + "' and '" + expiryDate2 + "';");
        return builder.toString();
    }

    public static String getIpDetails(Long custId) {
        StringBuilder builder = new StringBuilder();
        builder.append("select pooldtls.ip_address 'ipAddress' \n" +
                ", custcharges.startdate 'ipPurchaseDate' \n" +
                ", custcharges.enddate 'ipExpiredDate'\n" +
                "from tblcustchargedtls custcharges\n" +
                "inner join tblcharges charges  on custcharges.chargeid = charges.CHARGEID and charges.chargecategory = 'IP' \n" +
                "inner join tblippooldtls pooldtls on custcharges.ippooldtlsid =  pooldtls.pool_details_id\n" +
                "where custcharges.custid = ").append(custId + "  and custcharges.enddate >= sysdate() " +
                "and custcharges.startdate <= sysdate() and is_reversed = 0  ");
        return builder.toString();
    }
}
