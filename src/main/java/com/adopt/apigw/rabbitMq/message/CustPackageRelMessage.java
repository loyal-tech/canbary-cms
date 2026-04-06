package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.model.postpaid.CustQuotaDetails;
import com.adopt.apigw.modules.planUpdate.model.CustomerPackageDTO;
import com.adopt.apigw.pojo.api.CustPlanMapppingPojo;
import com.adopt.apigw.pojo.api.CustQuotaDtlsPojo;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Data
//@AllArgsConstructor
//@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = CustPackageRelMessage.class)
public class CustPackageRelMessage {

    //Customer Package
    private static final String ADOPT_API_GATEWAY = "ADOPT_API_GATEWAY";
    private static final String ID = "id";
    private static final String CUST_ID = "custid";
    private static final String PLAN_ID = "planId";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String EXPIRY_DATE = "expiryDate";
    private static final String STATUS = "status";
    private static final String QOS_POLICY_ID = "qospolicyId";
    private static final String UPLOAD_QOS = "uploadqos";
    private static final String DOWNLOAD_QOS = "downloadqos";
    private static final String UPLOAD_TS = "uploadts";
    private static final String DOWNLOAD_TS = "downloadts";
    private static final String QUOTA_LIST = "quotaList";
    private static final String SERVICE = "service";
    private static final String IS_DELETE = "isDelete";
    private static final String OFFER_PRICE = "offerPrice";
    private static final String TAX_AMOUNT = "taxAmount";
    private static final String CREDIT_DOC_ID = "creditdocid";
    private static final String WALLET_BAL_USED = "walletBalUsed";
    private static final String PURCHASE_TYPE = "purchaseType";
    private static final String ONLINE_PURCHASE_ID = "onlinePurchaseId";
    private static final String PURCHASE_FROM = "purchaseFrom";
    private static final String DEBIT_DOC_ID = "debitdocid";
    private static final String VALIDITY = "validity";
    private static final String MVNO_ID = "mvnoId";

    //Quota Details
//    private static final String ADOPT_API_GATEWAY = "Adopt Api Gateway";
//    private static final String ID = "id";
//    private static final String PLAN_ID = "planId";
    private static final String QUOTA_TYPE = "quotaType";
    private static final String TOTAL_QUOTA = "totalQuota";
    private static final String USED_QUOTA = "usedQuota";
    private static final String QUOTA_UNIT = "quotaUnit";
    private static final String TIME_TOTAL_QUOTA = "timeTotalQuota";
    private static final String TIME_QUOTA_USED = "timeQuotaUsed";
    private static final String TIME_QUOTA_UNIT = "timeQuotaUnit";
//    private static final String IS_DELETE = "isDelete";
    private static final String TOTAL_QUOTA_KB = "totalQuotaKB";
    private static final String USED_QUOTA_KB = "usedQuotaKB";
    private static final String TIME_USED_QUOTA_SEC = "timeUsedQuotaSec";
    private static final String TIME_TOTAL_QUOTA_SEC = "timeTotalQuotaSec";
    private static final String DID_TOTAL_QUOTA = "didtotalquota";
    private static final String DID_USED_QUOTA = "didusedquota";
    private static final String INTERCOM_TOTAL_QUOTA = "intercomtotalquota";
    private static final String INTERCOM_USED_QUOTA = "intercomusedquota";
    private static final String DID_QUOTA_UNIT = "didQuotaUnit";
    private static final String INTERCOM_QUOTA_UNIT = "intercomQuotaUnit";
    private static final String PLAN_NAME = "planName";
    private static final String CUST_PLAN_MAPPPING = "custPlanMappping";
    private static final String CUSTOMER = "customer";
    private static final String CREATED_BY_STAFF_ID="createdByStaffId";
    private static final String LAST_MODIFIED_BY_STAFF_ID="lastModifiedByStaffId";
    private static final String CREATED_BY_NAME="createdByName";
    private static final String UPDATED_BY_NAME="updatedByName";

    private static final String GRACE_DAYS="graceDays";

    private static final String CUST_PLAN_STATUS = "custPlanStatus";

    /**new attribute added**/
    private static final String DISCOUNT = "discount";
    private static final String ISINVOICESTOP = "isinvoicestop";
    private static final String ISTRIALPLAN = "istrialplan";
    private static final String DBR = "dbr";
    private static final String ISINVOICETOORG ="isInvoiceToOrg";
    private static final String BILLTo ="billTo";
    private static final String NEXTAPPORVER ="nextApprover";
    private static final String DEBITDOCID = "debitdocid";
    private static final String NEXTSTAFF= "nextStaff";
    private static final String TRAILDEBITDOCID = "traildebitdocid";
    private static final String ISTRIALVALIDITYDAYS = "isTrialValidityDays";
    private static final String TRIALPLANVALIDTIYCOUNT ="trialPlanValidityCount";
    private static final String CUST_SERVICE_MAPPING_ID = "custServiceMappingId";

    private static final String CUST_INVOICE_TYPE = "invoiceType";
    private static  final String isChunkAvailable = "isChunkAvailable";
    private static  final String reservedQuotaInPer = "reservedQuotaInPer";
    private static  final String totalReservedQuota = "totalReservedQuota";

    private static  final String parentQuotaType = "parentQuotaType";
    private static  final String usageQuotaType = "usageQuotaType";
    private static final String SKIP_QUOTA_UPDATE = "skipQuotaUpdate";
    private static final String NEXTQUOTARESETDATE="nextQuotaResetDate";


    private String messageId;
    private String message;
    private String operation;
    private String sourceName;
    private Date messageDate;
    private String currentUser;
    private boolean isCustomerCreated;
    private Map<String, Object> data;
    private boolean isTriggerCoaDm;
    private boolean ignoreOnCreate;

    public CustPackageRelMessage(){}

    public CustPackageRelMessage (CustPlanMapppingPojo custPlanMappping,String operation){
        Map<String, Object> map = new HashMap<>();
        map.put(ID, custPlanMappping.getId());
        map.put(CUST_ID, custPlanMappping.getCustid());
        map.put(PLAN_ID, custPlanMappping.getPlanId());
        if(custPlanMappping.getCustServiceMappingId() != null){
            map.put(CUST_SERVICE_MAPPING_ID , custPlanMappping.getCustServiceMappingId());
        }
        if(custPlanMappping.getStartDate() != null)
            map.put(START_DATE, custPlanMappping.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        if(custPlanMappping.getEndDate() != null)
            map.put(END_DATE, custPlanMappping.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        if(custPlanMappping.getExpiryDate() != null)
            map.put(EXPIRY_DATE, custPlanMappping.getExpiryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        map.put(STATUS, custPlanMappping.getStatus());
        map.put(QOS_POLICY_ID, custPlanMappping.getQospolicyId());
        map.put(UPLOAD_QOS, custPlanMappping.getUploadqos());
        map.put(DOWNLOAD_QOS, custPlanMappping.getDownloadqos());
        map.put(UPLOAD_TS, custPlanMappping.getUploadts());
        map.put(DOWNLOAD_TS, custPlanMappping.getDownloadts());
        map.put(SERVICE, custPlanMappping.getService());
        map.put(OFFER_PRICE, custPlanMappping.getOfferPrice());
        map.put(TAX_AMOUNT, custPlanMappping.getTaxAmount());
        map.put(CREDIT_DOC_ID, custPlanMappping.getCreditdocid());
        map.put(WALLET_BAL_USED, custPlanMappping.getWalletBalUsed());
        map.put(PURCHASE_TYPE, custPlanMappping.getPurchaseType());
        map.put(ONLINE_PURCHASE_ID, custPlanMappping.getOnlinePurchaseId());
        map.put(PURCHASE_FROM, custPlanMappping.getPurchaseFrom());
        map.put(DEBIT_DOC_ID, custPlanMappping.getDebitdocid());
        map.put(VALIDITY, custPlanMappping.getValidity());
        map.put(IS_DELETE, custPlanMappping.getIsDelete());
        map.put(CUST_PLAN_STATUS, custPlanMappping.getCustPlanStatus());
        if(custPlanMappping.getDiscount() != null){
            map.put(DISCOUNT ,custPlanMappping.getDiscount());
        }
        if(custPlanMappping.getIstrialplan() != null){
            map.put(ISTRIALPLAN ,custPlanMappping.getIstrialplan());
        }
        if(custPlanMappping.getIsinvoicestop() != null){
            map.put(ISINVOICESTOP ,custPlanMappping.getIsinvoicestop());
        }
        if(custPlanMappping.getIsInvoiceToOrg() != null){
            map.put(ISINVOICETOORG ,custPlanMappping.getIsInvoiceToOrg());
        }
        if(custPlanMappping.getBillTo() != null){
            map.put(BILLTo ,custPlanMappping.getBillTo());
        }

        if(custPlanMappping.getDebitdocid()!= null){
            map.put(DEBITDOCID ,custPlanMappping.getDebitdocid());
        }
        if(custPlanMappping.getIsTrialValidityDays()!= null){
            map.put(ISTRIALVALIDITYDAYS,custPlanMappping.getTraildebitdocid());
        }
        if(custPlanMappping.getTraildebitdocid()!= null){
            map.put(TRAILDEBITDOCID ,custPlanMappping.getTraildebitdocid());
        }
        if(custPlanMappping.getTrialPlanValidityCount()!= null){
            map.put(TRIALPLANVALIDTIYCOUNT ,custPlanMappping.getTrialPlanValidityCount());
        }

        map.put(CUST_INVOICE_TYPE,custPlanMappping.getInvoiceType());
        if(custPlanMappping.getNextQuotaResetDate() != null)
            map.put(NEXTQUOTARESETDATE, custPlanMappping.getNextQuotaResetDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//        map.put(MVNO_ID, custPlanMappping.getMvnoId());
//        map.put(QUOTA_LIST, custPlanMappping.getQuotaList());
        List<Map> custQuotaDtlsPojoList = new ArrayList<>();
//        List<CustQuotaDetails> custQuotaDetailsList = custQuotaService.convertQuotaPojoListToQuotaDomainList(custPlanMappping.getQuotaList());
        for(CustQuotaDtlsPojo custQuotaDtlsPojo : custPlanMappping.getQuotaList()){
            Map<String, Object> quotaMap = new HashMap<>();
            quotaMap.put(ID, custQuotaDtlsPojo.getId());
            quotaMap.put(PLAN_ID, custQuotaDtlsPojo.getPlanId());
            quotaMap.put(QUOTA_TYPE, custQuotaDtlsPojo.getQuotaType());
            quotaMap.put(TOTAL_QUOTA, custQuotaDtlsPojo.getTotalQuota());
            quotaMap.put(USED_QUOTA, custQuotaDtlsPojo.getUsedQuota());
            quotaMap.put(QUOTA_UNIT, custQuotaDtlsPojo.getQuotaUnit());
            quotaMap.put(TIME_TOTAL_QUOTA, custQuotaDtlsPojo.getTimeTotalQuota());
            quotaMap.put(TIME_QUOTA_USED, custQuotaDtlsPojo.getTimeQuotaUsed());
            quotaMap.put(TIME_QUOTA_UNIT, custQuotaDtlsPojo.getTimeQuotaUnit());
            quotaMap.put(IS_DELETE, custQuotaDtlsPojo.getIsDelete());
            quotaMap.put(TOTAL_QUOTA_KB, custQuotaDtlsPojo.getTotalQuotaKB());
            quotaMap.put(USED_QUOTA_KB, custQuotaDtlsPojo.getUsedQuotaKB());
            quotaMap.put(TIME_TOTAL_QUOTA_SEC, custQuotaDtlsPojo.getTimeTotalQuotaSec());
            quotaMap.put(TIME_USED_QUOTA_SEC, custQuotaDtlsPojo.getTimeUsedQuotaSec());
            quotaMap.put(DID_TOTAL_QUOTA, custQuotaDtlsPojo.getDidtotalquota());
            quotaMap.put(DID_USED_QUOTA, custQuotaDtlsPojo.getDidusedquota());
            quotaMap.put(INTERCOM_TOTAL_QUOTA, custQuotaDtlsPojo.getIntercomtotalquota());
            quotaMap.put(INTERCOM_USED_QUOTA, custQuotaDtlsPojo.getIntercomusedquota());
            quotaMap.put(DID_QUOTA_UNIT, custQuotaDtlsPojo.getDidQuotaUnit());
            quotaMap.put(INTERCOM_QUOTA_UNIT, custQuotaDtlsPojo.getIntercomQuotaUnit());
            quotaMap.put(PLAN_NAME, custQuotaDtlsPojo.getPlanName());
            quotaMap.put(CUST_PLAN_MAPPPING, custQuotaDtlsPojo.getCustPlanMappping());
            quotaMap.put(CUSTOMER, custQuotaDtlsPojo.getCustomer() != null ? custQuotaDtlsPojo.getCustomer().getId() : null);
            quotaMap.put(CREATED_BY_STAFF_ID, custQuotaDtlsPojo.getCreatedById());
            quotaMap.put(LAST_MODIFIED_BY_STAFF_ID, custQuotaDtlsPojo.getLastModifiedById());
            quotaMap.put(CREATED_BY_NAME, custQuotaDtlsPojo.getCreatedByName());
            quotaMap.put(isChunkAvailable, custQuotaDtlsPojo.isChunkAvailable());
            quotaMap.put(reservedQuotaInPer, custQuotaDtlsPojo.getReservedQuotaInPer());
            quotaMap.put(totalReservedQuota, custQuotaDtlsPojo.getTotalReservedQuota());
            quotaMap.put(usageQuotaType, custQuotaDtlsPojo.getUsageQuotaType());
            quotaMap.put(SKIP_QUOTA_UPDATE, custQuotaDtlsPojo.getSkipQuotaUpdate());
            if(custQuotaDtlsPojo.getParentQuotaType() != null){
                quotaMap.put(parentQuotaType , custQuotaDtlsPojo.getParentQuotaType());
            }
            custQuotaDtlsPojoList.add(quotaMap);

        }
        map.put("quotaDtls", custQuotaDtlsPojoList);
        map.put(GRACE_DAYS, custPlanMappping.getGraceDays());
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer Package rel from Api Gateway";
        this.data = map;
        this.sourceName = ADOPT_API_GATEWAY;
        this.operation=operation;
    }
}
