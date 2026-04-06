package com.adopt.apigw.modules.subscriber.model;

import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.pojo.api.CreditDocumentPaymentPojo;
import com.adopt.apigw.pojo.api.CustPlanMapppingPojo;
import com.adopt.apigw.pojo.api.NewPlanBindWithOldPlan;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

@Data
public class ChangePlanRequestDTO extends UpdateAbstarctDTO {
    private Integer custId;
    private Integer planId;
    private String purchaseType;
    private Boolean isAdvRenewal;
    private Boolean isRefund;
    private String remarks;
    private Boolean isPaymentReceived;
    private RecordPaymentRequestDTO recordPaymentDTO;
    private LocalDateTime addonStartDate;
    private LocalDateTime addonEndDate;
    private Double walletBalUsed = 0.0;
    private Long purchaseId = null;
    private String purchaseFrom;
    private String onlinePurType;
    private Double discount;

    private Integer planGroupId;

    private LocalDateTime createdate;

    private LocalDateTime updatedate;

    private String createdByName;

    private String lastModifiedByName;

    private Integer createdById;

    private Integer lastModifiedById;

    private Integer custServiceMappingId;

    private List<PostpaidPlan> planList;
    
//    private Double discount;

    private List<Integer> newPlanList;

    private String bindWithOldPlanId;

    private List<NewPlanBindWithOldPlan> planBindWithOldPlans;

//    @JsonManagedReference
    private List<CustPlanMapppingPojo> planMappingList = new ArrayList<>();

    private String paymentOwner;

    private Integer billableCustomerId=null;

    private Integer paymentOwnerId;

    private Boolean isParent;

    private Long voucherId;

    private Boolean isTriggerCoaDm;

    private Boolean isAutoPaymentRequired;

    private List<CreditDocumentPaymentPojo> creditDocumentPaymentPojoList;

    public ChangePlanRequestDTO() {
    }

    public ChangePlanRequestDTO(Integer custId, Integer planId, String purchaseType, Boolean isAdvRenewal, Boolean isRefund, String remarks,Double walletBalUsed,Long purchaseId,String purchaseFrom,String onlinePurType,List<NewPlanBindWithOldPlan> planBindWithOldPlans,String bindWithOldPlanId) {
        this.custId = custId;
        this.planId = planId;
        this.purchaseType = purchaseType;
        this.isAdvRenewal = isAdvRenewal;
        this.isRefund = isRefund;
        this.remarks = remarks;
        this.walletBalUsed = walletBalUsed;
        this.purchaseId=purchaseId;
        this.purchaseFrom = purchaseFrom;
        this.onlinePurType=onlinePurType;
        this.planBindWithOldPlans=planBindWithOldPlans;
        this.bindWithOldPlanId=bindWithOldPlanId;
    }
}
