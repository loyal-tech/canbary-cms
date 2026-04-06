package com.adopt.apigw.model.postpaid;

import java.util.Date;
import java.util.List;

public class PostpaidInvoiceCharges {

    private List<PostpaidItemCharge> itemCharges;

    private Integer custId;

    private String customerName;

    private String customerType;


    private Date endDate;

    private Double totalDirectChargeAmount;

    private Long invoiceId;
    private String customerUsername;

    private boolean isOrgCust = false;

    private Double totalInvoiceAmount;
    Integer loggedInUserId;

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

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public Integer getCustId() {
        return custId;
    }

    public void setCustId(Integer custId) {
        this.custId = custId;
    }

    public List<PostpaidItemCharge> getItemCharges() {
        return itemCharges;
    }

    public void setItemCharges(List<PostpaidItemCharge> itemCharges) {
        this.itemCharges = itemCharges;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Double getTotalDirectChargeAmount() {
        return totalDirectChargeAmount;
    }

    public boolean isOrgCust() {
        return isOrgCust;
    }

    public void setOrgCust(boolean orgCust) {
        isOrgCust = orgCust;
    }

    public void setTotalDirectChargeAmount(Double totalDirectChargeAmount) {
        this.totalDirectChargeAmount = totalDirectChargeAmount;
    }
}
