package com.adopt.apigw.modules.auditLog.queryScript;

import lombok.Data;

@Data
public class AuditSearchQueryScript {

    public static final String COMMON_QUERY = " select tal.audit_id 'id' from tblauditlog tal  ";
    public static final String WHERE = " WHERE ";
    public static final String AND = " AND ";
    public static final String LIKE =" LIKE ";
    public static final String AUDIT_DATE = " tal.auditdate ";
    public static final String MODULE = " tal.module ";
    public static final String BEFORE = " <= ";
    public static final String BETWEEN = " between ";
    public static final String AFTER = " >= ";
    public static final String EMP_ID = " tal.employee_id ";
    public static final String USER_ID = " tal.user_id ";
    public static final String PARTNER_ID = " tal.partner_id ";
    public static final String IS_NOT_NULL = " IS NOT NULL ";
    public static final String IS_NULL = " IS NULL ";
    public static final String OPERATION = " tal.operation ";
    public static final String EMPLOYEENAME = "tal.employee_name";
    public static final String USERNAME = "tal.user_name";



}
