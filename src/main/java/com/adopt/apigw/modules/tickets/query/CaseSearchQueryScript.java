package com.adopt.apigw.modules.tickets.query;

import lombok.Data;

@Data
public class CaseSearchQueryScript {

    //Query
    public static final String COMMON_QUERY = " select t.case_id 'id' from tblcases t ";
    public static final String CASE_IS_DELETE = " t.is_delete = 0 ";
    public static final String CASE_ORIGIN = " t.case_origin ";
    public static final String CASE_PRIORITY = " t.priority ";
    public static final String CASE_STATUS = " t.case_status ";
    public static final String CASE_NUMBER = " t.case_number ";
    public static final String CASE_TYPE = " t.case_type ";
    public static final String CASE_REASON = " reason.name ";
    public static final String CUST_JOIN = " inner join tblcustomers cust on cust.custid = t.case_for_id  ";
    public static final String STAFF_JOIN = " inner join tblstaffuser staff on staff.staffid = t.current_assignee_id  ";
    public static final String REASON_JOIN = " inner join tblcasereasons reason on reason.reason_id = t.reason_id  ";
    public static final String STAFF_FNAME = " staff.firstname ";
    public static final String STAFF_LNAME = " staff.lastname ";
    public static final String WHERE = " where ";
    public static final String EXISTS_BY_SERVICE_AREA = " exists( select * from tblcustomers t2 \n" +
            "inner join tblservicearea t3 on t2.servicearea_id = t3.service_area_id \n" +
            "where t3.name ";
    public static final String EXISTS_BY_NETWORK_DEVICE = " exists ( select * from tblcustomers t2 \n" +
            "inner join tblnetworkdevices t4 on t2.network_device_id = t2.network_device_id \n" +
            "where t4.name = ";
    public static final String EXISTS_BY_SLOT = " exists( select * from tblcustomers t2 \n" +
            "inner join tbloltslots t5 on t5.slotid = t2.oltslotid \n" +
            "where t5.slotname ";
    public static final String EXISTS_BY_PORT = " exists( select * from tblcustomers t2 \n" +
            "inner join tbloltportdetails t6 on t6.portid = t2.oltslotid \n" +
            "where t6.portname ";
    public static final String EXISTS_BY_ONU = " exists( select * from tblcustomers t2  \n" +
            "where t2.onuid " ;
    public static final String CUST_CONDITION = " and t2.custid = t.case_for_id  ";


    //Operator
    public static final String AND = " and ";
    public static final String OR = " or ";
    public static final String LIKE = " like ";
    public static final String EQUAL_TO = " = ";
    public static final String NOT_EQUAL_TO = " != ";
    public static final String GREATER_THAN = " > ";
    public static final String LESS_THAN = " < ";
    public static final String GREATER_THAN_EQUAL_TO = " >= ";
    public static final String LESS_THAN_EQUAL_TO = " <= ";

}
