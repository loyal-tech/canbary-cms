package com.adopt.apigw.service.BulkService;

public class BulkManagementConstant {

    public interface DropdownStatus {
        public final String ACTIVE = "Active";
        public final String INACTIVE = "Inactive";
        public final String PRIVATE = "private";
        public final String PUBLIC = "public";
        public final String UNDER_DEVELOPMENT = "UnderDevelopment";
    }

    public interface EntityName {
        public final String QOSPOLICY = "QosPolicy";
    }

    public interface SheetNames{

        String QOS_SHEET = "QOS-Sheet";

        String MASTER_SHEET = "MASTER-SHEET";
    }

    public interface ColumnName{

        String POSTPAID_PLAN_ID = "postpaid_plan_id";
        String PLAN_NAME = "plan_name";

        String END_DATE = "end_date";

        String QUOTA = "quota";

        String QUOTA_TYPE = "quota_type";

        String USAGE_QUOTA_TYPE = "usage_quota_type";

        String MAX_CONCURRENTSESSION = "max_concurrentsession";

        String QUOTA_RESET_INTERVAL = "quota_reset_interval";

        String QUOTA_UNIT = "quota_unit";

        String STATUS = "status";

        String VALIDITY = "validity";


        String UNITS_OF_VALIDITY = "units_of_validity";


        String ALLOW_OVER_USAGE = "allow_over_usage";

        String QOS_NAME = "qos_name";

    }

    public interface MapData {
        public final String QOS_NAME = "QOS_Name";
    }

}
