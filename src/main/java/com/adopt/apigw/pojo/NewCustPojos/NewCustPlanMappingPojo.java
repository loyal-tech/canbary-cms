package com.adopt.apigw.pojo.NewCustPojos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class NewCustPlanMappingPojo {
    @NotNull
    private Integer planId;
    private String service;
    private String billTo = "CUSTOMER";
    private String custPlanStatus = "Active";
    private Double discount;
    private Boolean isInvoiceToOrg = false;
    private Integer billableCustomerId=null;
    private Integer custServiceMappingId;
    private String planName;
    private String billableAddress;
    private LocalDateTime startDate;
    private LocalDateTime expiryDate;
    private Integer vasPackId;

    public NewCustPlanMappingPojo(Integer planId, String service, String billTo, String custPlanStatus, Double discount, Boolean isInvoiceToOrg, Integer billableCustomerId, Integer custServiceMappingId) {
        this.planId = planId;
        this.service = service;
        this.billTo = billTo;
        this.custPlanStatus = custPlanStatus;
        this.discount = discount;
        this.isInvoiceToOrg = isInvoiceToOrg;
        this.billableCustomerId = billableCustomerId;
        this.custServiceMappingId = custServiceMappingId;
    }

    public NewCustPlanMappingPojo(Integer planId, String service, String billTo, String custPlanStatus, Double discount, Boolean isInvoiceToOrg, Integer billableCustomerId, Integer custServiceMappingId,LocalDateTime startDate , LocalDateTime expiryDate,Integer vasPackId) {
        this.planId = planId;
        this.service = service;
        this.billTo = billTo;
        this.custPlanStatus = custPlanStatus;
        this.discount = discount;
        this.isInvoiceToOrg = isInvoiceToOrg;
        this.billableCustomerId = billableCustomerId;
        this.custServiceMappingId = custServiceMappingId;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.vasPackId = vasPackId;
    }
}
