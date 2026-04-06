package com.adopt.apigw.kafka;

public class KafkaConstant {

    // KAFKA TOPICS
    public static  final String COMBINED_GROUP="combined_group";
    public static final String KAFKA_COMMON_TOPIC="common";

    public static final String KAFKA_CMS_TOPIC="cms";

    public static final String KAFKA_PMS_TOPIC="pms";

    public static final String KAFKA_INVENTORY_TOPIC="inventory";

    public static final String KAFKA_REVENUE_TOPIC="revenue";

    public static final String KAFKA_NOTIFICATION_TOPIC="notification";

    public static final String KAFKA_RADIUS_TOPIC="radius";

    public static final String KAFKA_TICKET_TOPIC="ticket";

    public static final String KAFKA_SALES_CRM_TOPIC="sales";

    public static final String KAFKA_INTEGRATION_TOPIC="integration";

    //KAFKA-GROUP-ID'S
    public static  final String KAFKA_COMMON_GROUP_ID="cms-common-group";

    public static  final String KAFKA_PMS_GROUP_ID="cms-pms-group";

    public static  final String KAFKA_INVENTORY_GROUP_ID="cms-inventory-group";

    public static  final String KAFKA_REVENUE_GROUP_ID="cms-revenue-group";

    public static  final String KAFKA_NOTIFICATION_GROUP_ID="cms-notification-group";

    public static  final String KAFKA_RADIUS_GROUP_ID="cms-radius-group";

    public static  final String KAFKA_TICKET_GROUP_ID="cms-ticket-group";

    public static  final String KAFKA_SALSE_CRM_GROUP_ID="cms-sales-crm-group";

    public static  final String KAFKA_INTEGRATION_GROUP_ID="cms-integration-group";


    //Kafka Event-Types

    public static final String OPT_FOR_PORTAL="OPT_FOR_PORTAL";
    public static final String OPT_PROFILE_SAVE="OPT_PROFILE_SAVE";

    public static final String OPT_PROFILE_UPDATE="OPT_PROFILE_UPDATE";

    public static final String OPT_FOR_LOGIN_2FA="OPT_FOR_LOGIN_2FA";

    public static  final String BSS_CUSTOMER_DUNNING = "BSS_CUSTOMER_DUNNING";

    public static final String  LEAD_SAVE_PLANGROUP = "SAVE_PLANGROUP";

    public static final String  LEAD_UPDATE_PLANGROUP = "UPDATE_PLANGROUP";

    public static final String CREATE_PARTNER = "CREATE_PARTNER";

    public static final String UPDATE_PARTNER = "UPDATE_PARTNER";

    public static final String CREATE_SERVICE_CONFIG="CREATE_SERVICE_CONFIG";

    public static final String UPDATE_SERVICE_CONFIG= "UPDATE_SERVICE_CONFIG";

    public static final String SAVE_VENDOR="SAVE_VENDOR";

    public static final String UPDATE_VENDOR="UPDATE_VENDOR";

    public static final String CREATE_NEW_CHARGE = "CREATE_NEW_CHARGE";

    public static final String UPDATE_NEW_CHARGE = "UPDATE_NEW_CHARGE";

    public static final String CREATE_REF_CHARGE = "CREATE_REF_CHARGE";

    public static final String UPDATE_REF_CHARGE = "UPDATE_REF_CHARGE";

    public static final String CREATE_DATA_ROLE = "CREATE_DATA_ROLE";

    public static final String DELETE_DATA_ROLE = "DELETE_DATA_ROLE";

    public static final String UPDATE_CONCURRENCY = "UPDATE_CONCURRENCY";

    public static final String CUSTOMER_ENDDATE = "CUSTOMER_ENDDATE";

    public static final String SEND_QUOTA = "SEND_QUOTA";

    public static final String QUOTA_INTRIM = "QUOTA_INTRIM";

    public static final String CUSTOMERS_UPDATE_RESERVED_QUOTA = "CUSTOMERS_UPDATE_RESERVED_QUOTA";

    public static final String SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_STAFF="SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_STAFF";

    public static  final String SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_CUSTOMER="SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_CUSTOMER";

    public static final String SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_STAFF="SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_STAFF";

    public static final String SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_PARENT_STAFF ="SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_PARENT_STAFF";

    public static final String DUNNING_ADVANCE_NOTIFICATION="DUNNING_ADVANCE_NOTIFICATION";

    public static  final String PARTNER_DUNNING_DOCUMENT="PARTNER_DUNNING_DOCUMENT";

    public static final String PARTNER_DUNNING_DOCUMENT_DEACTIVATION= "PARTNER_DUNNING_DOCUMENT_DEACTIVATION";

    public static  final String CUSTOMER_OPEN_ADDRESS_SHIFTING_NOTIFICATION="CUSTOMER_OPEN_ADDRESS_SHIFTING_NOTIFICATION";

    public static  final String CUSTOMER_CLOSE_ADDRESS_SHIFTING_NOTIFICATION="CUSTOMER_CLOSE_ADDRESS_SHIFTING_NOTIFICATION";

    public static final String SEND_QUOTA_NOTIFICATION_CUSTOMER="SEND_QUOTA_NOTIFICATION_CUSTOMER";

    public static final String SEND_QUOTA_EXHUAST_NOTIFICATION_CUSTOMER="SEND_QUOTA_EXHUAST_NOTIFICATION_CUSTOMER";

    public static final String CAF_TAT_SUCCESS_MESSAGE ="CAF_TAT_SUCCESS_MESSAGE";

    public static final String TREMINATION_TAT_SUCCESS_MESSAGE="TREMINATION_TAT_SUCCESS_MESSAGE";

    public static final String LEAD_TAT_SUCCESS_MESSAGE="LEAD_TAT_SUCCESS_MESSAGE";

    public static  final String SEND_MVNO_DOCUMENT_DUNNING_MESSAGE_TO_NOTIFICATION="SEND_MVNO_DOCUMENT_DUNNING_MESSAGE_TO_NOTIFICATION";

    public static final String SEND_MVNO_DEACTIVATION_MESSAGE_TO_NOTIFICATION="SEND_MVNO_DEACTIVATION_MESSAGE_TO_NOTIFICATION";

    public static final String SEND_MVNO_PAYMENT_ADVANCE_NOTIFICATION="SEND_MVNO_PAYMENT_ADVANCE_NOTIFICATION";

    public static final String SEND_MVNO_PAYMENT_REMINDER_NOTIFICATION="SEND_MVNO_PAYMENT_REMINDER_NOTIFICATION";

    public static final String SEND_CUSTOMER_CREATE_AND_UPDATE_DATA="SEND_CUSTOMER_CREATE_AND_UPDATE_DATA";

    public static final String CUSTOMER_DOCUMENT_DUNNING_TO_STAFF = "CUSTOMER_DOCUMENT_DUNNING_TO_STAFF";

    public static final String SEND_PARTNER_AMOUNT_MESSAGE_TO_API_AND_PARTNER="UPDATE_PARTNER_BALANCE";

    public static final String KAFKA_CMS_CHANGE_PLAN_TOPIC="cms-change-plan";

    public static  final String KAFKA_CMS_CHANGE_PLAN_GROUP_ID="revenue-cms-change-plan-group";

    public static final String BUY_PLAN="BUY_PLAN";

    public static final String BUILDING_MGMT_SAVE = "BUILDING_MGMT_SAVE";
    public static final String BUILDING_MGMT_UPDATE = "BUILDING_MGMT_UPDATE";

    public static final String SEND_QUOTA_RESET = "SEND_QUOTA_RESET";

    public static final String CHANGE_SERVICE_STATUS_MESSAGE="CHANGE_SERVICE_STATUS_MESSAGE";

    public static final String SYNC_SERVICE_CONFIG="SYNC_SERVICE_CONFIG";

    public static final String KAFKA_REVENUE_INVOICE_TOPIC = "revenue_invoice_requests_topic";
}
