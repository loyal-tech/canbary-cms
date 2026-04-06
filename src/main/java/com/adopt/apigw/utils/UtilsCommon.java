package com.adopt.apigw.utils;

import com.adopt.apigw.model.common.ClientService;
import com.adopt.apigw.model.postpaid.InvoiceServer;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.postpaid.InvoiceServerService;
import com.adopt.apigw.spring.SpringContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

public class UtilsCommon {

    public static TreeMap<String, String> CUST_STATUS_MAP = null;
    public static TreeMap<String, String> CUST_STATUS_MAP_NOT_REG = null;
    public static TreeMap<String, String> CG_STATUS_MAP = null;
    public static TreeMap<String, String> ENTITY_STATUS_MAP = null;
    public static TreeMap<String, String> RADPROFILE_STATUS_MAP = null;
    public static TreeMap<String, String> PERMISSION_MAP = null;
    public static TreeMap<String, String> ACCTPROFILE_STATUS_MAP = null;
    public static TreeMap<String, String> PLAN_TYPE_MAP = null;
    static final String NUMERIC = "0123456789";
    static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    public static TreeMap<String, String> AUTHDRIVER_TYPE_MAP = null;

    public static TreeMap<String, String> LDAP_AUTHTYPE_MAP = null;

    public static TreeMap<String, String> IP_TYPE_MAP = null;

    public static TreeMap<String, String> INTERNATIONALLIZATION_COMBO = null;

    public static TreeMap<String, String> INVOICE_OPTION = null;

    public static TreeMap<String, String> PLAN_GROUP = null;

    public static TreeMap<String, String> DISCOUNT_CATEGORIES = null;

    public static TreeMap<String, String> DISCOUNT_TYPES = null;

//    static StringRedisTemplate redisTemplate = SpringContext.getBean(StringRedisTemplate.class);

    static Javers javers = JaversBuilder.javers().build();
    public static void resetCachedObjects() {
        activeInvoiceServer = null;
        BILL_PATH = null;
        PARTNER_BILL_PATH = null;
        PARTNER_ROLE_ID = null;
        TRIAL_BILL_PATH = null;
    }


    public static String getResponse(String flag1, String flag2, String flag3, Integer length) {
        String flag = "";
        if (flag1 != null) {
            flag += NUMERIC;
        }
        if (flag2 != null) {
            flag += UPPERCASE;
        }
        if (flag3 != null) {
            flag += LOWERCASE;
        }

        return randomString(length, flag);
    }

    static SecureRandom secureRnd = new SecureRandom();



    public static String randomString(int length, String flag) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(flag.charAt(secureRnd.nextInt(flag.length())));
        return sb.toString();
    }

    public static TreeMap<String, String> getPlanGroupOptionMap() {
        if (PLAN_GROUP == null) {
            PLAN_GROUP = new TreeMap<>();
            PLAN_GROUP.put("Base", "BASE");
            PLAN_GROUP.put("Renewal", "RENEWAL");
            PLAN_GROUP.put("Addon", "ADDON");
        }
        return PLAN_GROUP;
    }

    public static TreeMap<String, String> getInvoiceOptionMap() {
        if (INVOICE_OPTION == null) {
            INVOICE_OPTION = new TreeMap<>();
            INVOICE_OPTION.put("Single", "0");
            INVOICE_OPTION.put("Consolidated", "1");
        }
        return INVOICE_OPTION;
    }

    public static TreeMap<String, String> getInternationalizationsMap() {
        if (INTERNATIONALLIZATION_COMBO == null) {
            INTERNATIONALLIZATION_COMBO = new TreeMap<>();
            INTERNATIONALLIZATION_COMBO.put("English-INR", "0");
            INTERNATIONALLIZATION_COMBO.put("Nepali-INR", "1");
            INTERNATIONALLIZATION_COMBO.put("USD-Dollar", "2");
        }
        return INTERNATIONALLIZATION_COMBO;
    }

    public static TreeMap<String, String> getCustStatusMap() {
        if (CUST_STATUS_MAP == null) {
            CUST_STATUS_MAP = new TreeMap<>();
            CUST_STATUS_MAP.put("ACTIVE", "Active");
            CUST_STATUS_MAP.put("INACTIVE", "In Active");
            CUST_STATUS_MAP.put("BLOCK", "Blocked");
            CUST_STATUS_MAP.put("REGISTERED", "Registered");
        }
        return CUST_STATUS_MAP;
    }

    public static TreeMap<String, String> ONLINE_PAYMENT_STATUS_MAP = null;

    public static TreeMap<String, String> getPaymentStatus() {
        if (ONLINE_PAYMENT_STATUS_MAP == null) {
            ONLINE_PAYMENT_STATUS_MAP = new TreeMap<>();
            ONLINE_PAYMENT_STATUS_MAP.put("Pending", "Pending");
            ONLINE_PAYMENT_STATUS_MAP.put("Successful", "Successful");
            ONLINE_PAYMENT_STATUS_MAP.put("Failed", "Failed");
        }
        return ONLINE_PAYMENT_STATUS_MAP;
    }

    public static TreeMap<String, String> getCustStatusMapWhileNotRegister() {
        if (CUST_STATUS_MAP_NOT_REG == null) {
            CUST_STATUS_MAP_NOT_REG = new TreeMap<>();
            CUST_STATUS_MAP_NOT_REG.put("ACTIVE", "Active");
            CUST_STATUS_MAP_NOT_REG.put("REGISTERED", "Registered");
        }
        return CUST_STATUS_MAP_NOT_REG;
    }

    public static TreeMap<String, String> getPlanTypeMap() {
        if (PLAN_TYPE_MAP == null) {
            PLAN_TYPE_MAP = new TreeMap<>();
            PLAN_TYPE_MAP.put("TIME", "Time");
            PLAN_TYPE_MAP.put("VOLUME", "Volume");
        }
        return PLAN_TYPE_MAP;
    }

    public static TreeMap<String, String> getCGStatusMap() {
        if (CG_STATUS_MAP == null) {
            CG_STATUS_MAP = new TreeMap<>();
            CG_STATUS_MAP.put("ACTIVE", "Active");
            CG_STATUS_MAP.put("INACTIVE", "In Active");
        }
        return CG_STATUS_MAP;
    }


    public static TreeMap<String, String> getIpTypeMap() {
        if (IP_TYPE_MAP == null) {
            IP_TYPE_MAP = new TreeMap<>();
            IP_TYPE_MAP.put("IPv4", "IPv4");
            IP_TYPE_MAP.put("SUBNET", "SUBNET");
            IP_TYPE_MAP.put("IPv6", "IPv6");
        }
        return IP_TYPE_MAP;
    }

    public static TreeMap<String, String> getEntityStatusMap() {
        if (ENTITY_STATUS_MAP == null) {
            ENTITY_STATUS_MAP = new TreeMap<>();
            ENTITY_STATUS_MAP.put("ACTIVE", "Active");
            ENTITY_STATUS_MAP.put("INACTIVE", "In Active");
        }
        return ENTITY_STATUS_MAP;
    }


    public static TreeMap<String, String> getPermissionMap() {
        if (PERMISSION_MAP == null) {
            PERMISSION_MAP = new TreeMap<>();
            PERMISSION_MAP.put(CommonConstants.PERMISSION_NONE, "None");
            PERMISSION_MAP.put(CommonConstants.PERMISSION_READ, "Read");
            PERMISSION_MAP.put(CommonConstants.PERMISSION_WRITE, "Write");
            PERMISSION_MAP.put(CommonConstants.PERMISSION_DELETE, "Delete");
        }
        return PERMISSION_MAP;
    }

    public static TreeMap<String, String> getRadProfileStatusMap() {
        if (RADPROFILE_STATUS_MAP == null) {
            RADPROFILE_STATUS_MAP = new TreeMap<>();
            RADPROFILE_STATUS_MAP.put("1", "Active");
            RADPROFILE_STATUS_MAP.put("0", "In Active");
        }
        return RADPROFILE_STATUS_MAP;
    }

    public static TreeMap<String, String> getAcctProfileStatusMap() {
        if (ACCTPROFILE_STATUS_MAP == null) {
            ACCTPROFILE_STATUS_MAP = new TreeMap<>();
            ACCTPROFILE_STATUS_MAP.put("A", "Active");
            ACCTPROFILE_STATUS_MAP.put("I", "In Active");
        }
        return ACCTPROFILE_STATUS_MAP;
    }

    public static TreeMap<String, String> getAuthDriverTypeMap() {
        if (AUTHDRIVER_TYPE_MAP == null) {
            AUTHDRIVER_TYPE_MAP = new TreeMap<>();
            AUTHDRIVER_TYPE_MAP.put("1", "DB");
            AUTHDRIVER_TYPE_MAP.put("2", "LDAP");
        }
        return AUTHDRIVER_TYPE_MAP;
    }

    public static TreeMap<String, String> getLdapAuthTypeMap() {
        if (LDAP_AUTHTYPE_MAP == null) {
            LDAP_AUTHTYPE_MAP = new TreeMap<>();
            LDAP_AUTHTYPE_MAP.put("None", "None");
            LDAP_AUTHTYPE_MAP.put("simpe", "simpe");
            LDAP_AUTHTYPE_MAP.put("DIGEST-MD5", "DIGEST-MD5");
            LDAP_AUTHTYPE_MAP.put("GSSAPI", "GSSAPI");
        }
        return LDAP_AUTHTYPE_MAP;
    }


    public static TreeMap<String, String> YESNO_STATUS_MAP = null;

    public static TreeMap<String, String> getYesNoStatusMap() {
        if (YESNO_STATUS_MAP == null) {
            YESNO_STATUS_MAP = new TreeMap<>();
            YESNO_STATUS_MAP.put("Y", "Active");
            YESNO_STATUS_MAP.put("N", "In Active");
        }
        return YESNO_STATUS_MAP;
    }


    public static TreeMap<String, String> PLAN_STATUS_MAP = null;

    public static TreeMap<String, String> getPlanStatusMap() {
        if (PLAN_STATUS_MAP == null) {
            PLAN_STATUS_MAP = new TreeMap<>();
            PLAN_STATUS_MAP.put("Y", "Active");
            PLAN_STATUS_MAP.put("N", "In Active");
            PLAN_STATUS_MAP.put("S", "Staged");
            PLAN_STATUS_MAP.put("A", "Archived");
            PLAN_STATUS_MAP.put("L", "Live");
        }
        return PLAN_STATUS_MAP;
    }

    public static TreeMap<String, String> TAXTYPE_MAP = null;

    public static TreeMap<String, String> getTaxTypeMap() {
        if (TAXTYPE_MAP == null) {
            TAXTYPE_MAP = new TreeMap<>();
            TAXTYPE_MAP.put(CommonConstants.TAX_TYPE_SLAB, "Slab");
            TAXTYPE_MAP.put(CommonConstants.TAX_TYPE_TIER, "Tiered");
            TAXTYPE_MAP.put(CommonConstants.TAX_TYPE_COMPOUND, "Compound");
        }
        return TAXTYPE_MAP;
    }

    public static TreeMap<String, String> TAXGROUP_MAP = null;

    public static TreeMap<String, String> getTaxGroupMap() {
        if (TAXGROUP_MAP == null) {
            TAXGROUP_MAP = new TreeMap<>();
            TAXGROUP_MAP.put("TIER1", "Tier-1");
            TAXGROUP_MAP.put("TIER2", "Tier-2");
            TAXGROUP_MAP.put("TIER3", "Tier-3");
        }
        return TAXGROUP_MAP;
    }

    public static TreeMap<String, String> PLANCATEGORY_MAP = null;

    public static TreeMap<String, String> getPlanCategoryMap() {
        if (PLANCATEGORY_MAP == null) {
            PLANCATEGORY_MAP = new TreeMap<>();
            PLANCATEGORY_MAP.put("1", "Individual");
            PLANCATEGORY_MAP.put("2", "Group");
        }
        return PLANCATEGORY_MAP;
    }

    public static TreeMap<String, String> QUOTAUNIT_MAP = null;

    public static TreeMap<String, String> getQuotaUnitMap() {
        if (QUOTAUNIT_MAP == null) {
            QUOTAUNIT_MAP = new TreeMap<>();
            QUOTAUNIT_MAP.put("MB", "MB");
            QUOTAUNIT_MAP.put("GB", "GB");
            QUOTAUNIT_MAP.put("Minute", "Minutes");
            QUOTAUNIT_MAP.put("Hour", "Hours");

        }
        return QUOTAUNIT_MAP;
    }

    public static TreeMap<String, String> CHARGETYPE_MAP = null;

    public static TreeMap<String, String> getChargeTypeMap() {
        if (CHARGETYPE_MAP == null) {
            CHARGETYPE_MAP = new TreeMap<>();
            CHARGETYPE_MAP.put(CommonConstants.CHARGE_TYPE_RECURRING, "Recurring");
            CHARGETYPE_MAP.put(CommonConstants.CHARGE_TYPE_NONRECURRING, "Non Recurring");
            CHARGETYPE_MAP.put(CommonConstants.CHARGE_TYPE_ADVANCE, "Advance");
            CHARGETYPE_MAP.put(CommonConstants.CHARGE_TYPE_REFUNDABLE, "Refundable");
            CHARGETYPE_MAP.put(CommonConstants.CHARGE_TYPE_CUSTOMER_DIRECT, "Customer Direct");
        }
        return CHARGETYPE_MAP;
    }


    public static TreeMap<Integer, String> BILLINGCYCLE_MAP = null;

    public static TreeMap<Integer, String> getBillingCycleMap() {
        if (BILLINGCYCLE_MAP == null) {
            BILLINGCYCLE_MAP = new TreeMap<>();
            BILLINGCYCLE_MAP.put(1, "1");
            BILLINGCYCLE_MAP.put(2, "2");
            BILLINGCYCLE_MAP.put(3, "3");
            BILLINGCYCLE_MAP.put(4, "4");
            BILLINGCYCLE_MAP.put(5, "5");
            BILLINGCYCLE_MAP.put(6, "6");
            BILLINGCYCLE_MAP.put(7, "7");
            BILLINGCYCLE_MAP.put(8, "8");
            BILLINGCYCLE_MAP.put(9, "9");
            BILLINGCYCLE_MAP.put(10, "10");
            BILLINGCYCLE_MAP.put(11, "11");
            BILLINGCYCLE_MAP.put(12, "12");

        }
        return BILLINGCYCLE_MAP;
    }

    public static TreeMap<String, String> DISCTYPE_MAP = null;

    public static TreeMap<String, String> getDiscTypeMap() {
        if (DISCTYPE_MAP == null) {
            DISCTYPE_MAP = new TreeMap<>();
            DISCTYPE_MAP.put("percentage", "Percentage");
            DISCTYPE_MAP.put("value", "Value");
        }
        return DISCTYPE_MAP;
    }

    public static TreeMap<String, String> CUSTTYPE_MAP = null;

    public static TreeMap<String, String> getCustTypeMap() {
        if (CUSTTYPE_MAP == null) {
            CUSTTYPE_MAP = new TreeMap<>();
            CUSTTYPE_MAP.put(CommonConstants.CUST_TYPE_PREPAID, "Prepaid");
            CUSTTYPE_MAP.put(CommonConstants.CUST_TYPE_POSTPAID, "Postpaid");
        }
        return CUSTTYPE_MAP;
    }

    public static final String ADDR_TYPE_HOME = "Home";
    public static final String ADDR_TYPE_PAYMENT = "Payment";
    public static final String ADDR_TYPE_PRESENT = "Present";
    public static final String ADDER_TYPE_PERMANENT = "Permanent";
    public static final String ADDR_TYPE_OFFICE = "Office";
    public static final String ADDR_TYPE_OTHER = "Other";
    public static TreeMap<String, String> ADDRESSTYPE_MAP = null;

    public static TreeMap<String, String> getAddressTypeMap() {
        if (ADDRESSTYPE_MAP == null) {
            ADDRESSTYPE_MAP = new TreeMap<>();
            ADDRESSTYPE_MAP.put(ADDR_TYPE_HOME, "Home");
            ADDRESSTYPE_MAP.put(ADDR_TYPE_OFFICE, "Office");
            ADDRESSTYPE_MAP.put(ADDR_TYPE_OTHER, "Other");
        }
        return ADDRESSTYPE_MAP;
    }


    public static TreeMap<Integer, String> BILLDATE_MAP = null;

    public static TreeMap<Integer, String> getBillDateMap() {
        if (BILLDATE_MAP == null) {
            BILLDATE_MAP = new TreeMap<>();
            for (int i = 1; i <= 28; i++) {
                BILLDATE_MAP.put(i, String.valueOf(i));
            }
        }
        return BILLDATE_MAP;
    }

    public static String BILL_PATH = null;

    public static String getBillPath() {
        if (BILL_PATH == null) {
            ClientServiceSrv service = SpringContext.getBean(ClientServiceSrv.class);
            ClientService s1 = service.searchByName(CommonConstants.BILL_PATH_PARAM);
            if (s1 != null) {
                BILL_PATH = s1.getValue();
            }
        }
        return BILL_PATH;
    }

    public static String TRIAL_BILL_PATH = null;

    public static String getTrialBillPath() {
        if (TRIAL_BILL_PATH == null) {
            ClientServiceSrv service = SpringContext.getBean(ClientServiceSrv.class);
            ClientService s1 = service.searchByName(CommonConstants.TRIAL_BILL_PATH_PARAM);
            if (s1 != null) {
                TRIAL_BILL_PATH = s1.getValue();
            }
        }
        return TRIAL_BILL_PATH;
    }

    public static String PAYMENT_PATH = null;

    public static String getPaymentPath() {
        if (PAYMENT_PATH == null) {
            ClientServiceSrv service = SpringContext.getBean(ClientServiceSrv.class);
            ClientService s1 = service.searchByName(CommonConstants.PAYMENT_PATH_PARAM);
            if (s1 != null) {
                PAYMENT_PATH = s1.getValue();
            }
        }
        return PAYMENT_PATH;
    }

    public static TreeMap<String, String> BILLRUNSTATUS_MAP = null;

    public static TreeMap<String, String> getBillRunStatusMap() {
        if (BILLRUNSTATUS_MAP == null) {
            BILLRUNSTATUS_MAP = new TreeMap<>();
            BILLRUNSTATUS_MAP.put("Exported", "Exported");
            BILLRUNSTATUS_MAP.put("Generated", "Generated");
        }
        return BILLRUNSTATUS_MAP;
    }


    public static TreeMap<String, String> PLANTYPE_MAP = null;

    public static TreeMap<String, String> getPostpaidPlanTypeMap() {
        if (PLANTYPE_MAP == null) {
            PLANTYPE_MAP = new TreeMap<>();
            PLANTYPE_MAP.put(CommonConstants.PLAN_TYPE_PREPAID, "Prepaid");
            PLANTYPE_MAP.put(CommonConstants.PLAN_TYPE_POSTPAID, "Postpaid");
        }
        return PLANTYPE_MAP;
    }

    private static InvoiceServer activeInvoiceServer = null;
    private static InvoiceServer activeRadiusServer = null;

    public static InvoiceServer getInvoiceServer() {
        if (activeInvoiceServer == null) {
            InvoiceServerService service = SpringContext.getBean(InvoiceServerService.class);
            activeInvoiceServer = service.findActiveServerDetail(CommonConstants.SERVER_TYPE_BILLING);
        }
        return activeInvoiceServer;
    }

    public static InvoiceServer getRadiusServer() {
        if (activeRadiusServer == null) {
            InvoiceServerService service = SpringContext.getBean(InvoiceServerService.class);
            activeRadiusServer = service.findActiveServerDetail(CommonConstants.SERVER_TYPE_RADIUS);
        }
        return activeRadiusServer;
    }

    public static String PARTNER_BILL_PATH = null;

    public static String getPartnerBillPath() {
        if (PARTNER_BILL_PATH == null) {
            ClientServiceSrv service = SpringContext.getBean(ClientServiceSrv.class);
            ClientService s1 = service.searchByName(CommonConstants.PARTNER_BILL_PATH_PARAM);
            if (s1 != null) {
                PARTNER_BILL_PATH = s1.getValue();
            }
        }
        return PARTNER_BILL_PATH;
    }

    public static TreeMap<String, String> PART_COMM_TYPE_MAP = null;

    public static TreeMap<String, String> getPartnerCommTypes() {
        if (PART_COMM_TYPE_MAP == null) {
            PART_COMM_TYPE_MAP = new TreeMap<>();
            PART_COMM_TYPE_MAP.put(CommonConstants.PART_COMMTYPE_PERCUST_FLAT, "Flat per Customer");
            PART_COMM_TYPE_MAP.put(CommonConstants.PLAN_TYPE_POSTPAID, "Percentage on Invoice");
        }
        return PART_COMM_TYPE_MAP;
    }

    public static Integer PARTNER_ROLE_ID = null;

    public static String getPartnerRoleName() {
            String role = "";
            ClientServiceSrv service = SpringContext.getBean(ClientServiceSrv.class);
            ClientService s1 = service.searchByName(CommonConstants.PARTNER_TYPE_LCO_ROLE);
            if (s1 != null) {
                role = s1.getValue();
            }
            return role;
    }

    public static Integer getPartnerRoleId() {
        if (PARTNER_ROLE_ID == null) {
            ClientServiceSrv service = SpringContext.getBean(ClientServiceSrv.class);
            ClientService s1 = service.searchByName(CommonConstants.PARAM_PARTNER_ROLE_ID);
            if (s1 != null) {
                PARTNER_ROLE_ID = Integer.valueOf(s1.getValue());
            }
        }
        return PARTNER_ROLE_ID;
    }

    public static Integer PARTNER_MANAGER_ROLE_ID = null;

    public static Integer getPartnerManagerRoleId() {
        if (PARTNER_MANAGER_ROLE_ID == null) {
            ClientServiceSrv service = SpringContext.getBean(ClientServiceSrv.class);
            ClientService s1 = service.searchByName(CommonConstants.PARAM_PARTNER_MANAGER_ROLE_ID);
            if (s1 != null) {
                PARTNER_MANAGER_ROLE_ID = Integer.valueOf(s1.getValue());
            }
        }
        return PARTNER_MANAGER_ROLE_ID;
    }

    public static Integer PARTNER_OPERATOR_ROLE_ID = null;

    public static Integer getPartnerOperatorRoleId() {
        if (PARTNER_OPERATOR_ROLE_ID == null) {
            ClientServiceSrv service = SpringContext.getBean(ClientServiceSrv.class);
            ClientService s1 = service.searchByName(CommonConstants.PARAM_PARTNER_OPERATOR_ROLE_ID);
            if (s1 != null) {
                PARTNER_OPERATOR_ROLE_ID = Integer.valueOf(s1.getValue());
            }
        }
        return PARTNER_OPERATOR_ROLE_ID;
    }

    public static PasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();

    public static String generateBcryptPassword(String planPassword) {
        return bcryptEncoder.encode(planPassword);
    }

    public static List<Integer> partnerStaffRoleList = null;

    public static List<Integer> getPartnerStaffRoleIdList() {
        if (partnerStaffRoleList == null) {
            partnerStaffRoleList = new ArrayList<Integer>();
            partnerStaffRoleList.add(getPartnerManagerRoleId());
            partnerStaffRoleList.add(getPartnerOperatorRoleId());
        }
        return partnerStaffRoleList;
    }


    public static TreeMap<String, String> PAYMENT_STATUS_MAP = null;
    public static final String PAYMENT_STATUS_PENDING = "pending";
    public static final String PAYMENT_STATUS_APPROVED = "approved";
    public static final String PAYMENT_STATUS_REJECTED = "rejected";
    public static final String PAYMENT_STATUS_PENDING_APPROVED = "Pending Approved";
    public static final String PAYMENT_STATUS_PARTIAL_APPROVED = "Partial Approved";
    public static final Integer PAYMENT_STATUS_ADVANCED = 0;

    public static final String PAY_TYPE_INVOICE = "invoice";
    public static final String PAYMENT_TYPE = "Payment";

    public static final String CREDIT_NOTE_STATUS_GENERATED = "Generated";
    public static final String CREDIT_NOTE_STATUS_ADJUSTED = "Adjusted";
    public static final String CREDIT_NOTE_STATUS_PARTIALLY_ADJUSTED = "Partially Adjusted";


    public static TreeMap<String, String> getPaymentStatusMap() {
        if (PAYMENT_STATUS_MAP == null) {
            PAYMENT_STATUS_MAP = new TreeMap<>();
            PAYMENT_STATUS_MAP.put(PAYMENT_STATUS_APPROVED, "Approved");
            PAYMENT_STATUS_MAP.put(PAYMENT_STATUS_PENDING, "Pending");
            PAYMENT_STATUS_MAP.put(PAYMENT_STATUS_REJECTED, "Rejected");
            PAYMENT_STATUS_MAP.put(PAYMENT_STATUS_PARTIAL_APPROVED, "Partial Approved");
            PAYMENT_STATUS_MAP.put(PAYMENT_STATUS_PENDING_APPROVED, "Pending Approved");

        }
        return PAYMENT_STATUS_MAP;
    }

    public static TreeMap<String, String> PAYMENT_MODE_MAP = null;
    public static final String PAYMENT_MODE_ONLINE = "Online";
    public static final String PAYMENT_MODE_CASH = "cash";
    public static final String PAYMENT_MODE_CHEQUE = "cheque";
    public static final String PAYMENT_MODE_ADJUST = "adjust";

    public static final String PAYMENT_MODE_DIRECTDEPOSIT = "directdeposit";


    public static final String PAYMENT_MODE_CREDITCARD = "creditcard";

    public static final String PAYMENT_MODE_DEBITCARD= "debitcard";

    public static final String PAYMENT_MODE_NEFT_RTGS = "neft/rtgs";

    public static final String PAYMENT_MODE_EFTs = "efts";

//    public static final String PAYMENT_MODE_DIRECTDEPOSIT = "directdeposit";

    public static final String PAYMENT_MODE_VATRECEIVEABLE = "vatreceiveable";

    public static final String PAYMENT_MODE_NONCASHADJUSTMENT= "noncashadjustment";

    public static final String PAYMENT_MODE_POSADJUSTMENT = "posadjustment";

    public static final String PAYMENT_MODE_QR = "qr";

    public static final String PAYMENT_MODE_OPGADJUSTMENT = "opgadjustment";

    public static final String PAYMENT_MODE_TDS = "tds";

    public static final String ACCOUNT_NUMBER_KEY = "customer:account:number";

    public static TreeMap<String, String> getPaymentModeMap() {
        if (PAYMENT_MODE_MAP == null) {
            PAYMENT_MODE_MAP = new TreeMap<>();
            PAYMENT_MODE_MAP.put(PAYMENT_MODE_CASH, "Cash");
            PAYMENT_MODE_MAP.put(PAYMENT_MODE_ONLINE, "Online");
            PAYMENT_MODE_MAP.put(PAYMENT_MODE_CHEQUE, "Cheque");
        }
        return PAYMENT_MODE_MAP;
    }


    public static long getUniqueNumber() {
        AtomicReference<Long> currentTime = new AtomicReference<>(System.currentTimeMillis());
        Long prev;
        Long next = System.currentTimeMillis();
        do {
            prev = currentTime.get();
            next = next > prev ? next : prev + 1;
        } while (!currentTime.compareAndSet(prev, next));
        return next;
    }


   /* public static String getNewCustomerAccountNo(CustAccountProfile custAccountProfile, Integer mvnoId) {
        String result;
        if(custAccountProfile.getType().equalsIgnoreCase("timestamp")){
            result =timestampType(custAccountProfile.getPrefix(),custAccountProfile.isYear(), custAccountProfile.isMonth(), custAccountProfile.isDay());
        } else if (custAccountProfile.getType().equalsIgnoreCase("number")) {
             result = numberType(custAccountProfile.getPrefix(),custAccountProfile.getStartFrom(),mvnoId);
        }
        else {
            ApplicationLogger.logger.error("Provide Specific Profile Type");
            return null;
        }
        return result;
    }


    public static String timestampType(String prefix, boolean year, boolean month, boolean day){
        try {
            long timestamp = getUniqueNumber();
            LocalDate local = LocalDate.now();
            int count = (year ? 1 : 0) + (month ? 1 : 0) + (day ? 1 : 0);
            switch (count) {
                case 3:
                    return prefix + Constants.SEPARATOR+local.getYear()+ local.getMonthValue() +local.getDayOfMonth()+Constants.SEPARATOR + timestamp;
                case 2:
                    if (year && month)
                        return prefix + Constants.SEPARATOR+local.getYear()+ local.getMonthValue() + Constants.SEPARATOR + timestamp;
                    if (year && day)
                        return prefix + Constants.SEPARATOR+local.getYear()+ local.getDayOfMonth() + Constants.SEPARATOR+  timestamp;
                    if (month && day)
                        return prefix + Constants.SEPARATOR+local.getMonthValue()+ local.getDayOfMonth() + Constants.SEPARATOR+ timestamp;
                case 1:
                    if (year)
                        return prefix + Constants.SEPARATOR+local.getYear()+Constants.SEPARATOR + timestamp;
                    if (month)
                        return prefix + Constants.SEPARATOR+local.getMonthValue()+Constants.SEPARATOR + timestamp;
                    if (day)
                        return prefix + Constants.SEPARATOR+local.getDayOfMonth()+Constants.SEPARATOR + timestamp;
                default:
                    return "No conditions Matched.";
            }
        }catch (Exception e){
            ApplicationLogger.logger.error("Error in performing for processTimestamp during account number generation...");
            e.getStackTrace();
            return null;
        }
    }

    public static String numberType(String prefix, Long startFrom, Integer mvnoId) {
        String accountNumber;
        Long number;
        try {
            // If not in local cache, fallback to Redis
            String redisKey = "customerAccountNumber-" + mvnoId;
            String currentNumber = redisTemplate.opsForValue().get(redisKey);
            // If the key doesn't exist (first-time generation), initialize it to startFrom
            if (currentNumber == null) {
                // Set the initial account number using Redis SETNX or similar command
                redisTemplate.opsForValue().set(redisKey, String.valueOf(startFrom));
                number = startFrom; // Initialize the current number with the startFrom value
            } else {
                // Increment the current account number by 1 for subsequent generations
                number = redisTemplate.opsForValue().increment(redisKey, 1);
            }

            // Generate the account number by concatenating the prefix and the current number
             accountNumber = prefix + Constants.SEPARATOR + number;
        }catch (Exception e){
            ApplicationLogger.logger.error("Error generating account number by Number type for mvnoId : "+mvnoId);
            return null;
        }
       return accountNumber;
    }*/

//	public OkHttpClient getSecureHttpClient() {
//    	
//		OkHttpClient httpClient = null;
//		SSLContext sslContext;
//        TrustManager[] trustManagers;	    
//    	try {
//    		
//    		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            keyStore.load(null, null);
//            InputStream certInputStream = trustStore.getURL().openStream();
//            BufferedInputStream bis = new BufferedInputStream(certInputStream);
//            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//            while (bis.available() > 0) {
//                Certificate cert = certificateFactory.generateCertificate(bis);
//                keyStore.setCertificateEntry("localhost", cert);
//            }
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactory.init(keyStore);
//            trustManagers = trustManagerFactory.getTrustManagers();
//            sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, trustManagers, null);
//
//            
//            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    logger.info("Trust Host :" + hostname);
//                    return true;
//                }
//            };
//            httpClient = new OkHttpClient.Builder()
//            		.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0])
//                   	.protocols(Arrays.asList(okhttp3.Protocol.HTTP_2,okhttp3.Protocol.HTTP_1_1))
//            		.hostnameVerifier(hostnameVerifier)            	
//                    .build();
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//	    return httpClient;
//	}

    public static Map<String, Object> convertJsonToHashMap(String json) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            //Convert Map to JSON
            map = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            map = null;
        }
        return map;
    }

//    public static Long generateId()
//    {
//        String id= LocalDateTime.now().toString()
//                .replace(":","")
//                .replace("-","")
//                .replace(".","")
//                .replace("T","");
//        if(id.length()>17)
//            return Long.valueOf(id.substring(0,16));
//        else
//            return Long.valueOf(id);
//    }

    public static TreeMap<String, String> DUNNING_CREDIT_CLASS_MAP = null;
    public static final String CREDIT_CLASS_GOLD = "gold";
    public static final String CREDIT_CLASS_SILVER = "silver";
    public static final String CREDIT_CLASS_BRONZE = "bronze";

    public static TreeMap<String, String> getCreditClassMap() {
        if (DUNNING_CREDIT_CLASS_MAP == null) {
            DUNNING_CREDIT_CLASS_MAP = new TreeMap<>();
            DUNNING_CREDIT_CLASS_MAP.put(CREDIT_CLASS_GOLD, "Gold");
            DUNNING_CREDIT_CLASS_MAP.put(CREDIT_CLASS_SILVER, "Silver");
            DUNNING_CREDIT_CLASS_MAP.put(CREDIT_CLASS_BRONZE, "Bronze");
        }
        return DUNNING_CREDIT_CLASS_MAP;
    }

    public static TreeMap<String, String> DUNNING_ACTION_MAP = null;
    public static final String DUNNING_ACTION_BLOCK = "block";
    public static final String DUNNING_ACTION_NOTIFY = "notify";

    public static TreeMap<String, String> getDunningActionMap() {
        if (DUNNING_ACTION_MAP == null) {
            DUNNING_ACTION_MAP = new TreeMap<>();
            DUNNING_ACTION_MAP.put(DUNNING_ACTION_BLOCK, "Block");
            DUNNING_ACTION_MAP.put(DUNNING_ACTION_NOTIFY, "Notify");
        }
        return DUNNING_ACTION_MAP;
    }

    public static List<String> PAYMENT_STATUS = Arrays.asList("Initiate", "Success", "Failure");

    public static Long generateId(Long customerId) {

        String id = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy")) + customerId + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hhmm"));
        return Long.parseLong(id);
    }

    public static final String INITIAL_PAYMENT_ADJUST = "0";

    public static Long getDaysForExpiryDateByMonth(Double double1, LocalDate date) {
        Long totalDays = 0l;
        for (int i = 0; i < double1; i++) {
            totalDays = totalDays + date.plusMonths(i).lengthOfMonth();
        }
        return totalDays;
    }

    public static Long getDaysForExpiryDateByYear(Double double1, LocalDate date) {
        Long totalDays = 0l;
        for (int i = 1; i <= double1; i++) {
            totalDays = totalDays + date.plusYears(i).lengthOfYear();
            if(date.isLeapYear()) { //check for leap year
                totalDays = totalDays + 1;
            }
        }
        return totalDays;
    }

    public static int gen() {
        Random r = new Random(System.currentTimeMillis());
        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
    }

    public static String getUpdatedDiff(Object customers22, Object newcust2w) {
        String updated = "";
        try {
            Diff diff = javers.compare(customers22, newcust2w);
            if (diff.hasChanges()) {
                List<Change> changes = diff.getChanges();
                for (Change change : changes) {
                    if (change instanceof ValueChange) {
                        ValueChange valChange = (ValueChange) change;

                        if (!(valChange.getPropertyName().equals("createdOn")
                                || valChange.getPropertyName().equals("lastModifiedOn")
                                || valChange.getPropertyName().equals("createdBy")
                                || valChange.getPropertyName().equals("lastModifiedBy")
                                || valChange.getPropertyName().equals("lastModifiedById")
                                ||valChange.getPropertyName().equals("lastModifiedByName")
                                || valChange.getPropertyName().equals("mvnoId")
                                || valChange.getPropertyName().equals("createdByName")
                                ||valChange.getPropertyName().equals("createdate")
                                ||valChange.getPropertyName().equals("isDelete")
                                || valChange.getPropertyName().equals("updatedate")
                                || valChange.getPropertyName().equals("createdById")

                               )) {
                             if((valChange.getLeft() != valChange.getRight()) && (valChange.getLeft() != null && valChange.getRight() != null)
                                     && (valChange.getLeft() != valChange.getRight())) {
                                    updated = updated + " property: " + valChange.getPropertyName() + " from "
                                            + valChange.getLeft() + " to " + valChange.getRight() ;
                               }
                            }
                       }
                   }
                }
        }
        catch (Exception e)
        {
            return null;
        }
        System.out.println("Javers changes updated >>>>>>>>>>>>>>>>>> "+updated.trim());
        return updated.trim();
    }

    public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static LocalDateTime convertToLocalDateTimeViaSqlTimestamp(Date dateToConvert) {
        return new java.sql.Timestamp(
                dateToConvert.getTime()).toLocalDateTime();
    }

    public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static LocalDateTime convertToLocalDateTimeViaMilisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static boolean isDateInBetweenIncludingEndPoints(final LocalDate min, final LocalDate max, final LocalDate date){
        return !(date.isBefore(min) || date.isAfter(max));
    }

    public static final List<String> POSSIBLE_IP_HEADERS = Arrays.asList(
            "X-Forwarded-For",
            "HTTP_FORWARDED",
            "HTTP_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_CLIENT_IP",
            "HTTP_VIA",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "REMOTE_ADDR",
            "True-Client-IP",
            "CF-Connecting-IP",
            "X-Real-IP",
            "Forwarded",
            "Fastly-Client-IP",
            "X-Original-Forwarded-For"
    );

    public static String getIpAddressFromHeader(HttpServletRequest request) {
        for (String ipHeader : POSSIBLE_IP_HEADERS) {
            if (request!=null) {
                String headerValue = Collections.list(request.getHeaders(ipHeader)).stream()
                        .filter(StringUtils::hasLength)
                        .findFirst()
                        .orElse(null);


                if (headerValue != null && !"0:0:0:0:0:0:0:1".equals(headerValue)) {
                    return headerValue;
                }
            }else {
                return "0:0:0:0:0:0:0:1";
            }
        }


        return request.getRemoteAddr();
    }

    public static int gen6Digit() {
        Random r = new Random(System.currentTimeMillis());
        return ((1 + r.nextInt(2)) * 100000 + r.nextInt(100000));
    }

//    public static <T> List<T> getListFromCache(String key, Class<T> clazz) {
//        Object data = redisTemplates.opsForValue().get(key);
//        if (data == null) return null;
//
//        return objectMapper.convertValue(data, new TypeReference<List<T>>() {});
//    }




}
