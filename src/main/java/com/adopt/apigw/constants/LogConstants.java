package com.adopt.apigw.constants;

public class LogConstants {
    public static final String  REQUEST_FROM = "Request from : ";

    public static final String  REQUEST_FOR = " , Request for : ";

    public static final String  REQUEST_TO_CREATE = " , Request to create";

    public static final String  REQUEST_TO_FETCH = " , Request to fetch";

    public static final String  REQUEST_TO_UPDATE = " , Request to update";

    public static final String REQUEST_TO_DELETE = " , Request to delete";

    public static final String  REQUEST_BY = " , Request by : ";

    public static final String  LOG_STATUS = " , STATUS : ";

    public static final String  LOG_SUCCESS = "SUCCESS ";

    public static final String  LOG_FAILED = "FAILED ";

    public static final String  LOG_NOT_FOUND = "NOT FOUND ";

    public static final String  LOG_ERROR = ", ERROR : ";

    public static final String  LOG_INFO = "INFO : ";

    public static final String  LOG_UNAUTHORIZED = "UNAUTHORIZED ";

    public static final String  LOG_STATUS_CODE = ", STATUS CODE : ";

    public  static  final String LOG_NOT_CREATED = "NOT CREATED";

    public static final String  LOG_NO_RECORD_FOUND = "No records found !!";

    public static final String LOG_BY_NAME = " Entity Name : ";
    public static final String TRACE_ID = "traceId";
    public static final String USERNAME_ALREADY_EXIST = "Username already exists :";
    public static final String USERNAME_NOT_FOUND = "Username Not exists :";

    public interface HeaderConstants {
        public static final String  REQUEST_FROM = "requestFrom";
        public static final String  REQUEST_TYPE = "type";
        public static final String  REQUEST_USERNAME = "userName";
        public static final String  REQUEST_SPANID = "spanId";
        public static final String  REQUEST_CREATE = "Create";
        public static final String  REQUEST_FETCH = "Fetch";
    }

}
