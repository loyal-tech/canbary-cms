package com.adopt.apigw.constants;

public class PGConstants {

    public static final String PGCONFIG_FILE = "pgconfig.properties";
    public static final String PG_CONFIG_CLASS_MAME = ".classname";

    //Payu Merchant Info From PGConfig
    public static final String PG_CONFIG_PAYU_ID = "1";
    public static final String PG_CONFIG_PAYU_MERCHANT_KEY = "payu.merchant.id";
    public static final String PG_CONFIG_PAYU_PARTNER_SURL = "payu.surl.partner";
    public static final String PG_CONFIG_PAYU_PARTNER_FURL = "payu.furl.partner";
    public static final String PG_CONFIG_PAYU_CUSTOMER_SURL = "payu.surl.customer";
    public static final String PG_CONFIG_PAYU_CUSTOMER_FURL = "payu.furl.customer";
    public static final String PG_CONFIG_PAYU_SERVER_URL = "payu.clientredirecturl";
    public static final String PG_CONFIG_PAYU_SERVER_URL_CUST_PORTAL = "payu.clientredirecturlforcustportal";
    public static final String PG_CONFIG_PAYU_MERCHANT_SALT = "payu.merchant.salt";
    public static final String PG_CONFIG_PAYU_MERCHANT_AUTH_HEADER = "payu.auth.header";
    public static final String PG_CONFIG_PAYU_MERCHANT_SUBMIT_URL = "payu.submiturl";

    //OrderType
    public static final String ORDER_TYPE_PLAN = "PLAN";
    public static final String ORDER_TYPE_PARTNER_ADD_BALANCE = "BALANCE";

    //PaymentStatus
    public static final String PENDING_STATUS = "Pending";
    public static final String SUCCESS_STATUS = "Success";
    public static final String SUCCESSFUL_STATUS = "Successful";
    public static final String ABORT_STATUS = "Aborted";
    public static final String INVALID_STATUS = "Invalid";
    public static final String TIMEOUT_STATUS = "Timeout";
    public static final String FAILED_STATUS = "Failed";

    //Payu Map Key
    public static final String PAYU_MAP_KEY = "key";
    public static final String PAYU_MAP_AMOUNT = "amount";
    public static final String PAYU_MAP_FIRSTNAME = "firstname";
    public static final String PAYU_MAP_PHONE = "phone";
    public static final String PAYU_MAP_PRODUCT_INFO = "productinfo";
    public static final String PAYU_MAP_EMAIL = "email";
    public static final String PAYU_MAP_TXN_ID = "txnid";
    public static final String PAYU_MAP_UNIQUE_ID = "uniqueid";
    public static final String PAYU_MAP_SURL = "surl";
    public static final String PAYU_MAP_FURL = "furl";
    public static final String PAYU_MAP_SALT = "salt";
    public static final String PAYU_MAP_HEADER = "header";
    public static final String PAYU_MAP_UDF1 = "udf1";
    public static final String PAYU_MAP_UDF2 = "udf2";
    public static final String PAYU_MAP_UDF3 = "udf3";
    public static final String PAYU_MAP_SUBURL = "suburl";
    public static final String PAYU_MAP_HASH = "hash";

    //Payu PGResponse Key
    public static final String PAYU_RESPONSE_STATUS = "status";
    public static final String PAYU_RESPONSE_AMOUNT = "amount";
    public static final String PAYU_RESPONSE_TXNID = "txnid";
    public static final String PAYU_RESPONSE_MIHPAYID = "mihpayid";


    //CCAvenue Merchant Info
    public static final String PG_CONFIG_CCAVENUE_ID = "2";
    public static final String PG_CONFIG_CCAVENUE_MERCHANT_KEY = "ccavenue.merchant.id";
    public static final String PG_CONFIG_CCAVENUE_RI_DIRECT_URL = "ccavenue.redirect_url";
    public static final String PG_CONFIG_CCAVENUE_CANCEL_URL = "ccavenue.cancel_url";
    public static final String PG_CONFIG_CCAVENUE_ACCESS_CODE = "ccavenue.access_code";
    public static final String PG_CONFIG_CCAVENUE_ENC_KEY = "ccavenue.enc_key";
    public static final String PG_CONFIG_CCAVENUE_SUBMIT_URL = "ccavenue.submiturl";


    //CCAvenue Map Key
    public static final String CCAVENUE_MAP_MERCHANT_ID_KEY = "merchant_id";
    public static final String CCAVENUE_MAP_ORDER_ID_KEY = "order_id";
    public static final String CCAVENUE_MAP_CURRENCY_KEY = "currency";
    public static final String CCAVENUE_MAP_AMOUNT_KEY = "amount";
    public static final String CCAVENUE_MAP_RI_DIRECT_URL_KEY = "redirect_url";
    public static final String CCAVENUE_MAP_CANCEL_URL_KEY = "cancel_url";
    public static final String CCAVENUE_MAP_LANGUAGE_KEY = "language";
    public static final String CCAVENUE_MAP_ACCESS_CODE_KEY = "access_code";
    public static final String CCAVENUE_MAP_ENC_KEY = "enc_key";
    public static final String CCAVENUE_MAP_SUBMIT_URL_KEY = "suburl";
    public static final String CCAVENUE_MAP_ENC_REQUEST_KEY = "encRequest";

    //CCAVENUE PGResponse Key
    public static final String CCAVENUE_RESPONSE_STATUS = "order_status";
    public static final String CCAVENUE_RESPONSE_AMOUNT = "amount";
    public static final String CCAVENUE_RESPONSE_TRACKING_ID = "tracking_id";
    public static final String CCAVENUE_RESPONSE_ORDER_NO = "orderNo";
    public static final String CCAVENUE_RESPONSE_ORDER_ID = "order_id";

    //application.properties
    public static final String APPLICATION_PROPERTIES = "application.properties";
    public static final String PG_USER_STAFFID = "pguser.staffId";
    public static final String PG_USER_STAFFNAME = "pguser.staffName";

    public static final String CLIENT_REDIRECT_URL = "/#/masters/payment-status?txnid={TXNID}";
    public static final String CLIENT_REDIRECT_URL_CUST_PORTAL = "/#/recharge/payment-status?txnid={TXNID}";

    //Payu Basic Details Constant
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String MOBILE = "mobile";
    public static final String SURL = "surl";
    public static final String FURL = "furl";

    public static final String PARAMETER_SEP = "&";
    public static final String PARAMETER_EQUALS = "=";
}
