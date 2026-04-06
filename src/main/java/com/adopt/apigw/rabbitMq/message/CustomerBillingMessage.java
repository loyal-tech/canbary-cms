package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.pojo.api.RecordPaymentPojo;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = CustomerBillingMessage.class)
public class CustomerBillingMessage {

    private static final String BILLING = "billing";
    private static final String CUST_ID = "custId";
    private static final String STR_DATE = "strDate";
    private static final String PACKAGE_REL_ID = "packageRelId";
    private static final String CHARGE_ID = "chargeId";
    private static final String INVENTORY_MAPPING_ID = "inventoryMappingId";
    private static final String currentUserLoggedInId = "currentUserLoggedInId";
    private static final String CREATED_BY_NAME="createdByName";
    private static final String UPDATE_BY_NAME="updateByName";
    private static final String PARTNER_LEDGER_DTLS_ID = "partnerLedgerMappingId";
    private static final String oldDebitDocId = "oldDebitDocId";
    private static final String creditDocumentId = "creditDocumentId";
    private static final String isFromFlutterWave = "isFromFlutterWave";
    private static final String PAYMENT_OWNER = "paymentOwner";

    private static final String PAYMENT_OWNER_ID = "paymentOwnerId";

    private static final String INVOICE_TYPE = "invoiceType";

    public static final String RENEWAL_ID = "renewalId";


    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private String currentUser;
    private String type;
    private String createdByName;
    private String updateByName;
    private String paymentOwner;
    private Integer paymentOwnerId;
    private List<Integer> childIds;
    private List<Integer> custChargeIds;
    private RecordPaymentPojo recordPaymentDTO;

    private Map<String, Object> data;

    private Integer loggedInStaffId;

    Integer oldDebitDocumentId;

    Boolean isCaf;
    private Integer renewalId;

    public CustomerBillingMessage() {
    }

    public CustomerBillingMessage(String strDate, Integer custId, Integer packageRelId, String chargeId, Long inventoryMappingId, Long partnerLedgerMappingId, Integer loggedInStaffId, HashSet<Integer> oldDebitDocumentId,String createdByName,String updateByName , String creditDocId , String isFromFlutter , String paymentowner,Integer paymentOwnerId) {
        Map<String, Object> map = new HashMap<>();
        map.put(CUST_ID, custId);
        map.put(STR_DATE, strDate);
        map.put(PACKAGE_REL_ID, packageRelId);
        map.put(PARTNER_LEDGER_DTLS_ID, partnerLedgerMappingId);
        map.put(CHARGE_ID, chargeId);
        map.put(INVENTORY_MAPPING_ID, inventoryMappingId);
        map.put(currentUserLoggedInId, loggedInStaffId);
        map.put(RENEWAL_ID, renewalId);
        if (createdByName!=null && createdByName.length()!=0) {
            map.put(CREATED_BY_NAME, createdByName);
        }else{
            map.put(CREATED_BY_NAME, "");
        }
        if (updateByName!=null && updateByName.length()!=0) {
            map.put(UPDATE_BY_NAME,updateByName);
        }else{
            map.put(UPDATE_BY_NAME, "");
        }

        if (!CollectionUtils.isEmpty(oldDebitDocumentId)) {
            String oldDebitDocumentIdStr = "";
            for (Integer invoiceNo: oldDebitDocumentId) {
                oldDebitDocumentIdStr = oldDebitDocumentIdStr+invoiceNo+",";
            }
            oldDebitDocumentIdStr = oldDebitDocumentIdStr.substring(0, oldDebitDocumentIdStr.length() - 1);
            map.put(oldDebitDocId, oldDebitDocumentIdStr);
        }else {
            map.put(oldDebitDocId, "");
        }
        if(creditDocId != null && creditDocId.length() != 0){
            map.put(creditDocumentId , creditDocId);
        }
        else
        {
            map.put(creditDocumentId , "");
        }
        if(isFromFlutter != null && isFromFlutter.length() != 0){
            map.put(isFromFlutterWave , isFromFlutter);
        }
        else
        {
            map.put(isFromFlutterWave , "");
        }
        if(paymentowner != null && paymentowner.length() != 0){
            map.put(PAYMENT_OWNER , paymentowner);
        }
        else
        {
            map.put(PAYMENT_OWNER , "");
        }
        if (paymentOwnerId != null && !paymentOwnerId.equals("")){
            map.put(PAYMENT_OWNER_ID , paymentOwnerId);
        }else {
            map.put(PAYMENT_OWNER_ID, "-1");
        }
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer Billing Engine from Api Gateway";
        this.data = map;
        this.sourceName = BILLING;

    }
}
