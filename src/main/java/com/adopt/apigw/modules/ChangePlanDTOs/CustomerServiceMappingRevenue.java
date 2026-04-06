package com.adopt.apigw.modules.ChangePlanDTOs;

import com.adopt.apigw.model.common.CustomerServiceMapping;
import lombok.Data;

@Data
public class CustomerServiceMappingRevenue {



    private Integer id;

    private Integer custId;

    private Long serviceId;


    private String connectionNo;

    private Boolean isDeleted = false;

    private String invoiceformat;

    private String invoiceType;

    private Long cafNo;

    private String uploadCAF;

    private String customerName;

    private Long accountNumber;


    private Long partner;

    private String expiryDate;

    private String terminationAddress;

    private String chargeTypeFile;

    private String billingCycle;
    private String billingType;

    private String billable;

    private String billingGroup;

    private String payable;


    private String fullName;

    private String organisation;

    private Boolean isDelete = false;

    private Integer mvnoId;

    private Long buId;

    private Double discount;

    private String discountType="One-time";

    private String discountExpiryDate;

    private String newDiscountType;

    private Double newDiscount;

    private String newDiscountExpiryDate;

    private String remarks;

    private Integer nextTeamHierarchyMappingId;

    private Integer nextStaff;

    private String connectionType;

    private String serviceName;

    private String serviceHoldDate;

    private String serviceHoldBy;

    private String serviceResumeBy;

    private String serviceResumeDate;

    private String stopServiceRemark;

    private String discountFlowInProcess;

    private Double old_discount;

    public CustomerServiceMappingRevenue(CustomerServiceMapping customerServiceMapping){
        this.id =customerServiceMapping.getId();
        this.custId =customerServiceMapping.getCustId();
        this.serviceId =customerServiceMapping.getServiceId();
        this.connectionNo =customerServiceMapping.getConnectionNo();
        this.isDeleted =customerServiceMapping.getIsDeleted();
        this.invoiceformat =customerServiceMapping.getInvoiceformat();
        this.invoiceType =customerServiceMapping.getInvoiceType();
        this.cafNo =customerServiceMapping.getCafNo();
        this.uploadCAF =customerServiceMapping.getUploadCAF();
        this.customerName =customerServiceMapping.getCustomerName();
        this.accountNumber =customerServiceMapping.getAccountNumber();
        this.partner =customerServiceMapping.getPartner();
        if(customerServiceMapping.getExpiryDate()!=null){
            this.expiryDate =customerServiceMapping.getExpiryDate().toString();
        }
        this.chargeTypeFile =customerServiceMapping.getChargeTypeFile();
        this.billingCycle =customerServiceMapping.getBillingCycle();
        this.billingType =customerServiceMapping.getBillingType();
        this.billable =customerServiceMapping.getBillable();
        this.billingGroup =customerServiceMapping.getBillingGroup();
        this.payable =customerServiceMapping.getPayable();
        this.organisation =customerServiceMapping.getOrganisation();
        this.isDelete =customerServiceMapping.getIsDelete();
        this.mvnoId =customerServiceMapping.getMvnoId();
        this.buId =customerServiceMapping.getBuId();
        if (customerServiceMapping.getDiscount() != null){
            this.discount =customerServiceMapping.getDiscount();
        }
        this.discountType =customerServiceMapping.getDiscountType();
        if (customerServiceMapping.getDiscountExpiryDate()!=null) {
            this.discountExpiryDate = customerServiceMapping.getDiscountExpiryDate().toString();
        }
        this.newDiscountType =customerServiceMapping.getNewDiscountType();
        this.newDiscount =customerServiceMapping.getNewDiscount();
        if(customerServiceMapping.getNewDiscountExpiryDate()!=null) {
            this.newDiscountExpiryDate = customerServiceMapping.getNewDiscountExpiryDate().toString();
        }
        this.remarks =customerServiceMapping.getRemarks();
        this.nextTeamHierarchyMappingId =customerServiceMapping.getNextTeamHierarchyMappingId();
        this.nextStaff =customerServiceMapping.getNextStaff();
        this.connectionType =customerServiceMapping.getConnectionType();
        this.serviceName =customerServiceMapping.getServiceName();
        if (customerServiceMapping.getServiceHoldDate()!=null) {
            this.serviceHoldDate = customerServiceMapping.getServiceHoldDate().toString();
        }
        if (customerServiceMapping.getServiceResumeDate()!=null) {
            this.serviceResumeDate = customerServiceMapping.getServiceResumeDate().toString();
        }
        this.serviceResumeBy =customerServiceMapping.getServiceResumeBy();
        this.stopServiceRemark =customerServiceMapping.getStopServiceRemark();
        this.discountFlowInProcess =customerServiceMapping.getDiscountFlowInProcess();
        this.old_discount =customerServiceMapping.getOld_discount();
    }

}
