package com.adopt.apigw.modules.ChangePlanDTOs;

import com.adopt.apigw.model.postpaid.CustomerChargeHistory;
import lombok.Data;

@Data
public class CustomerChargeHistoryRevenue {

    private Integer id;

    private Integer customerId;

    private Integer planId;
    private Integer chargeId;
    private Integer taxId;
    private Double chargeAmount;
    private Double taxAmount;
    private Double discount = 0.0;
    private Integer custPlanMapppingId;
    private Integer planGroupId;
    private String planName;

    private String chargeName;

    private String charge_desc;

    private String chargeType;

    private Integer billingCycle;

    private Integer customerBillDay;

    private Boolean isFirstChargeApply;

    private Boolean isRoyaltyApply=false;

    private String nextBillDate;

    private String lastBillDate;

    public CustomerChargeHistoryRevenue(CustomerChargeHistory customerChargeHistory){
        this.id =customerChargeHistory.getId();
        this.customerId =customerChargeHistory.getCustomerId();
        this.planId =customerChargeHistory.getPlanId();
        this.chargeId =customerChargeHistory.getChargeId();
        this.taxId =customerChargeHistory.getTaxId();
        this.chargeAmount =customerChargeHistory.getChargeAmount();
        this.taxAmount =customerChargeHistory.getTaxAmount();
        this.discount =customerChargeHistory.getDiscount();
        this.custPlanMapppingId =customerChargeHistory.getCustPlanMapppingId();
        this.planGroupId =customerChargeHistory.getPlanGroupId();
        this.planName =customerChargeHistory.getPlanName();
        this.chargeName =customerChargeHistory.getChargeName();
        this.charge_desc =customerChargeHistory.getCharge_desc();
        this.chargeType =customerChargeHistory.getChargeType();
        this.billingCycle =customerChargeHistory.getBillingCycle();
        this.customerBillDay =customerChargeHistory.getCustomerBillDay();
        this.isFirstChargeApply =customerChargeHistory.getIsFirstChargeApply();
        this.isRoyaltyApply =customerChargeHistory.getIsRoyaltyApply();
        if (customerChargeHistory.getNextBillDate()!=null) {
            this.nextBillDate = customerChargeHistory.getNextBillDate().toString();
        }
        if (customerChargeHistory.getLastBillDate()!=null) {
            this.lastBillDate = customerChargeHistory.getLastBillDate().toString();
        }
    }

}
