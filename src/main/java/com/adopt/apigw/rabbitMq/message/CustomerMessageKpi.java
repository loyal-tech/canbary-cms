package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.PlanGroup;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import lombok.AllArgsConstructor;
import lombok.Data;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class CustomerMessageKpi {
    private Integer customerId;
    private String userName;
    private String firstName;

    private String lastName;

    private String email;

    private String customerStatus;

    private String accountNumber;

    private String accountType;

    private String customerType;

    private String gender;

    private String phone;

    private String subscriberPackage;

    private String subscriberPackageId;

    private String createDate;

    private String expiryDate;

    private String lastStatusChangeDate;

    private String nextBillDate;

    private String lastBillDate;

    private Integer billDay;

    private Integer partnerId;

    private Integer macAddress;

    private Integer parentCustomerId;

    private String customer;

    private String contactPerson;

    private String mobile;

    private String customerCategory;

    private Double walletBalance;

    private Long oltSlotId;

    private Long oltPortId;

    private Integer createdByStaffId;

    private Integer lastModifiedByStaffId;

/*    private Long serviceAreaId;*/
    @ManyToOne
    @JoinColumn(name = "serviceareaid")
    private ServiceArea servicearea;

    private String createByName;

    private String updateByName;

    private String lastModifiedDate;

    private String cafApproveStatus;

    private Integer mvnoId;

    private PlanGroup planGroupId;

    private String invoiceType;

    private String calendarType;

    private Long buId;

    private String planPurchaseType;

    private Long branchId;

    private Integer lcoId;

    private Long popid;

    private Long leadId;

    private String businessType;

    private Integer billableCustomerId;

    public CustomerMessageKpi(){}
    public CustomerMessageKpi(Customers customer) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

        this.customerId = customer.getId();
        this.userName = customer.getUsername();
        this.firstName = customer.getFirstname();
        this.lastName = customer.getLastname();
        this.email = customer.getEmail();
        this.customerStatus = customer.getStatus();
        this.accountNumber = customer.getAcctno();
        this.customerType = customer.getCustomerType();
        // this.gender = customer.get();
        this.phone = customer.getPhone();
        /*this.subscriberPackage =  ;
        this.subscriberPackageId = ;*/
        this.createDate = customer.getCreatedate().format(formatter);
        this.expiryDate = customer.getCreatedate().format(formatter);
        if (customer.getLastStatusChangeDate() != null) {
            this.lastStatusChangeDate = customer.getLastStatusChangeDate().format(formatter);
        }
        this.nextBillDate = customer.getNextBillDate().format(formatter);
        if(customer.getLastBillDate() != null) {
            this.lastBillDate = customer.getLastBillDate().format(formatter);
        }
        this.billDay = customer.getBillday();
        this.partnerId = customer.getPartner().getId();
        if(customer.getMACADDRESS() != null) {
            this.macAddress = Integer.valueOf(customer.getMACADDRESS());
        }
        this.parentCustomerId = customer.getParentCustomersId();
        this.contactPerson = customer.getContactperson();
        this.mobile = customer.getMobile();
        this.customerCategory = customer.getCustcategory();
        this.walletBalance = customer.getWalletbalance();
        this.oltSlotId  = customer.getOltslotid();
        this.oltPortId = customer.getOltslotid();
        this.createdByStaffId = customer.getCreatedById();
        this.lastModifiedByStaffId = customer.getLastModifiedById();
        this.servicearea = customer.getServicearea();
        this.createByName = customer.getCreatedByName();
        this.updateByName = customer.getCreatedByName();
        this.lastModifiedDate = customer.getUpdatedate().format(formatter);
        this.cafApproveStatus = customer.getCafApproveStatus();
        this.mvnoId = customer.getMvnoId();
        this.planGroupId = customer.getPlangroup();
        this.invoiceType = customer.getInvoiceType();
        this.calendarType = customer.getCalendarType();
        this.buId = customer.getBuId();
        this.planPurchaseType = customer.getPlanPurchaseType();
        this.branchId = customer.getBranch();
        this.lcoId = customer.getLcoId();
        this.popid = customer.getPopid();
        this.leadId = customer.getLeadId();
        this.businessType = customer.getBusinessType();
        this.billableCustomerId = customer.getBillableCustomerId();

    }
}

