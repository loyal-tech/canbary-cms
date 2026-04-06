package com.adopt.apigw.modules.integrations.NexgeVoice.constants;

public class NexgeVoiceProvisionConstants {

    //URLs
    public static final String NEXG_BASE_URL = "/api/v1";
    public static final String NEXG_AUTH_URL = "/auth/key";
    public static final String NEXG_LOGIN_URL = "/auth/login";
    public static final String NEXG_LOGIN_REFRESH_URL = "/auth/refresh";
    public static final String NEXG_SAVE_CUSTOMER_URL = "/customers";
    public static final String NEXG_ACCOUNT_URL = "/accounts";
    public static final String NEXG_DELETE_URL = "/delete";
    public static final String NEXG_DID_URL = "/DID";

    //Header Keys
    public static final String NEXG_HEADER_X_API_KEY = "X-API-KEY";
    public static final String NEXG_HEADER_AUTHORIZATION = "Authorization";

    //DB Keys
    public static final String NEXG_AUTH_API_KEY = "nexg_api_key";
    public static final String NEXG_SERVER_KEY = "nexg_server";
    public static final String NEXG_AUTH_USER_ID_KEY = "nexg_auth_userid";
    public static final String NEXG_AUTH_USER_PASSWORD_KEY = "nexg_auth_password";
    public static final String NEXG_AUTH_RESPONSE_KEY = "nexg_auth_response_key";
    public static final String NEXG_AUTH_RESPONSE_IP_KEY = "nexg_auth_response_ip";
    public static final String NEXG_LOGIN_TOKEN = "nexg_auth_token";
    public static final String NEXG_LOGIN_TOKEN_EXPIRY = "nexg_auth_token_expiry";
    public static final String NEXG_L_ONE_PASSWORD = "nexg_auth_level_one_password";
    public static final String NEXG_L_TWO_PASSWORD = "nexg_auth_level_two_password";
    public static final String NEXG_PARENT_NAME_KEY = "nexg_req_parent";

    //JSON Map Keys
    public static final String NEXG_JSON_RESP_API_KEY = "key";
    public static final String NEXG_JSON_RESP_IP_KEY = "ip";
    public static final String NEXG_JSON_LOGIN_RESP_TOKEN_KEY = "token";
    public static final String NEXG_JSON_LOGIN_RESP_EXPIRY_KEY = "expiresAt";
    public static final String NEXG_JSON_AUTH_USERID_KEY = "authUserId";
    public static final String NEXG_JSON_AUTH_PASSWORD_KEY = "authResponse";
    public static final String NEXG_JSON_SERVICE_PLAN_ID_KEY = "servicePlanId";

    // Constants
    public static final String NEXG_PLAN_TYPE_PREPAID = "PrePaid";
    public static final String NEXG_PLAN_TYPE_POSTPAID = "PostPaid";
    public static final String NEXG_BSS = "BSS";

    //Status
    public static final String NEXG_SUB_STATUS_ACTIVE = "Active";

    //Account Types
    public static final String NEXG_SUB_ACC_PHONELINE = "Phoneline";
    public static final String NEXG_SUB_ACC_SIPTRUNK = "SIPTrunk";
    public static final String NEXG_SUB_ACC_INTERCOM = "Intercom";




}
