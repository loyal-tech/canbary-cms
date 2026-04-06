package com.adopt.apigw.model.postpaid;

import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Data
public class PrepaidInvoiceCharges {

    private List<ItemCharge> itemCharges;

    private Integer custId;

    private String customerName;

    private String customerType;

    private Double totalDirectChargeAmount;

    private Long invoiceId;

    private String customerUsername;

    private boolean isOrgCust = false;

    private Double totalInvoiceAmount;

    Integer loggedInUserId;
    HashSet<Integer> oldDebitDocumentId;

    List<Long> custServiceIds;

    private String creditDocumentId = "creditDocumentId";

    private String isFromFlutterWave = "isFromFlutterWave";

    private String isCaf;
    private Double walletBalance;

    private Long inventoryMappingId;

    private DebitDocument debitDocument;

    private String paymentStatus;

    private Integer billRunId;

    private String createdByName;
    List<Map.Entry<Integer, Long>> CustPackAndDebitDocIdPair ;

    Double adjustedAmount;

    String billRunStatus;

    Boolean isPaymentApproved;

    Boolean isVoid;

    private Boolean  isDirectChargeInvoice;

    private String nextBilldate;

    private List<Integer> chargeIds;

    List<Map.Entry<Integer, String>> CustPackAndEndDatePair;
    List<Map.Entry<Integer, String>>  childIdNextBillDatePair;

    public List<Map.Entry<Integer, Long>> getCustPackAndDebitDocIdPair() {
        return CustPackAndDebitDocIdPair;
    }


    public void setCustPackAndDebitDocIdPair(List<Map.Entry<Integer, Long>> custPackAndDebitDocIdPair) {
        CustPackAndDebitDocIdPair = custPackAndDebitDocIdPair;
    }


    public String getIsCaf() {
        return isCaf;
    }

    public void setIsCaf(String isCaf) {
        this.isCaf = isCaf;
    }


    public String getCreditDocumentId() {
        return creditDocumentId;
    }

    public void setCreditDocumentId(String creditDocumentId) {
        this.creditDocumentId = creditDocumentId;
    }

    public String getIsFromFlutterWave() {
        return isFromFlutterWave;
    }

    public void setIsFromFlutterWave(String isFromFlutterWave) {
        this.isFromFlutterWave = isFromFlutterWave;
    }

    public HashSet<Integer> getOldDebitDocumentId() {
        return oldDebitDocumentId;
    }

    public void setOldDebitDocumentId(HashSet<Integer> oldDebitDocumentId) {
        this.oldDebitDocumentId = oldDebitDocumentId;
    }


    public Integer getLoggedInUserId() {
        return loggedInUserId;
    }

    public void setLoggedInUserId(Integer loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
    }

    public Double getTotalInvoiceAmount() {
        return totalInvoiceAmount;
    }

    public void setTotalInvoiceAmount(Double totalInvoiceAmount) {
        this.totalInvoiceAmount = totalInvoiceAmount;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }


    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getCustId() {
        return custId;
    }

    public void setCustId(Integer custId) {
        this.custId = custId;
    }

    public List<ItemCharge> getItemCharges() {
        return itemCharges;
    }

    public void setItemCharges(List<ItemCharge> itemCharges) {
        this.itemCharges = itemCharges;
    }

    public Double getTotalDirectChargeAmount() {
        return totalDirectChargeAmount;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setTotalDirectChargeAmount(Double totalDirectChargeAmount) {
        this.totalDirectChargeAmount = totalDirectChargeAmount;
    }

    public boolean isOrgCust() {
        return isOrgCust;
    }

    public void setOrgCust(boolean orgCust) {
        isOrgCust = orgCust;
    }

    public Long getInventoryMappingId() {
        return inventoryMappingId;
    }

    public void setInventoryMappingId(Long inventoryMappingId) {
        this.inventoryMappingId = inventoryMappingId;
    }

    public List<Long> getCustServiceIds() {
        return custServiceIds;
    }

    public void setCustServiceIds(List<Long> custServiceIds) {
        this.custServiceIds = custServiceIds;
    }

    public List<Integer> getChargeIds() {
        return chargeIds;
    }

    public void setChargeIds(List<Integer> chargeIds) {
        this.chargeIds = chargeIds;
    }

    public List<Map.Entry<Integer, String>> getCustPackAndEndDatePair() {
        return CustPackAndEndDatePair;
    }

    public void setCustPackAndEndDatePair(List<Map.Entry<Integer, String>> custPackAndEndDatePair) {
        CustPackAndEndDatePair = custPackAndEndDatePair;
    }
}
