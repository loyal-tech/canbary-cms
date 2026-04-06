package com.adopt.apigw.modules.ChangePlanDTOs;

import com.adopt.apigw.model.postpaid.CustPlanMappping;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CustPlanMappingRevenue {

    private Integer id;

    private Integer planId;
    private String service;
    private String startDate;
    private String endDate;
    private String expiryDate;
    private String status;
    private Integer customerId;

    private Double offerPrice;
    private Double taxAmount;
    private Double walletBalUsed = 0.0;
    private String purchaseType;
    private Long onlinePurchaseId;
    private String purchaseFrom;

    private Integer billableCustomerId;

    private Boolean isinvoicestop = false;

    private Boolean istrialplan = false;


    private Long debitdocid;

    private Boolean isDelete;


    private Double discount;

    private Integer planValidityDays;

    private Integer planGroupId;
    private Boolean isInvoiceToOrg;

    private String billTo;

    private Double newAmount;

    private Integer renewalId;

    private Integer custRefId;

    private  String custrefName;

    private Integer nextStaff;

    private Double dbr;

    private String custPlanStatus;

    private Integer nextTeamHierarchyMappingId;

    private Boolean isInvoiceCreated;

    private Double oldDiscount;

    private Integer graceDays = 0;

    private String stopServiceDate;

    private Integer custServiceMappingId;


    private String graceDateTime;


    private String invoiceType;

    private Long traildebitdocid;


    private Long promisetopay_renew_count;


    private Double isTrialValidityDays;

    private Integer trialPlanValidityCount =0;

    private String startServiceDate;

    private String serviceHoldDate;

    private String promise_to_pay_startdate;;

    private String promise_to_pay_enddate;

    private Integer totalHoldDays;

    private String discountType="One-time";

    private String discountExpiryDate;


    private String downTimeExpiryDate;

    private String downTimeStartDate;

    private Integer cprIdForPromiseToPay;

    private Boolean isHold;

    private Boolean isVoid;


    private String serviceHoldBy;

    private String serviceStartBy;

    private Boolean isContainsCustomerInvoice;

    private Integer customerCpr;

    private Long creditdocid;

    private Integer serviceId;

    private String serialNumber;

    private Boolean renewalForBooster;

    private Integer vasId;


    public CustPlanMappingRevenue(CustPlanMappping custPlanMapppingList){
        this.id =custPlanMapppingList.getId();
        this.planId =custPlanMapppingList.getPlanId();
        this.service =custPlanMapppingList.getService();
        this.startDate =custPlanMapppingList.getStartDate().toString();
        this.endDate =custPlanMapppingList.getEndDate().toString();
        this.expiryDate =custPlanMapppingList.getExpiryDate().toString();
        this.status =custPlanMapppingList.getStatus();
        this.customerId =custPlanMapppingList.getCustomer().getId();
        this.offerPrice =custPlanMapppingList.getOfferPrice();
        this.taxAmount =custPlanMapppingList.getTaxAmount();
        this.walletBalUsed =custPlanMapppingList.getWalletBalUsed();
        this.purchaseType =custPlanMapppingList.getPurchaseType();
        this.onlinePurchaseId =custPlanMapppingList.getOnlinePurchaseId();
        this.purchaseFrom =custPlanMapppingList.getPurchaseFrom();
        this.billableCustomerId =custPlanMapppingList.getBillableCustomerId();
        this.isinvoicestop =custPlanMapppingList.getIsinvoicestop();
        this.istrialplan =custPlanMapppingList.getIstrialplan();
        this.debitdocid =custPlanMapppingList.getDebitdocid();
        this.isDelete =custPlanMapppingList.getIsDelete();
        this.discount =custPlanMapppingList.getDiscount();
        this.planValidityDays =custPlanMapppingList.getPlanValidityDays();
        if(custPlanMapppingList.getPlanGroup()!=null) {
            this.planGroupId = custPlanMapppingList.getPlanGroup().getPlanGroupId();
        }
        this.isInvoiceToOrg =custPlanMapppingList.getIsInvoiceToOrg();
        this.billTo =custPlanMapppingList.getBillTo();
        this.newAmount =custPlanMapppingList.getNewAmount();
        this.renewalId =custPlanMapppingList.getRenewalId();
        this.custRefId =custPlanMapppingList.getCustRefId();
        this.custrefName = custPlanMapppingList.getCustRefName();
        this.nextStaff =custPlanMapppingList.getNextStaff();
        this.dbr =custPlanMapppingList.getDbr();
        this.custPlanStatus =custPlanMapppingList.getCustPlanStatus();
        this.nextTeamHierarchyMappingId =custPlanMapppingList.getNextTeamHierarchyMappingId();
        this.isInvoiceCreated =custPlanMapppingList.getIsInvoiceCreated();
        this.oldDiscount =custPlanMapppingList.getOldDiscount();
        this.graceDays =custPlanMapppingList.getGraceDays();
        if(custPlanMapppingList.getStopServiceDate()!=null) {
            this.stopServiceDate = custPlanMapppingList.getStopServiceDate().toString();
        }
        this.custServiceMappingId =custPlanMapppingList.getCustServiceMappingId();
        if(custPlanMapppingList.getGraceDateTime()!=null) {
            this.graceDateTime = custPlanMapppingList.getGraceDateTime().toString();
        }
        this.invoiceType =custPlanMapppingList.getInvoiceType();
        this.traildebitdocid =custPlanMapppingList.getTraildebitdocid();
        this.promisetopay_renew_count =custPlanMapppingList.getPromisetopay_renew_count();
        this.isTrialValidityDays =custPlanMapppingList.getIsTrialValidityDays();
        this.trialPlanValidityCount =custPlanMapppingList.getTrialPlanValidityCount();
        if(custPlanMapppingList.getStartServiceDate()!=null) {
            this.startServiceDate = custPlanMapppingList.getStartServiceDate().toString();
        }
        if (custPlanMapppingList.getServiceHoldDate()!=null) {
            this.serviceHoldDate = custPlanMapppingList.getServiceHoldDate().toString();
        }
        if (custPlanMapppingList.getPromise_to_pay_startdate()!=null) {
            this.promise_to_pay_startdate = custPlanMapppingList.getPromise_to_pay_startdate().toString();
        }
        if (custPlanMapppingList.getPromise_to_pay_enddate()!=null) {
            this.promise_to_pay_enddate = custPlanMapppingList.getPromise_to_pay_enddate().toString();
        }
        this.totalHoldDays =custPlanMapppingList.getTotalHoldDays();
        this.discountType =custPlanMapppingList.getDiscountType();
        if(custPlanMapppingList.getDiscountExpiryDate()!=null) {
            this.discountExpiryDate = custPlanMapppingList.getDiscountExpiryDate().toString();
        }
        if (custPlanMapppingList.getDownTimeExpiryDate()!=null) {
            this.downTimeExpiryDate = custPlanMapppingList.getDownTimeExpiryDate().toString();
        }
        if (custPlanMapppingList.getDownTimeStartDate()!=null) {
            this.downTimeStartDate = custPlanMapppingList.getDownTimeStartDate().toString();
        }
        this.cprIdForPromiseToPay =custPlanMapppingList.getCprIdForPromiseToPay();
        this.isHold =custPlanMapppingList.getIsHold();
        this.isVoid =custPlanMapppingList.getIsVoid();
        this.serviceHoldBy =custPlanMapppingList.getServiceHoldBy();
        this.serviceStartBy =custPlanMapppingList.getServiceStartBy();
        this.isContainsCustomerInvoice =custPlanMapppingList.getIsContainsCustomerInvoice();
        this.customerCpr =custPlanMapppingList.getCustomerCpr();
        this.creditdocid =custPlanMapppingList.getCreditdocid();
        this.serviceId =custPlanMapppingList.getServiceId();
        this.serialNumber =custPlanMapppingList.getSerialNumber();
        this.renewalForBooster = custPlanMapppingList.getRenewalForBooster();
        this.vasId = custPlanMapppingList.getVasId();
    }

    public CustPlanMappingRevenue(Integer custPlanMappingId,LocalDateTime endDate,LocalDateTime expirydate ,String custPlanStatus, Integer vasId , String status){

        this.id = custPlanMappingId;
        this.endDate = endDate.toString();
        this.expiryDate = expirydate.toString();
        this.custPlanStatus = custPlanStatus;
        this.vasId = vasId;
        this.status = status;
    }

}
