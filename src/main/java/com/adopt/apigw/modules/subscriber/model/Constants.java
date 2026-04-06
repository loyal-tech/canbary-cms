package com.adopt.apigw.modules.subscriber.model;

public class Constants {
    public static final String MB = "mb";
    public static final String GB = "gb";
    public static final String HOUR = "hour";
    public static final String MINUTES = "minute";
    public static final String ADD = "add";
    public static final String REPLACE = "replace";
    public static final String ACTIVE = "Active";
    public static final String INACTIVE = "InActive";
    public static final String SUSPEND = "Suspend";
    public static final String TERMINATE = "Terminate";
    public static final String PENDING = "pending";
    public static final String APPROVED = "approved";
    public static final String NEW_ACTIVE = "NewActivation";
    public static final String ACTIVE_DB = "Active";
    public static final String RESOLVED = "Resolved";
    public static final String ASSIGNED = "Assigned";
    public static final String ADVANCE = "advance";
    public static final String DAYS = "Day";
    public static final String MONTH = "Month";
    public static final String YEAR = "Year";

    public interface SCHEDULER_AUDIT {
        public static final String SCHEDULER_STATUS_SUCCESS = "success";
        public static final String SCHEDULER_STATUS_FAILURE = "failure";
        public static final String SCHEDULAR_AUDIT_FOR_UPDATE_CUSTOMER_AND_ITS_SERVICE = "Update Customer and its service";

    }
}
