package com.adopt.apigw.constants;

import lombok.Data;

@Data
public class AuditLogConstants {

    public static final String AUDIT_FOR_EMPLOYEE = "employee";
    public static final String AUDIT_FOR_PARTNER = "partner";
    public static final String AUDIT_FOR_CUSTOMER = "customer";
    public static final String AUDIT_FOR_PAYMENT_GATEWAY = "paymentgateway";
    public static final String OPERATION_INSERT = "insert";
    public static final String OPERATION_DELETE  = "delete";
    public static final String OPERATION_VIEW  = "view";
    public static final String OPERATION_UPDATE  = "update";


    //constant for audit

    public static final String USER_ID = "user_id";
    public static final String IP_ADDRESS = "ip_address";
    public static final String TEAMS = "Teams";
    public static final String MVNOID = "mvnoId";
    public static final String DELETE = "Delete";
    public static final String CHANGETYPE = "changeType";
    public static final String CREATE = "Create";
    public static final String UPDATE = "Update";
    public static final String LISTCHANGE = "ListChange";
    public static final String IS_DELETE = "isDelete";
    public static final String TOTAL_RECORDS = "totalRecords";
    public static final String AUDITTRAILS = "auditTrails";
    public static final String INITIAL_VALUE_CHANGE = "InitialValueChange";
    public static final String VALUE_CHANGE = "ValueChange";
    public static final String REFERENCE_CHANGE="ReferenceChange";
    public static final String IS_DELETED = "isDeleted";
    public static final String TERMINALVALUE_CHANGE= "TerminalValueChange";
    public static final String LEFT = "left";
    public static final String POSTPAID_PLAN_VIEW = "POSTPAID PLAN VIEW";
    public static final String PostpaidPlan = "PostpaidPlan";




}
