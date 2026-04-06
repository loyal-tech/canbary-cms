package com.adopt.apigw.rabbitMq.message;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class NewCprMessage {
    private String messageId;
    private String message;
    private Date messageDate;

    private Long custPackageId;

    private Long customerId;

    private Long planId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    public NewCprMessage(Long custPackageId, LocalDateTime startDate,LocalDateTime endDate,Long customerId,Long planId)
    {
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer's used data updates";
        this.custPackageId=custPackageId;
        this.startDate=startDate;
        this.endDate=endDate;
        this.customerId=customerId;
        this.planId=planId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public Long getCustPackageId() {
        return custPackageId;
    }

    public void setCustPackageId(Long custPackageId) {
        this.custPackageId = custPackageId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
