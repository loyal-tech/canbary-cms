package com.adopt.apigw.modules.subscriber.queryScript;

public class PlanQueryScript {

    public static String getActivePlans(Integer custId) {
        StringBuilder builder = new StringBuilder();
        builder.append("select packrel.service 'service'\n" +
                ", cust.custid 'custId'\n" +
                ", packrel.planid 'planId'\n" +
                ", packrel.is_hold 'isHold'\n" +
                ", packrel.is_void 'isVoid'\n" +
                ", packrel.debitdocid 'debitdocid'\n" +
                ", packrel.custservicemappingid 'custPlanMapppingId' \n" +
                ", packrel.is_delete 'isdeleteforVoid' \n" +
                ", plan.NAME 'planName' \n" +
                ", plan.serviceid 'serviceId'\n" +
                ", packrel.custpackageid 'planmapid' \n" +
                ", quota.quotatype 'quotaType'\n" +
                ", quota.usedquota 'volUsedQuota'\n" +
                ", if(quota.totalquota = -1,'Unlimited',quota.totalquota)'volTotalQuota'\n" +
                ", quota.quotaunit 'volQuotaUnit'\n" +
                ", quota.timequotaused 'timeUsedQuota'\n" +
                ", if(quota.timetotalquota = -1,'Unlimited',quota.timetotalquota) 'timeTotalQuota'\n" +
                ", quota.timequotaunit 'timeQuotaUnit'\n" +
                ", qos.name 'qosPolicyName'\n" +
                ", qos.id 'qosPolicyId'\n" +
                ", qos.qosspeed 'qosSpeed'\n" +
                ", packrel.startdate 'startDate'\n" +
                ", packrel.startdate 'startDateString'\n" +
                ", packrel.expirydate 'expiryDate'\n" +
                ", packrel.expirydate 'expiryDateString'\n" +
                ", packrel.enddate 'endDate'\n" +
                ", packrel.cust_plan_status 'custPlanStatus'\n" +
                ", packrel.istrialplan 'istrialplan'\n" +
                ", packrel.grace_days 'promiseToPayDays'\n" +
                ", packrel.promise_to_pay_startdate 'promiseToPayStartDate'\n" +
                ", packrel.promise_to_pay_enddate 'promiseToPayEndDate'\n" +
                ", packrel.promisetopay_renew_count 'promiseToPayCount'\n" +
                ", packrel.service_start_remarks 'serviceStartRemarks'\n" +
                ", packrel.service_hold_remarks 'serviceHoldRemarks'\n" +
                ", packrel.service_start_by 'serviceStartBy'\n" +
                ", packrel.service_hold_by 'serviceHoldBy'\n" +
                ", cust.isinvoicestop 'isinvoicestop'\n" +
                ", packrel.isinvoicestop 'isinvoicestopinpackrel'\n" +
                ", plan.plangroup 'plangroup' \n" +
                ", packrel.plangroupid 'plangroupid' \n" +
                ", packrel.stop_service_date 'stopServiceDate' \n" +
                ", plan.maxconcurrentsession 'maxsession' \n" +
                ", packrel.createbyname 'createbyname'\n" +
//                ", packrel.createdate 'createdate'\n" +
                ", plan.validity 'validity'\n" +
                ", ser.is_qosv 'is_qosv'\n" +
                ", ser.is_dtv 'is_dtv',\n" +
                "case \n" +
                "when (date(packrel.startdate) > date(sysdate())) and plan.plangroup IN ('Renew','Volume Booster','Registration','Registration and Renewal','Bandwidthbooster','DTV Addon') \n" +
                "then 'FUTURE'\n" +
                "when date(packrel.startdate) <= sysdate() \n" +
                "and (packrel.enddate IS NULL OR  packrel.enddate >= sysdate()) \n" +
                "and timediff(packrel.enddate , sysdate()) >=0 and plan.plangroup IN ('Renew','Volume Booster','Registration','Registration and Renewal','Bandwidthbooster','DTV Addon')  \n" +
                "then 'ACTIVE'\n" +
                "when packrel.cust_plan_status in ('Suspend','Terminate', 'Disable','InGrace','Stop','SUSPEND','TERMINATE', 'DISABLE', 'STOP','INGRACE') then packrel.cust_plan_status\n" +
                "else 'EXPIRED'\n" +
                "end as planstage\n" +
                "from tblcustomers cust\n" +
                "inner join tblcustpackagerel packrel on cust.custid = packrel.custid  \n" +
                "inner join tblmpostpaidplan plan on plan.POSTPAIDPLANID = packrel.planid  \n" +
                "left join tblcustquotadtls quota on quota.custpackageid = packrel.custpackageid\n" +
                "left join tbl_qos_policy qos on qos.id = packrel.qospolicyid\n" +
                "left join tblmservices ser on ser.serviceid = plan.serviceid\n" +
                "where cust.custid = " + custId);
        return builder.toString();
    }

    public static String getRefundableOrders() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT p.purchaseid \n" +
                ",p.orderid\n" +
                ",p.custid \n" +
                ",o.balanced_used\n" +
                ",o.ledger_details_id \n" +
                "FROM tbl_purchase_details p join tbl_order_details o \n" +
                "ON p.orderid = o.orderid \n" +
                "WHERE p.paymentstatus = 'Pending' \n" +
                "and p.purchase_status = 'Pending' \n" +
                "and o.is_balance_used = TRUE\n" +
                "and o.balanced_used > 0 \n" +
                "and o.is_settled = false;");
        return builder.toString();
    }

    public static String getCaseCount(Integer custid) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select case_for_id 'caseId' ,count(*) totalcases\n" +
                ",sum(case  when tc.case_status not like 'Closed' then 1 else 0\n" +
                "end) as opencase\n" +
                "from tblcases tc\n" +
                "where \n" +
                "tc.case_for_id = ").append(custid + " and tc.case_for = 'Customer'\n" +
                "and tc.is_delete is false\n" +
                "group by tc.case_for_id");
        return stringBuilder.toString();
    }

    public static String getChargeDetailsByCustomer(Integer custId) {
        StringBuilder builder = new StringBuilder();
        builder.append("select t2.CHARGENAME 'chargeName' \n" +
                ",(if(t.price is not null,t.price ,0) + if(t.taxamount is not null,t.taxamount,0)) 'chargeAmount'\n" +
                ", t.remarks 'remarks'\n" +
                ", t.CREATEDATE 'createdDate'\n" +
                ", t.charge_date 'chargeDate'\n" +
                ", t.enddate 'expiryDate'\n" +
                ", if(t3.debitdocumentid is not null , t3.debitdocumentnumber , '-' ) 'debitDocNum'\n" +
                ", t3.debitdocumentid 'debitdocid'\n" +
                ", t.cstchargeid 'custchargeid'\n" +
                "from tblcustchargedtls t \n" +
                "inner join tblcharges t2 \n" +
                "on t2.CHARGEID = t.chargeid \n" +
                "left join tbltdebitdocument t3 \n" +
                "on t3.cstchargeid = t.cstchargeid\n" +
                "where t.custid = ").append(custId).append(" order by t.cstchargeid desc");
        return builder.toString();
    }

}
