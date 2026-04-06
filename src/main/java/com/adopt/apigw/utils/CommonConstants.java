package com.adopt.apigw.utils;

import com.adopt.apigw.modules.CommonList.utils.TypeConstants;

public class CommonConstants {
    public static final Integer DB_PAGE_SIZE = 5;
    public static final Integer DISP_PAGE_SIZE = 5;

    public static final Integer SORT_ORDER_ASC = 1;
    public static final Integer SORT_ORDER_DESC = 0;

    public static final String SECRET = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";
    public static final String AUTHORIZATION_TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER_STRING = "Authorization";
    public static final long EXPIRATION_TIME = 864_000_000;
    public static final long FORGOT_PWD_EXPIRATION_TIME = 900;
    public static final String RESCHEDULE_FOLLOW_UP_REMARKS = "rescheduleFollowupRemarks";
    public static final String PERMISSION_NONE = "0";
    public static final String PERMISSION_READ = "1";
    public static final String PERMISSION_WRITE = "2";
    public static final String PERMISSION_DELETE = "4";

    public static final String FLASH_MSG_TYPE_SUCCESS = "success";
    public static final String FLASH_MSG_TYPE_ERROR = "fail";

    public static final String TAX_TYPE_SLAB = "SLAB";

    public static final String TAX_TYPE_COMPOUND = "Compound";
    public static final String TAX_TYPE_TIER = "TIER";

    public static final String BILL_PATH_PARAM = "pdfpath";
    public static final String PAYMENT_PATH_PARAM = "paymentpdfpath";
    public static final String ANONYMOUS_USER = "anonymoususer";


    public static final String PARTNER_BILL_PATH_PARAM = "partnerpdfpath";

    public static final String TRIAL_BILL_PATH_PARAM = "trialpdfpath";

    public static final String SERVER_TYPE_RADIUS = "radius";
    public static final String SERVER_TYPE_BILLING = "billing";

    public static final String PAYMENT_STATUS_PENDDING = "pending";
    public static final String PAYMENT_STATUS_APPROVED = "approved";


    public static final String CHARGE_TYPE_ADVANCE = "ADVANCE";
    public static final String CHARGE_TYPE_RECURRING = "RECURRING";
    public static final String CHARGE_TYPE_NONRECURRING = "NON_RECURRING";

    public static final String PARENT_EXPERIENCE_SINGLE = "Single";
    public static final String PARENT_EXPERIENCE_ACTUAL = "Actual";
    public static final String CHARGE_TYPE_REFUNDABLE = "REFUNDABLE";
    public static final String CHARGE_TYPE_CUSTOMER_DIRECT = "CUSTOMER_DIRECT";

    public static final String CUST_TYPE_PREPAID = "Prepaid";
    public static final String CUST_TYPE_POSTPAID = "Postpaid";

    public static final String PLAN_TYPE_PREPAID = "Prepaid";
    public static final String PLAN_TYPE_POSTPAID = "Postpaid";
    public static final String CHARGE_RECURRING_AUTO = "Recurring_Auto";
    public static final String CHARGE_ADVANCE_AUTO = "Advance_Auto";
    public static final String CHARGE_TYPE_STATIC_IP = "Static-Ip";

    public static final String PLAN_GROUP_RENEW = "Renew";
    public static final String PLAN_GROUP_BOD = "BOD";
    public static final String PLAN_GROUP_VOLUME_BOOSTER = "Volume Booster";
    public static final String PLAN_GROUP_BANDWIDTH_BOOSTER = "Bandwidthbooster";
    public static final String PLAN_GROUP_DTV_ADDON = "DTV Addon";

    public static final String ACTIVE_STATUS = "Active";
    public static final String TERMINATED_STATUS = "Terminate";
    public static final String NEW_ACTIVATION_STATUS = "NewActivation";
    public static final String INACTIVE_STATUS = "Inactive";
    public static final String STOP_STATUS = "STOP";
    public static final String CLOSED_STATUS = "Closed";

    public static final String INGRACE_STATUS = "INGRACE";

    public static final String YES_STATUS = "Y";
    public static final String NO_STATUS = "N";

    public static final String BILL_RUN_URL = "http://{server}:{port}/AdoptBillingEngine/generatebill/{billrundate}";
    public static final String CUST_BILL_RUN_URL = "http://{server}:{port}/AdoptBillingEngine/generatebillcust/{billrundate}/{custid}";
    public static final String CUST_PLANREL_BILL_RUN_URL = "http://{server}:{port}/AdoptBillingEngine/generatebillcust/{billrundate}/{custid}/{custplanrelid}";
    public static final String GERERATE_INVOICE_URL = "http://{server}:{port}/AdoptBillingEngine/generatepdf/{billrunid}";
    public static final String EMAIL_INVOICE_URL = "http://{server}:{port}/billing-engine-1.0/billingprocess/emailpdf/{billrunid}";
    public static final String REVERT_INVOICE_URL = "http://{server}:{port}/billing-engine-1.0/billingprocess/revertbill/{invoiceid}";
    public static final String PAYMENT_RECEIPT_URL = "http://{server}:{port}/AdoptBillingEngine/payment/generatereceipt/{creditdocid}";

    public static final String BILL_CHARGE_RUN_URL = "http://{server}:{port}/AdoptBillingEngine/generatebillcharge/{custid}/{chargemappingid}";

    public static final String TRIAL_BILL_RUN_URL = "http://{server}:{port}/billing-engine-1.0/trialbillingprocess/generatebill/{billrundate}";
    public static final String TRIAL_GERERATE_INVOICE_URL = "http://{server}:{port}/billing-engine-1.0/trialbillingprocess/generatepdf/{billrunid}";
    public static final String TRIAL_REVERT_BILLRUN_URL = "http://{server}:{port}/billing-engine-1.0/trialbillingprocess/revertbillrun/{billrunid}";


    public static final String PBILL_RUN_URL = "http://{server}:{port}/billing-engine-1.0/partnerprocess/generatepartnerbill/{billrundate}";
    public static final String PARTNER_BILL_RUN_URL = "http://{server}:{port}/billing-engine-1.0/partnerprocess/generatebillpartner/{billrundate}/{partnerid}";
    public static final String PGERERATE_INVOICE_URL = "http://{server}:{port}/billing-engine-1.0/partnerprocess/generatepartnerpdf/{billrunid}";
    public static final String PEMAIL_INVOICE_URL = "http://{server}:{port}/billing-engine-1.0/partnerprocess/partneremailpdf/{billrunid}";

    public static final String TAX_CALCULATION_URL = "http://{taxCalServer}:{taxCalPort}/billing-engine-1.0/billingprocess/taxcalculation/{id}/{location}";
    public static final String TAX_CAL_SERVER = "127.0.0.1";
    public static final String TAX_CAL_PORT = "40080";

    public static final String TAX_NAME_TOTAL = "Total";

    public static final String USER_DISCONNECT_USER_URL = "http://{disconnectUserServer}:{disconnectUserPort}/rest/radius/dmusersessions/{username}";
    public static final String USER_DISCONNECT_SESSION_URL = "http://{disconnectSessionServer}:{disconnectSessionPort}/rest/radius/dmsession/{session}";


    public static final String PART_COMMTYPE_PERCUST_FLAT = "PERCUSTFLAT";
    public static final String PART_COMMTYPE_PERCUST_PERCENTAGE = "PERCUSTPERC";
    public static final String PART_COMMTYPE_PRICEBOOK = "PRICEBOOK";

    public static final Integer DEFAULT_PARTNER_ID = 1;
    public static final String DEFAULT_PASSWORD = "Password@123";
    public static final String PARAM_PARTNER_ROLE_ID = "partnerroleid";
    public static final String PARAM_PARTNER_MANAGER_ROLE_ID = "partnermanagerroleid";
    public static final String PARAM_PARTNER_OPERATOR_ROLE_ID = "partneroperatorroleid";

    public static final String TRANS_CATEGORY_INVOICE = "INVOICE";
    public static final String TRANS_CATEGORY_INVOICE1 = "Invoice";
    public static final String TRANS_CATEGORY_PAYMENT = "PAYMENT";

    public static final String TRANS_BUSINESS_PROMOTION = "Business Promotion";

    public static final String TRANS_REVERSAL_BUSINESS_PROMOTION = "Reverse Business Promotion";

    public static final String TRANS_CATEGORY_REVERSAL_PAYMENT = "REVERSAL PAYMENT";

    public static final String TRANS_CREDIT_NOTE = "CREDITNOTE";
    public static final String TRANS_CREDIT_NOTE1 = "Credit Note";
    public static final String TRANS_CATEGORY_REFUND = "REFUND";
    public static final String TRANS_CATEGORY_ADD_BALANCE = "Balance";
    public static final String CREDIT_NOTE_COMMISSION = "Credit Note Commission";
    public static final String WALLET_BALANCE_PAYOUT = "Wallet Balance Payout";
    public static final String TRANS_CATEGORY_ADD_CREDIT = "Credit";
    public static final String TRANS_CATEGORY_REVERSE_BALANCE = "ReverseBalance";
    public static final String TRANS_CATEGORY_COMMISSION = "Commision";

    public static final String TRANS_CATEGORY_BALANCE_TRANSFER = "TransferBalance";
    public static final String TRANS_CATEGORY_COMMISSION_TRANSFER = "TransferCommission";

    public static final String TRANS_CATEGORY_REVERT_COMMISSION = "Revert Commision";
    public static final String TRANS_CATEGORY_WALLET_ADJUST = "WalletAdj";
    public static final String TRANS_CATEGORY_ = "AddBalance";
    public static final String TRANS_CATEGORY_CUST_CREATE = "Balance";
    public static final String TRANS_TYPE_CREDIT = "CR";
    public static final String TRANS_TYPE_DEBIT = "DR";
    public static final String WALLET_BALANCE_TOPUP = "Wallet Balance Topup";
    public static final String TRANSFER_FROM_BALANCE = "Balance";
    public static final String TRANSFER_FROM_COMMISSION = "Commission";
    public static final String BALANCE_TRANSFER = "BalanceTranfer";
    public static final String COMMISSION_TRANSFER = "CommissionTrasnfer";
    public static final String WITHDRAW_COMMISSION = "Withdraw";

    public static final String PAYMENT_MODE_TYPE_DEBIT = "debitcard";
    public static final String PAYMENT_MODE_TYPE_CREDIT = "creditcard";
    public static final String PAYMENT_MODE_TYPE_CASH = "Cash";
    public static final String PAYMENT_MODE_TYPE_CASH_CAPS = "CASH";
    public static final String STAFF_PAYMENT_MODE_TYPE_CHEQUE = "Cheque";
    public static final String PAYMENT_MODE_TYPE_CHEQUE = "cheque";
    public static final String PAYMENT_MODE_TYPE_AUTOMATIC = "Automatic";
    public static final String PAYMENT_MODE_TYPE_NEFT_RTGS = "neft/rtgs";
    public static final String PAYMENT_MODE_TYPE_ONLINE = "online";

    public static final String TIME_QUOTA_TYPE = "time";
    public static final String DATA_QUOTA_TYPE = "data";
    public static final String BOTH_QUOTA_TYPE = "both";

    public static final String DID_QUOTA_TYPE = "did";
    public static final String INTERCOM_QUOTA_TYPE = "intercom";
    public static final String VOICE__BOTH_QUOTA_TYPE = "voiceboth";

    public static final String PRICE_BOOK = "pricebook";
    public static final Integer DEFAULT_MVNO_ID = 1;

    public static final Integer OPERATION_ADD = 1;
    public static final Integer OPERATION_UPDATE = 2;
    public static final Integer OPERATION_DELETE = 3;

    public static final String RESPONSE_MESSAGE = "message";

    public static final Integer DATA_SERVICE_ID = 1;
    public static final Integer VOICE_SERVICE_ID = 2;

    public static final String PAYMENT_INITIATE = "Initiate";
    public static final String DEFAULT_PARTNER = "Default";


    public static final String PR_RECEPIT_ID = "{RECEIPT_ID}";
    public static final String PR_CUST_ID = "{CUST_ID}";
    public static final String PR_RECEIPT_NO = "{RECEIPT_NO}";
    public static final String PR_RECEIPT_DATE = "{RECEIPT_DATE}";
    public static final String PR_PAY_AMOUNT = "{PAY_AMOUNT}";
    public static final String PR_PAY_AMOUNT_WORDS = "{PAY_AMOUNT_WORDS}";
    public static final String PR_PAY_DETAILS1 = "{PAY_DETAILS1}";
    public static final String PR_PAY_DETAILS2 = "{PAY_DETAILS2}";
    public static final String PR_PAY_DETAILS3 = "{PAY_DETAILS3}";
    public static final String PR_PAY_DETAILS4 = "{PAY_DETAILS4}";
    public static final String PR_PAY_REFNO = "{PAY_REF_NO}";
    public static final String PR_PHONE = "{PHONE}";
    public static final String PR_OUTSTANDING = "{OUTSTANDING}";
    public static final String PR_FROM_EMAIL = "{FROM_EMAIL}";
    public static final String PR_ADDRESS1 = "{ADDRESS1}";
    public static final String PR_ADDRESS2 = "{ADDRESS2}";
    public static final String PR_ADDRESS_TYPE = "{ADDRESS_TYPE}";
    public static final String PR_CITY = "{CITY}";
    public static final String PR_COUNTRY = "{COUNTRY}";
    public static final String PR_STATE = "{STATE}";
    public static final String PR_PIN = "{PIN}";
    public static final String PR_SUBSCR_ID = "{SUBSCR_ID}";
    public static final String PR_CUST_EMAIL = "{CUST_EMAIL}";
    public static final Long ADMIN_ROLE_ID = 1L;
    public static final Integer BACK_OFFICE_STAFF_ROLE_ID = 7;
    public static final Long SUBSCRIBER_ROLE_ID = 8L;
    public static final Long PG_USER_ROLE_ID = 9L;
    public static final String USER_DICONNECT_NAME = "username";
    public static final String USER_DICONNECT_SESSION = "session";
    public static final String BLANK_STRING = "string";

    public static final String PAY_RECEIPT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + " <receipt>" + "     <id>" + PR_RECEPIT_ID + "</id>" + "     <customerId>" + PR_CUST_ID + "</customerId>" + "     <number>" + PR_RECEIPT_NO + "</number>" + "     <createDate>" + PR_RECEIPT_DATE + "</createDate>" + "     <payment>" + PR_PAY_AMOUNT + "</payment>" + "     <totalAmountInWords>" + PR_PAY_AMOUNT_WORDS + "</totalAmountInWords>" + "<paymentdetails1>" + PR_PAY_DETAILS1 + "</paymentdetails1>" + "<paymentdetails2>" + PR_PAY_DETAILS2 + "</paymentdetails2>" + "<paymentdetails3>" + PR_PAY_DETAILS3 + "</paymentdetails3>" + "<paymentdetails4>" + PR_PAY_DETAILS4 + "</paymentdetails4>" + "<referenceno>" + PR_PAY_REFNO + "</referenceno>" + "     <customerInformation>" + "         <accountnumber xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <accounttype xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <authorizationpolicyname xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <balance xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <birthdate xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <brand xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <country xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <createdate xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <cui xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <customertype xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <email>" + PR_CUST_EMAIL + "</email>" + "         <encryptiontype xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <expirydate xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <failureattempt xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <firstlogintime xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <firstname xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <gatewayaddress xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <gender xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <hotspotname xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <imei xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <imsi xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <lastlogintime xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <lastlogouttime xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <lastmodifieddate xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <lastname xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <laststatuschangedate xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <location xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <msisdn xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <outstandingbalance>" + PR_OUTSTANDING + "</outstandingbalance>" + "         <phone>" + PR_PHONE + "</phone>" + "         <qos xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <status xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <subscriberpackage xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <timebasedtotalquota xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <timebasedunusedquota xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <timebasedusedquota xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <volumebasedtotalquota xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <volumebasedunusedquota xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "         <volumebasedusedquota xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" + "     </customerInformation>" + "     <addressDetail>" + "         <address1>" + PR_ADDRESS1 + "</address1>" + "         <address2>" + PR_ADDRESS2 + "</address2>" + "         <addresstype>" + PR_ADDRESS_TYPE + "</addresstype>" + "         <city>" + PR_CITY + "</city>" + "         <country>" + PR_COUNTRY + "</country>" + "         <pincode>" + PR_PIN + "</pincode>" + "         <state>" + PR_STATE + "</state>" + "         <subscriberid>" + PR_SUBSCR_ID + "</subscriberid>" + "     </addressDetail>" + "     <planInformation>" + "         <description>{PLAN_DESC}</description>" + "         <displayname>{PLAN_DISP_NAME}</displayname>" + "         <name>{PLAN_NAME}</name>" + "         <postpaidplanid>{PLAN_ID}</postpaidplanid>" + "     </planInformation>" + "     <email>" + PR_CUST_EMAIL + "</email>" + "     <phone>" + PR_PHONE + "</phone>" + " </receipt>";


    public static final String REFURBISHED = "Refurbished";
    public static final String OLD = "Old";
    public static final String NEW = "New";

    public static final String EXPIRED = "Expired";
    public static final String DAMAGED_AT_SITE = "DamagedAtSite";
    public static final String DAMAGED_AT_STORE = "DamagedAtStore";

    public static final String CUSTOMER_STATUS_NEW_ACTIVATION = "NewActivation";

    public static final String CUSTOMER_STATUS_TERMINATE = "Terminate";

    public static final String CUSTOMER_STATUS_INACTIVE = "InActive";

    public static final String CUSTOMER_STATUS_HOLD = "Hold";

    public static final String CUSTOMER_PLAN_STATUS_HOLD = "ON Hold";
    public static final String CUST_PLAN_STATUS_ACTIVE = "Active";

    public static final String CASE_STATUS_NEW_ACTIVATION = "NewActivation";
    public static final String DUNNING_ACTION_TYPE_EMAIL = "Email";
    public static final String DUNNING_ACTION_TYPE_SMS = "SMS";
    public static final String DUNNING_ACTION_TYPE_DEACTIVATION = "DeActivation";

    public static final String INVOICE_TYPE_GROUP = "Group";
    public static final String INVOICE_TYPE_INDEPENDENT = "Independent";

    public static final String VALIDIDY_UNIT_DAYS = "Days";
    public static final String VALIDIDY_UNIT_MONTHS = "Months";
    public static final String VALIDIDY_UNIT_YEARS = "Years";
    public static final String VALIDIDY_UNIT_HOURS = "Hours";

    public static final String CAL_TYPE_ENGLISH = "English";
    public static final String CAL_TYPE_NEPALI = "Nepali";

    public static final String COMMISSION_ON_SERVICE = "Service level";

    public static final String COMMISSION_ON_PLAN = "Plan level";

    public interface TICKET_STATUS {
        String IN_PROGRESS = "In Progress";
        String OPEN = "Open";
        String PENDING = "Pending";
        String RESOLVED = "Resolved";
        String CLOSE = "Close";

        String ASSIGNED = "Assigned";
    }

    public interface TICKET_ACTION {
        String NOTIFICATION = "Notification";
        String BOTH = "Both";
        String REASSIGN = "Reassign";
    }


    public static final String HIERARCHY_TERMINATION = "Termination";

    public static final String HIERARCHY_PAYMENTS = "PAYMENT";
    public static final String HIERARCHY_PLANS = "PLAN";

    public static final String HIERARCHY_TYPE = "hierarchy_event";

    public static final String DUNNING_TYPE_PAYMENT = "Payment";
    public static final String DUNNING_TYPE_ADVANCENOTIFICATION = "AdvanceNotification";
    public static final String DUNNING_TYPE_DOCUMENT = "Document";
    public static final String DUNNING_TYPE_PARTNER_DOCUMENT = "PartnerDocument";

    public static final String DUNNING_TYPE_MVNO_DOCUMENT = "MvnoDocument";
    public static final String TICKET_COUNT = "TICKET_COUNT";
    public static final String TICKET_COUNT_SAME_CATEGORY = "TICKET_COUNT_SAME_CATEGORY";
    public static final String TICKET_COUNT_IN_LAST_DAYS = "TICKET_COUNT_IN_LAST_DAYS";
    public static final String TICKET_COUNT_SAME_CATEGORY_DAYS = "TICKET_COUNT_SAME_CATEGORY_DAYS";

    public static final String IN = "IN";
    public static final String OUT = "OUT";
    public static final String DUNNING_TYPE_Document = "Document";

    public static final String BILL_CHARGE_RUN_URL_FOR_INVENTORY = "http://{server}:{port}/AdoptBillingEngine/generateBillForInventory/{custid}/{inventoryMappingId}";

    public static final String LIST_TYPE_HIERARCHY = "hierarchy_event";
    public static final String HIERARCHY_LEAD = "Lead";

    public static final String HIERARCHY_CUSTOMER_CHANGE_DISCOUNT = "CUSTOMER_DISCOUNT";
    public static final String WAREHOUSE = "Warehouse";
    public static final String STAFF = "Staff";
    public static final String CUSTOMER = "Customer";
    public static final String PARTNER = "Partner";
    public static final String SERVICE_AREA = "Service Area";
    public static final String POP = "POP";
    public static final String INWARD = "Inward";
    public static final String CUSTOMER_BIND = "CustomerBind";
    public static final String NETWORK_BIND = "NetworkBind";
    public static final String NA = "NA";
    public static final String ASSIGN_INVETORIES = "assign_inventory";
    public static final String REMOVE_INVETORIES = "remove_inventory";
    public static final String REPLACE_INVETORIES = "replace_inventory";

    public static final String REJECT_INVETORIES = "reject_inventory";


    public static final String CUSTOMER_STATUS_ACTIVE = "Active";
    public static final String DUNNING_TYPE_MVNO_PAYMENT = "MvnoPayment";
    public static final String DUNNING_TYPE_MVNO_ADVANCE_NOTIFICATION = "MVNOAdvanceNotification";
    public static final String RENEW_PAYMENT = "Renew_Payment";
    public static final String CURRENT_PAYMENT = "Current_Payment";


    public interface WORKFLOW_EVENT_NAME {
        String PAYMENT = "PAYMENT";
        String PLAN = "PLAN";
        String CAF = "CAF";
        String TERMINATION = "TERMINATION";
        String CUSTOMER_INVENTORY_ASSIGN = "CUSTOMER_INVENTORY_ASSIGN";
        String LEAD = "LEAD";
        String SHIFT_LOCATION = "SHIFT_LOCATION";
        String CASE = "CASE";
        String PLAN_GROUP = "PLAN_GROUP";
        String CHANGE_DISCOUNT = "CUSTOMER_DISCOUNT";
        String BILL_TO_ORGANIZATION = "BILL_TO_ORGANIZATION";
        String PARTNER_BALANCE = "PARTNER_BALANCE";
        String DOCUMENT_VERIFICATION = "DOCUMENT_VERIFICATION";
        String MVNO_DOCUMENT_VERIFICATION = "MVNO_DOCUMENT_VERIFICATION";
        String SPECIAL_PLAN_MAPPING = "SPECIAL_PLAN_MAPPING";
        String REMOVE_INVENTORY = "REMOVE_INVENTORY";
        String CREDIT_NOTE = "CREDIT_NOTE";
        String CUSTOMER_SERVICE_TERMINATION = "CUSTOMER_SERVICE_TERMINATION";

        String CUSTOMER_SERVICE_ADD = "CUSTOMER_SERVICE_ADD";
        String CUSTOMER_INVENTORY_REPLACE = "CUSTOMER_INVENTORY_ASSIGN_REPLACE";
        String LEAD_QUOTATION = "LEAD_QUOTATION";
    }

    public interface WORKFLOW_AUDIT_ACTION {
        String ASSIGNED = "Assigned";
        String APPROVED = "Approved";
        String REJECTED = "Rejected";
        String PICKED = "Picked";

        String REMOVE = "Remove";

    }
    public interface  WORKFLOW_AUDIT_STATUS{
        String PENDING="Pending";
        String FINAL_APPROVED="Final Approved";
        String FINAL_REJECTED="Final Rejected";
        String INPROGRESS="In Progress";
        String APPROVAL_STATUS="APPROVAL_STATUS";
        String CUST_ID="CUST_ID";
    }

    public interface CAF_ACTION {
        String INVENTORY_ASSIGNMENT = "INVENTORY_ASSIGNMENT";
        String INVOICE_GENERATION = "INVOICE_GENERATION";
        String ACTIVATION = "ACTIVATION";
        String DOCUMENT_UPLOAD = "DOCUMENT_UPLOAD";
        String UPLOAD_ALL_DOCUMENTS = "UPLOAD_ALL_DOCUMENTS";
        String DOCUMENT_VERIFICATION = "DOCUMENT_VERIFICATION";
        String FEASIBILITY_RESULT = "FEASIBILITY_RESULT";
        String DOCUMENT_TYPE_CONTRACT = "DOCUMENT_TYPE_CONTRACT";
        String DOCUMENT_TYPE_PROOF_OF_ADDRESS = "DOCUMENT_TYPE_PROOF_OF_ADDRESS";
        String DOCUMENT_TYPE_PROOF_OF_IDENTITY = "DOCUMENT_TYPE_PROOF_OF_IDENTITY";
        String WALLET_SETTLEMENT = "WALLET_SETTLEMENT";
        String DIGITAL_SIGNATURE = "DIGITAL_SIGNATURE";
        String NETWORK_LOCATION="NETWORK LOCATION";

    }

    public interface PAYMENT_CONDITION {
        String PAYMENT_AMOUNT = "amount";
        String PLAN_PURCHASE_TYPE = "PLAN_PURCHASE_TYPE";
        String PLAN_MODE = "PLAN_MODE";
        String SERVICE_AREA = "SERVICE_AREA";
        String CALENDAR_TYPE = "CALENDAR_TYPE";
        String CUSTOMER_CATEGORY = "CUSTOMER_CATEGORY";
        String PARTNER_NAME = "PARTNER_NAME";
        String PARTNER_EMAIL = "PARTNER_EMAIL";
        String AREA = "AREA";
        String PINCODE = "PINCODE";
        String CITY = "CITY";
        String STATE = "STATE";
        String BILL_TO = "BILL_TO";
        String INVOICE_TO_ORG = "INVOICE_TO_ORG";
        String PARENT_CUSTOMER_USERNAME = "PARENT_CUSTOMER_USERNAME";
        String USERNAME = "USERNAME";
        String DISCOUNT = "DISCOUNT";
        String PLAN_SERVICE = "PLAN_SERVICE";
        //String CURRENT_TEAM_ASSIGNED = "CURRENT_TEAM_ASSIGNED";
        String LEAD_SOURCE = "LEAD_SOURCE";
        String FEASIBILITY_REQUIRED = "FEASIBILITY_REQUIRED";
        String BRANCH = "BRANCH";
        String REGION = "REGION";
        String BUSINESS_VERTICAL = "BUSINESS_VERTICAL";
        String TEAM_ASSIGNED_NEW = "TEAM_ASSIGNED_NEW";
        String PLAN_GROUP = "PLAN_GROUP";
        String PLAN_CATEGORY = "PLAN_CATEGORY";
        String BU = "BU";
        //String OLD_DISCOUNT = "OLD_DISCOUNT";
        String PAYMENT_MODE = "PAYMENT_MODE";
        String PAYMENT_TYPE = "PAYMENT_TYPE";
    }

    public interface CAF_CONDITION {
        String OFFER_PRICE = "offer_price";
        String DISCOUNT = "discount";
        //String PLAN_PURCHASE_TYPE = "PLAN_PURCHASE_TYPE";
        String PLAN_MODE = "PLAN_MODE";
        String SERVICE_AREA = "SERVICE_AREA";
        String CALENDAR_TYPE = "CALENDAR_TYPE";
        String CUSTOMER_CATEGORY = "CUSTOMER_CATEGORY";
        String PARTNER_NAME = "PARTNER_NAME";
        String PARTNER_EMAIL = "PARTNER_EMAIL";
        String AREA = "AREA";
        String PINCODE = "PINCODE";
        String CITY = "CITY";
        String STATE = "STATE";
        String BILL_TO = "BILL_TO";
        String INVOICE_TO_ORG = "INVOICE_TO_ORG";
        String PARENT_CUSTOMER_USERNAME = "PARENT_CUSTOMER_USERNAME";
        String USERNAME = "USERNAME";
        String PLAN_SERVICE = "PLAN_SERVICE";
        //String CURRENT_TEAM_ASSIGNED = "CURRENT_TEAM_ASSIGNED";
        String LEAD_SOURCE = "LEAD_SOURCE";
        String FEASIBILITY_REQUIRED = "FEASIBILITY_REQUIRED";
        String OLD_DISCOUNT = "OLD_DISCOUNT";
        String NEW_DISCOUNT = "NEW_DISCOUNT";
        String BRANCH = "BRANCH";
        String REGION = "REGION";
        String BUSINESS_VERTICAL = "BUSINESS_VERTICAL";
        //String TEAM_ASSIGNED_NEW = "TEAM_ASSIGNED_NEW";
        //String PLAN_GROUP = "PLAN_GROUP";
        String PLAN_CATEGORY = "PLAN_CATEGORY";
        String BU = "BU";
        String TRIAL = "TRIAL";
        String DEPARTMENT="DEPARTMENT";
        String NETWORK_LOCATION="NETWORK_LOCATION";
    }

    public static final String CUSTOMER_STATUS_REJECTED = "Rejected";

    public interface EVENT_NAME {
        public static final String PAYMENT = "PAYMENT";
        public static final String PLAN = "PLAN";
        public static final String CAF = "CAF";
        public static final String TERMINATION = "Termination";
        public static final String INVENTORY_ASSIGN = "INVENTORY_ASSIGN";
        public static final String LEAD = "LEAD";
        public static final String CUSTOMER_DISCOUNT = "CUSTOMER_DISCOUNT";
        public static final String PLAN_GROUP = "PLAN_GROUP";
        public static final String CASE = "CASE";
        public static final String BILL_TO_ORGANIZATION = "BILL_TO_ORGANIZATION";
        public static final String PARTNER_BALANCE = "PARTNER_BALANCE";
        public static final String SHIFT_LOCATION = "SHIFT_LOCATION";
        public static final String DOCUMENT_VERIFICATION = "DOCUMENT_VERIFICATION";
        public static final String LEAD_QUOTATION = "LEAD_QUOTATION";


    }

    public interface PLAN_CONDITION {
        String OFFER_PRICE = "offer_price";
        String SERVICE_AREA = "SERVICE_AREA";
        String SERVICE = "SERVICE";
        String PLAN_TYPE = "PLAN_TYPE";
        String PLAN_CATEGORY = "PLAN_CATEGORY";
        String TYPE = "TYPE";
        String QUOTA_TYPE = "QUOTA_TYPE";
        String BILL_CYCLE = "BILL_CYCLE";
        String QUOTA_RESET_INTERVAL = "QUOTA_RESET_INTERVAL";
        //String ICCODE = "ICCODE";
        String BRANCH = "BRANCH";
        String REGION = "REGION";
        String BUSINESS_VERTICAL = "BUSINESS_VERTICAL";
        //String TEAM_ASSIGNED_NEW = "TEAM_ASSIGNED_NEW";
        String PLAN_GROUP = "PLAN_GROUP";
       // String CURRENT_TEAM_ASSIGNED = "CURRENT_TEAM_ASSIGNED";
        String PARTNER_NAME = "PARTNER_NAME";
        String DISCOUNT = "DISCOUNT";
        String PLAN_MODE = "PLAN_MODE";
        String FEASIBILITY_REQUIRED = "FEASIBILITY_REQUIRED";
        String BU = "BU";

    }

    public interface PLAN_ACTION {
        String ACTIVATION = "ACTIVATION";

    }

    public interface PLAN_GROUP_CONDITION {
        String OFFER_PRICE = "offer_price";
        String SERVICE_AREA = "SERVICE_AREA";
        String SERVICE = "SERVICE";
        String PLAN_TYPE = "PLAN_TYPE";
        String PLAN_CATEGORY = "PLAN_CATEGORY";
        //String TYPE = "TYPE";
        String QUOTA_TYPE = "QUOTA_TYPE";
        //String BILL_CYCLE = "BILL_CYCLE";
        String QUOTA_RESET_INTERVAL = "QUOTA_RESET_INTERVAL";
        //String ICCODE = "ICCODE";
        String BRANCH = "BRANCH";
        String REGION = "REGION";
        String BUSINESS_VERTICAL = "BUSINESS_VERTICAL";
        String TEAM_ASSIGNED_NEW = "TEAM_ASSIGNED_NEW";
        String PLAN_GROUP = "PLAN_GROUP";
        String DISCOUNT = "DISCOUNT";
        String CURRENT_TEAM_ASSIGNED = "CURRENT_TEAM_ASSIGNED";
        String PARTNER_NAME = "PARTNER_NAME";
        String PLAN_MODE = "PLAN_MODE";
        String FEASIBILITY_REQUIRED = "FEASIBILITY_REQUIRED";
        String BU = "BU";

    }

    public interface PLAN_GROUP_ACTION {
        String ACTIVATION = "ACTIVATION";

    }

    public interface CUSTOMER_INVENTORY_ASSIGN_ACTION {
        String INVOICE_GENERATION = "GENERATE_INVOICE";
        String MAC_CHANGE_PROVISION = "MAC_CHANGE_PROVISION";

    }

    public interface CUSTOMER_INVENTORY_ASSIGN_CONDITION {
        String PLAN_PURCHASE_TYPE = "PLAN_PURCHASE_TYPE";
        String PLAN_MODE = "PLAN_MODE";
        //        String CURRENT_SERVICE_AREA = "CURRENT_SERVICE_AREA";
        String SERVICE_AREA = "SERVICE_AREA";
        String CALENDAR_TYPE = "CALENDAR_TYPE";
        String CUSTOMER_CATEGORY = "CATEGORY";
        String PARTNER_NAME = "PARTNER_NAME";
        String PARTNER_EMAIL = "PARTNER_EMAIL";
        String AREA = "AREA";
        String PINCODE = "PINCODE";
        String CITY = "CITY";
        String STATE = "STATE";
        String BILL_TO = "BILL_TO";
        String INVOICE_TO_ORG = "INVOICE_TO_ORG";
        String PARENT_CUSTOMER_USERNAME = "PARENT_CUSTOMER_USERNAME";
        String USERNAME = "USERNAME";
        String PLAN_SERVICES = "PLAN_SERVICES";
        String CURRENT_TEAM_ASSIGNED = "CURRENT_TEAM_ASSIGNED";
        //        String CURRENT_PARTNER = "CURRENT_PARTNER";
        String OLD_INVENTORY_CATEGORY = "OLD_INVENTORY_CATEGORY";
        String OLD_INVENTORY_PRODUCT = "OLD_INVENTORY_PRODUCT";
        String NEW_INVENTORY_CATEGORY = "NEW_INVENTORY_CATEGORY";
        String NEW_INVENTORY_PRODUCT = "NEW_INVENTORY_PRODUCT";
        String BILL_CYCLE = "BILL_CYCLE";
        String BRANCH = "BRANCH";
        String REGION = "REGION";
        String BUSINESS_VERTICAL = "BUSINESS_VERTICAL";
        String TEAM_ASSIGNED_NEW = "TEAM_ASSIGNED_NEW";
        String PLAN_GROUP = "PLAN_GROUP";
        String PLAN_CATEGORY = "PLAN_CATEGORY";
        String BU = "BU";
        String OLD_DISCOUNT = "OLD_DISCOUNT";
        String FEASIBILITY_REQUIRED = "FEASIBILITY_REQUIRED";
    }

    public interface TERMINATION_ACTION {
        String CHANGE_STATUS_TO_TERMINATE = "CHANGE_STATUS_TO_TERMINATE";
        String REMOVE_INVENTORY = "REMOVE_INVENTORY";
        String WALLET_SETTLEMENT = "WALLET_SETTLEMENT";
        String CLOSE_ALL_TICKETS="CLOSE_ALL_TICKETS";
        String CLOSE_ALL_PENDING_TASKS="CLOSE_ALL_PENDING_TASKS";
        String SEVICE_WALLET_SETTLEMENT="SEVICE_WALLET_SETTLEMENT";

    }
    public interface TERMINATION_CONDITION {
        String PLAN_PURCHASE_TYPE = "PLAN_PURCHASE_TYPE";
        String PLAN_MODE = "PLAN_MODE";
        String SERVICE_AREA = "SERVICE_AREA";
        String CALENDAR_TYPE = "CALENDAR_TYPE";
        String CATEGORY = "CATEGORY";
        String PARTNER_NAME = "PARTNER_NAME";
        String PARTNER_EMAIL = "PARTNER_EMAIL";
        String AREA = "AREA";
        String PINCODE = "PINCODE";
        String CITY = "CITY";
        String STATE = "STATE";
        String BILL_TO = "BILL_TO";
        //String INVOICE_TO_ORG = "INVOICE_TO_ORG";
        //String PARENT_CUSTOMER_USERNAME = "PARENT_CUSTOMER_USERNAME";
        String USERNAME = "USERNAME";
        String DISCOUNT = "DISCOUNT";
        String WALLET_AMOUNT = "WALLET_AMOUNT";
        String PLAN_SERVICES = "PLAN_SERVICES";
        String CUSTOMER_CATEGORY = "CUSTOMER_CATEGORY";
        //String CURRENT_TEAM_ASSIGNED = "CURRENT_TEAM_ASSIGNED";
        String LEAD_SOURCE = "LEAD_SOURCE";
        String FEASIBILITY_REQUIRED = "FEASIBILITY_REQUIRED";
        String BRANCH = "BRANCH";
        String REGION = "REGION";
        String BUSINESS_VERTICAL = "BUSINESS_VERTICAL";
        //String TEAM_ASSIGNED_NEW = "TEAM_ASSIGNED_NEW";
        String PLAN_GROUP = "PLAN_GROUP";
        String PLAN_CATEGORY = "PLAN_CATEGORY";
        String BU = "BU";
        String OLD_DISCOUNT = "OLD_DISCOUNT";
    }

    public interface LEAD_ACTION {
        String CONVERT_CAF = "CONVERT_CAF";
        String FEASIBILITY_RESULT = "FEASIBILITY_RESULT";
    }

    public interface LEAD_CONDITION {
        String PLAN_PURCHASE_TYPE = "PLAN_PURCHASE_TYPE";
        String PLAN_MODE = "PLAN_MODE";
        String SERVICE_AREA = "SERVICE_AREA";
        String CALENDAR_TYPE = "CALENDAR_TYPE";
        String CATEGORY = "CATEGORY";
        String PARTNER_NAME = "PARTNER_NAME";
        String PARTNER_EMAIL = "PARTNER_EMAIL";
        String AREA = "AREA";
        String PINCODE = "PINCODE";
        String CITY = "CITY";
        String STATE = "STATE";
        String BILL_TO = "BILL_TO";
        String INVOICE_TO_ORG = "INVOICE_TO_ORG";
        //String PARENT_CUSTOMER_USERNAME = "PARENT_CUSTOMER_USERNAME";
        String USERNAME = "USERNAME";
        String DISCOUNT = "DISCOUNT";
        String PLAN_SERVICES = "PLAN_SERVICES";
        //String CURRENT_TEAM_ASSIGNED = "CURRENT_TEAM_ASSIGNED";
        String CURRENT_TEAM_ASSIGNED = "CURRENT_TEAM";
        String LEAD_SOURCE = "LEAD_SOURCE";
        String FEASIBILITY_REQUIRED = "FEASIBILITY_REQUIRED";
        String BRANCH = "BRANCH";
        String REGION = "REGION";
        String BUSINESS_VERTICAL = "BUSINESS_VERTICAL";
        String TEAM_ASSIGNED_NEW = "TEAM_ASSIGNED_NEW";
        String PLAN_GROUP = "PLAN_GROUP";
        String PLAN_CATEGORY = "PLAN_CATEGORY";
        String BU = "BU";
        //String TEAM_NAME="TEAM_NAME";
        String TRIAL = "TRIAL";
        String DEPARTMENT="DEPARTMENT";
    }

    public interface PAYMENT_ACTION {
        String APPROVE = "APPROVE";
    }

    public interface SHIFT_LOCATION_ACTION {
        //String CHANGE_LOCATION = "CHANGE_LOCATION";
        //String REASSIGN_INVENTORY = "REASSIGN_INVENTORY";
    }

    public interface SHIFT_LOCATION_CONDITION {
        String PLAN_PURCHASE_TYPE = "PLAN_PURCHASE_TYPE";
        String PLAN_MODE = "PLAN_MODE";
        String SERVICE_AREA = "SERVICE_AREA";
        String CALENDAR_TYPE = "CALENDAR_TYPE";
        String CATEGORY = "CATEGORY";
        String PARTNER_NAME = "PARTNER_NAME";
        String PARTNER_EMAIL = "PARTNER_EMAIL";
        String AREA = "AREA";
        String PINCODE = "PINCODE";
        String CITY = "CITY";
        String STATE = "STATE";
        String BILL_TO = "BILL_TO";
        String INVOICE_TO_ORG = "INVOICE_TO_ORG";
        String PARENT_CUSTOMER_USERNAME = "PARENT_CUSTOMER_USERNAME";
        String USERNAME = "USERNAME";
        String PLAN_SERVICES = "PLAN_SERVICES";
        String CURRENT_TEAM_ASSIGNED = "CURRENT_TEAM_ASSIGNED";
        String BRANCH = "BRANCH";
        String REGION = "REGION";
        String BUSINESS_VERTICAL = "BUSINESS_VERTICAL";
        String TEAM_ASSIGNED_NEW = "TEAM_ASSIGNED_NEW";
        String PLAN_GROUP = "PLAN_GROUP";
        String PLAN_CATEGORY = "PLAN_CATEGORY";
        String FEASIBILITY_REQUIRED = "FEASIBILITY_REQUIRED";
        String OLD_SERVICE_AREA = "OLD_SERVICE_AREA";
        String BU = "BU";
        String CHARGE="Charge";
    }

    public interface CASE_CONDITION {
        //String PLAN_PURCHASE_TYPE = "PLAN_PURCHASE_TYPE";
        String PLAN_MODE = "PLAN_MODE";
        //        String CURRENT_SERVICE_AREA = "CURRENT_SERVICE_AREA";
        String SERVICE_AREA = "SERVICE_AREA";
        String CALENDAR_TYPE = "CALENDAR_TYPE";
        String CATEGORY = "CATEGORY";
        String PARTNER_NAME = "PARTNER_NAME";
        String PARTNER_EMAIL = "PARTNER_EMAIL";
        String AREA = "AREA";
        String PINCODE = "PINCODE";
        String CITY = "CITY";
        String STATE = "STATE";
        String BILL_TO = "BILL_TO";
        String INVOICE_TO_ORG = "INVOICE_TO_ORG";
        String PARENT_CUSTOMER_USERNAME = "PARENT_CUSTOMER_USERNAME";
        String USERNAME = "USERNAME";
        String PLAN_SERVICES = "PLAN_SERVICES";
        String CURRENT_TEAM_ASSIGNED = "CURRENT_TEAM_ASSIGNED";
        //        String CURRENT_PARTNER = "CURRENT_PARTNER";
        String TICKET_CATEGORY = "Problem Domain";
        String TICKET_SUB_CATEGORY = "Sub Problem Domain";
        String PRIORITY = "PRIORITY";
        String DEPARTMENT = "DEPARTMENT";
        String CUSTOMER_AREA = "Customer Area";
        String VALLEY_TYPE = "Valley Type";
        String BRANCH = "BRANCH";
        String REGION = "REGION";
        String BUSINESS_VERTICAL = "BUSINESS_VERTICAL";
        String TEAM_ASSIGNED_NEW = "TEAM_ASSIGNED_NEW";
        String PLAN_GROUP = "PLAN_GROUP";
        String PLAN_CATEGORY = "PLAN_CATEGORY";
        String FEASIBILITY_REQUIRED = "FEASIBILITY_REQUIRED";
        String REASON_CATEGORY = "REASON_CATEGORY";
        String BU = "BU";
        String CUSTOMER_TYPE= "CUSTOMER_TYPE";
        String TICKET_STATUS="TICKET_STATUS";
        //String TICKET_RAISED_BY_TEAM="TICKET_RAISED_BY_TEAM";
        String CASE_TYPE="CASE_TYPE";
        //String TICCKET_CREATED_DURATION="TICCKET_CREATED_DURATION";

    }

    public interface CHANGE_DISCOUNT_CONDITION {

        String DISCOUNT = "discount";
        String OFFER_PRICE = "OFFER_PRICE";
        String PLAN_PURCHASE_TYPE = "PLAN_PURCHASE_TYPE";
        String PLAN_MODE = "PLAN_MODE";
        String SERVICE_AREA = "SERVICE_AREA";
        String CALENDAR_TYPE = "CALENDAR_TYPE";
        String CUSTOMER_CATEGORY = "CUSTOMER_CATEGORY";
        String PARTNER_NAME = "PARTNER_NAME";
        String PARTNER_EMAIL = "PARTNER_EMAIL";
        String AREA = "AREA";
        String PINCODE = "PINCODE";
        String CITY = "CITY";
        String STATE = "STATE";
        String BILL_TO = "BILL_TO";
        String INVOICE_TO_ORG = "INVOICE_TO_ORG";
        String PARENT_CUSTOMER_USERNAME = "PARENT_CUSTOMER_USERNAME";
        String USERNAME = "USERNAME";
        String PLAN_SERVICE = "PLAN_SERVICE";
        String CURRENT_TEAM_ASSIGNED = "CURRENT_TEAM_ASSIGNED";
        String LEAD_SOURCE = "LEAD_SOURCE";
        String FEASIBILITY_REQUIRED = "FEASIBILITY_REQUIRED";
        String OLD_DISCOUNT = "OLD_DISCOUNT";
        String NEW_DISCOUNT = "NEW_DISCOUNT";
        String BRANCH = "BRANCH";
        String REGION = "REGION";
        String BUSINESS_VERTICAL = "BUSINESS_VERTICAL";
        String PLAN_GROUP = "PLAN_GROUP";
        String PLAN_CATEGORY = "PLAN_CATEGORY";
        String BU = "BU";
        String PLAN_TYPE = "PLAN_TYPE";
    }

    public interface PARTNER_PAYMENT_CONDITION {
        public static String PAYMENT_MODE = "PAYMENT_MODE";
        public static String BALANCE = "BALANCE";
        public static String CREDIT_LIMIT = "CREDIT_LIMIT";
        public static String BALANCE_TRANSFER = "BALANCE_TRANSFER";
        public static String WITHDRAWAL = "WITHDRAWAL";

    }

    //    public interface PARTNER_PAYMENT_ACTION{
//        public static String PARTNER_PAYMENT_APPROVE= "Approve";
//    }
    public static String LEADINQ = "Inquiry";
    public static String LEADRINQ = "Re-Inquiry";

    public static String TERMINATED = "TERMINATED";
    public static String FORWARDED_INWARD_TYPE = "Forwarded";
    public static String RETURNED_INWARD_TYPE = "Returned";

    public static String UNALLOCATED = "UnAllocated";

    public static String ALLOCATED = "Allocated";

    public static String RETURNED = "Returned";
    public static String MAINTENANCE = "Maintenance";

    public static String DEFECTIVE = "Defective";

    public static final String PARTNER_TYPE_FRANCHISE = "Franchise";

    public static final String PARTNER_TYPE_LCO = "LCO";
    public static final String PARTNER_TYPE_LCO_ROLE = "LCO_ROLE";


    public interface WORKFLOW_MSG_ACTION {
        String CUSTOMER = "Customer";
        String LEAD = "Lead";
        String PLAN = "Plan";
        String PAYMENT = "Payment";
        String PLAN_GROUP = "Plan Bundle";
        String SHIFT_LOCATION = "Shift Location";
        String TICKET = "Ticket";
        String INVENTORY = "Inventory";
        String PARTNER_BALANCE = "Partner Balance";
        String BILL_TO_ORGANIZATION = "Bill To Organization";
        String CHANGE_DISCOUNT = "Change Discount";
        String CUSTOMER_DOCUMENT = "Customer Document";
        String SPECIAL_PLAN_MAPPING = "Special plan mapping";
        String TERMINATION = "TERMINATION";
        String CREDIT_NOTE = "Credit Note";
        String CUSTOMER_SERVICE_TERMINATION = "Customer service termination";
        String LEAD_QUOTATION = "Lead Quotation";

        String CUSTOMER_SERVICE_ADD = "Customer Add Service";

    }

    public interface DEBIT_DOC_STATUS {
        String FULLY_PAID = "Fully Paid";
        String PARTIALY_PAID = "Partialy Paid";
        String PENDING = "pending";
        String REJCTED = "rejected";

        String APPROVED = "approved";
        String PENDING_SENT = "Pending Sent";
        String PENDING_ACCEPTED = "Pending Accepted";
        String PARTIAL_PENDING = "Partial Pending";

        String CLEAR = "Clear";

        String PAYABLE = "Payable";

        String UNPAID = "Unpaid";

        String CANCELLED = "Cancelled";

        String VOID = "Void";

    }

    public interface CREDIT_DOC_STATUS {
        String FULLY_ADJUSTED = "Fully Adjusted";
        String PARTIAL_ADJUSTED = "Partialy Adjusted";
        String ADVANCE_PAYMENT = "advance";

        String PENDING = "pending";

        String WITHDRAWAL = "Withdrawal";

        String GENERATED = "Generated";

        String ADJUSTED = "Adjusted";
        String ADJUSTMENT = "ADJUSTMENT";


    }

    public interface PAYMENT_MODE {
        String CREDIT_NOTE = "Credit Note";
        String CREDIT_NOTE1 = "CreditNote";
        String CASH = "Cash";
        String BUSINESS_PROMOTION = "Buiness Promotion";

    }

    public interface PAYMENT_STATUS {
        String COLLECTED = "Collected";
        String SUBMITTED = "Submitted";
        String VERIFIED = "Verified";
        String REJECTED = "Rejected";
        String ACCEPTED = "Accepted";

    }

    public interface CUSTOMER_STATUS {
        String INAVCTIVE = "InActive";
        String TERMINATE = "Terminate";
        String SUSPEND = "Suspend";
        String ACTIVE = "Active";
        String FUTURE = "FUTURE";
        String NEW_ACTIVATION ="NewActivation";
        String ACTIVATION_PENDING ="ActivationPending";
    }

    public interface CUSTOMER_INVENTORY_STATUS {
        public String ACTIVE = "ACTIVE";
        public String PENDING = "PENDING";
    }

    public static String CHANGE_PLAN_MSG = "CHANGE_PLAN";

    public static String NOTIFICATION_TYPE_STAFF = "Staff";
    public static String NOTIFICATION_TYPE_TEAM = "Team";


    public interface EZ_BILL_CAS_NAMES {
        String SAFEVIEW = "SafeView";
        String SAFEVIEW1 = "Safeview1";
        String VERIMATRIX = "VeriMatrix";


    }

    public interface CAS_PARAMS {
        String AUTH_TOKEN_EZ_BILL = "authToken";
    }

    public interface SAVE_CUSTOMER_RESPONSE_PARAMS_EZ {
        String CUSTOMER_ID = "customer_id";
        String account_number = "account_number";
        String customer_service_id = "customer_service_id";
        String stock_id = "stock_id";


    }

    public static final String WORKFLOW_ASSIGNED_FOR_APPROVAL = "is assigned to you. Please approve.";

    public interface TICKET_SEARCH_OPTION {
        String CUSTOMER_USERNAME = "CUSTOMER_USERNAME";
        String TICKET_STATUS = "TICKET_STATUS";
        String TICKET_PRIORITY = "TICKET_PRIORITY";
        String ASSIGNED_TEAM = "ASSIGNED_TEAM";
        String CUSTOMER_SERVICE_AREA = "CUSTOMER_SERVICE_AREA";
        String TICKET_NUMBER = "TICKET_NUMBER";
        String TICKET_PROBLEM_DOMAIN = "TICKET_PROBLEM_DOMAIN";
        String USER_ID = "USER_ID";
        String NETWORK_EQUIPMENT = "NETWORK_EQUIPMENT";
        String TAT_BREATCH = "TAT_BREATCH";
        String RESPONSE_TIME_BREACH = "RESPONSE_TIME_BREACH";
        String TICKET_LEVEL = "TICKET_LEVEL";
        String MY_TICKETS = "MY_TICKETS";


    }

    public interface CREDIT_NOTE_CONDITION {
        String PAYMENT_AMOUNT = "amount";
        String PLAN_PURCHASE_TYPE = "PLAN_PURCHASE_TYPE";
        String PLAN_MODE = "PLAN_MODE";
        String SERVICE_AREA = "SERVICE_AREA";
        String CALENDAR_TYPE = "CALENDAR_TYPE";
        String CUSTOMER_CATEGORY = "CUSTOMER_CATEGORY";
        String PARTNER_NAME = "PARTNER_NAME";
        String PARTNER_EMAIL = "PARTNER_EMAIL";
        String AREA = "AREA";
        String PINCODE = "PINCODE";
        String CITY = "CITY";
        String STATE = "STATE";
        String BILL_TO = "BILL_TO";
        String INVOICE_TO_ORG = "INVOICE_TO_ORG";
        String PARENT_CUSTOMER_USERNAME = "PARENT_CUSTOMER_USERNAME";
        String USERNAME = "USERNAME";
        String DISCOUNT = "DISCOUNT";
        String PLAN_SERVICE = "PLAN_SERVICE";
        String CURRENT_TEAM_ASSIGNED = "CURRENT_TEAM_ASSIGNED";
        String LEAD_SOURCE = "LEAD_SOURCE";
        String FEASIBILITY_REQUIRED = "FEASIBILITY_REQUIRED";
        String BRANCH = "BRANCH";
        String REGION = "REGION";
        String BUSINESS_VERTICAL = "BUSINESS_VERTICAL";
        String TEAM_ASSIGNED_NEW = "TEAM_ASSIGNED_NEW";
        String PLAN_GROUP = "PLAN_GROUP";
        String PLAN_CATEGORY = "PLAN_CATEGORY";
        String BU = "BU";
        String OLD_DISCOUNT = "OLD_DISCOUNT";
        String PAYMENT_MODE = "PAYMENT_MODE";
        String PAYMENT_TYPE = "PAYMENT_TYPE";
    }

    public interface DISCOUNT_TYPE {
        String ONE_TIME = "One-time";
        String RECURRING = "Recurring";
    }

    public static final String LEAD_CHANGE_ASSIGNEE = "Change Assignee";

    public static final String DATA_LIST = "dataList";
    public static final String RETAIL = "Retail";
    public static final String ENTERPRISE = "Enterprise";
    public static final String PREDEFINED = "Predefined";
    public static final String ON_DEMAND = "On-Demand";
    public static final String QUOTATION_STATUS_NEW_ACTIVATION = "NewActivation";

    public interface COMMON_DATA_FOR_NAV {
        String BRANCH = "BF183";
        String BU = " BU105";
        String ICCODE = " IC116";

    }

    public interface CUSTOMER_SERVICE_ADD_ACTION {
        String INVENTORY_ASSIGNMENT = "INVENTORY_ASSIGNMENT";
        String INVOICE_GENERATION = "INVOICE_GENERATION";
        String ACTIVATION = "ACTIVATION";
        String DOCUMENT_UPLOAD = "DOCUMENT_UPLOAD";
        String DOCUMENT_VERIFICATION = "DOCUMENT_VERIFICATION";
        String FEASIBILITY_RESULT = "FEASIBILITY_RESULT";
        String DOCUMENT_TYPE_CONTRACT = "DOCUMENT_TYPE_CONTRACT";
        String DOCUMENT_TYPE_PROOF_OF_ADDRESS = "DOCUMENT_TYPE_PROOF_OF_ADDRESS";
        String DOCUMENT_TYPE_PROOF_OF_IDENTITY = "DOCUMENT_TYPE_PROOF_OF_IDENTITY";
    }
    public interface CUSTOMER_SERVICE_TERMINATION_ACTION {
        String SERVICE_TERMINATE = "SERVICE_TERMINATE";
    }
    public interface CUSTOMER_SERVICE_ADD_CONDITION {
        String OFFER_PRICE = "offer_price";
        String DISCOUNT = "discount";
        String PLAN_PURCHASE_TYPE = "PLAN_PURCHASE_TYPE";
        String PLAN_MODE = "PLAN_MODE";
        String SERVICE_AREA = "SERVICE_AREA";
        String CALENDAR_TYPE = "CALENDAR_TYPE";
        String CUSTOMER_CATEGORY = "CUSTOMER_CATEGORY";
        String PARTNER_NAME = "PARTNER_NAME";
        String PARTNER_EMAIL = "PARTNER_EMAIL";
        String AREA = "AREA";
        String PINCODE = "PINCODE";
        String CITY = "CITY";
        String STATE = "STATE";
        String BILL_TO = "BILL_TO";
        String INVOICE_TO_ORG = "INVOICE_TO_ORG";
        String PARENT_CUSTOMER_USERNAME = "PARENT_CUSTOMER_USERNAME";
        String USERNAME = "USERNAME";
        String PLAN_SERVICE = "PLAN_SERVICE";
        String CURRENT_TEAM_ASSIGNED = "CURRENT_TEAM_ASSIGNED";
        String LEAD_SOURCE = "LEAD_SOURCE";
        String FEASIBILITY_REQUIRED = "FEASIBILITY_REQUIRED";
        String OLD_DISCOUNT = "OLD_DISCOUNT";
        String NEW_DISCOUNT = "NEW_DISCOUNT";
        String BRANCH = "BRANCH";
        String REGION = "REGION";
        String BUSINESS_VERTICAL = "BUSINESS_VERTICAL";
        String TEAM_ASSIGNED_NEW = "TEAM_ASSIGNED_NEW";
        String PLAN_GROUP = "PLAN_GROUP";
        String PLAN_CATEGORY = "PLAN_CATEGORY";
        String BU = "BU";
    }

    public interface DOCUMENT_VERIFICATION_CONDITION{
        String DEPARTMENT = "DEPARTMENT";
    }

    public interface STAFF_WALLET_STATUS {
        String SETTELED = "Settled";
        String PARTIALY_PENDING = "Partialy Pending";
        String PENDING = "Pending";
        String CLEAR = "Clear";

    }

    public interface INVOICE_TYPE {
        String CUSTOMER_CHARGE = "Customer_Charge";
        String CREATE_CUSTOMER = "Create_Customer";
        String RENEW = "Renew";
        String CHANGE_PLAN = "Change_Plan";

        String IS_CAF_CUSTOMER = "isCAFCustomer";

        String INVENTORY = "Inventory";
        String CANCEL_REGENERATE = "Cancel_Regenerate";

        String NEW_SERVICE = "New_Service";
        String TRIAL_TO_NORMAL = "Trail_To_Normal";

        String CREATE_CAF_CUSTOMER = "Create_Caf_Customer";
    }

    public enum SubscriptionMode {
        PLAN,VOUCHER,OTP, MEMBER, GUEST;
    }

    public interface CUST_QUOTA_TYPE {
        String SHAREABLE = "shareable";
        String INDIVIDUAL = "individual";

    }

    public interface  MODULES{
        String MODULE_CMS = "CMS";
    }

    /**This is a flag that determined which Rabbitmq operation done **/
    public interface PAYMENT_CONFIG_RABBITMQ_FLAG{
        String CREATE = "CREATE";

        String UPDATE = "UPDATE";

        String DELETE = "DELETE";
    }

    public interface NMS_CONSTANTS{
        String CONFIG_NAME = "NMS_ENABLE";
    }

    public interface CDATA_CONSTANTS{
        String CDATA_MANUFACTURER = "CDATA_MANUFACTURER";
        String CONFIG_NAME = "CDATA_ENABLE";
    }

    public interface FIBER_HOME_CONSTANTS{
        String FIBER_HOME_MANUFACTURER = "FiberHome";
        String FINER_HOME_CONF_NAME = "FiberHomeEnable";
    }

    public interface SOCKET_URL_CONSTANT{
        String QR_URL = "/topic/qr";
    }


    public interface ONLINE_PAY_AUDIT{
        String INTITATE = "Initiate";
        String SUCCESS = "Succcess";
        String FAILED = "Failed";
    }

    public interface EVENTCONSTANTS {
        public static final String VOLUME_BOOSTER_EXPIRE = "VOLUME_BOOSTER_EXPIRE";
        public static final String CUSTOMER_CREATE = "CUSTOMER_CREATE";
        public static final String CUSTOMER_UPDATE = "CUSTOMER_UPDATE";
        public static final String CHANGE_PLAN = "CHANGE_PLAN";

        public static final String QUOTA_BOOSTER_EXPIRE = "QUOTA_BOOSTER_EXPIRE";

        public static final String RENEW_PLAN = "RENEW_PLAN";
        public static final String NEW_VOLUME_BOOSTER = "NEW_VOLUME_BOOSTER";
        public static final String NEW_BANDWIDTH_BOOSTER = "NEW_BANDWIDTH_BOOSTER";
    }

    public static final String MVNO_CUST_REF = "MVNO_CUST_REF";
    public static String LEAD_CAF_VISIBILITY_RISTRICT = "LEAD_CAF_VISIBILITY_RISTRICT";
    public static final String CURRENT_TEAM_ACTION = "currentTeamAction";
    public static final String CURRENT_TEAM_AUTO_APPROVE_ENABLE = "currentTeamAutoApprove";

    public static final String CURRENT_TEAM_AUTO_ASSIGN_ENABLE = "currentTeamAutoAssign";
    public static final String PROOF_OF_IDENTITY ="proofofidentity";
    public static final String PROOF_OF_ADDRESS = "proofofaddress";
    public static final String CUSTOMER_DOC_TYPE_ONLINE ="custdocsubtype_proofofidentity_online";
    public static final String CUSTOMER_DOC_TYPE_OFFLINE ="custdocsubtype_proofofidentity_offline";

    public interface ENTITYTYPE
    {
        public static final String CAF = "CAF";
        public static final String TERMINATION = "TERMINATIION";
        public static final String PLAN ="PLAN";
        public static final String LEAD = "LEAD";
        public static final String CREDITNOTE = "CREDITNOTE";
        public static final String PAYMENT = "PAYMENT";
    }

    public static final String FEEDBACKFORM_FREQUENCY = "FEEDBACKFORM_FREQUENCY";

    public static final String VAS_CHARGE = "VAS_CHARGE";
    public interface INSTALLMENT_FREQUENCY {
        String MONTHLY = "MONTHLY";
        String QUARTERLY = "QUARTERLY";
        String ANNUALLY = "ANNUALLY";
    }

    public interface BooleanMessages {

        // Default VAS related messages
        String DEFAULT_VAS_NOT_FOUND = "No default VAS configuration was found. Please ensure a default VAS is set for proper processing.";
        String MULTIPLE_VAS_FOUND = "Multiple default VAS configurations detected. Please resolve this conflict to proceed.";
        String DEFAULT_VAS_ALLOWED = "A default VAS has been successfully applied and marked as allowed.";
        String DEFAULT_VAS_NOT_ALLOWED = "The default VAS was applied, but it does not meet the criteria to be allowed.";

        // Shift location count and invoice rules
        String SHIFT_LOCATION_COUNT_SET_UNLIMITED = "The shift location has no limit on the number of entries. Unlimited access is enabled.";
        String VAS_ELIGIBLATITY_FAILED = "VAS plan either expired or not found. The customer is required to make a payment for the shift location. Would you like to proceed?";
        String VAS_ELIGIBLATITY_SUCCESS = "You are Eligible For Free Shift Location.";

        // Shift location condition validation
        String SHIFT_LOCATION_ALLOWED = "All shift location rules are satisfied. Access is granted.";
        String SHIFT_LOCATION_NOT_ALLOWED = "You are Not Eligible For Free Shift Location.";

        // Payment limit conditions
        String SHIFT_LOCATION_PAYMENT_LIMIT_ALLOWED = "The payment limit condition for the shift location has been satisfied. Proceeding with payment-related actions.";
        String SHIFT_LOCATION_PAYMENT_LIMIT_NOT_ALLOWED = "Customer is required to pay shift location charges as the payment condition is not met.";

        // Overall condition result
        String ALL_CONDITIONS_SATISFIED = "All shift location rules and conditions are fully met. Operation successful.";
    }


}

