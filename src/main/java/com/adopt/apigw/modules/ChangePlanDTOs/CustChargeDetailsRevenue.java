package com.adopt.apigw.modules.ChangePlanDTOs;

import com.adopt.apigw.model.postpaid.CustChargeDetails;
import com.adopt.apigw.model.postpaid.CustChargeInstallment;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CustChargeDetailsRevenue {

    private Integer id;

    private Double validity;

    private Integer planid;

    private Integer chargeid;

    private String chargetype;

    private Double price;

    private Double actualprice;

    private String charge_date;

    private String startdate;

    private String enddate;

    private Double taxamount;

    private Boolean is_reversed;

    private String rev_date;

    private Double rev_amt;


    private Integer customerId;

    private Boolean isUsed;

    private Long purchaseEntityId;

    private Long debitdocid;


    private Integer billableCustomerId;

    private String type;

    private Integer planValidity;

    private String unitsOfValidity;

    private Integer taxId;

    private Integer custPlanMapppingId;

    private String lastBillDate;

    private String nextBillDate;

    private Integer billingCycle;

    private Boolean isDeleted = false;

    private Double dbr = 0.0;

    private Double discount;

    private Boolean isInvoiceToOrg;

    private String billTo;

    private Double newAmount;

    private String staticIPAdrress ;

    private String connection_no;

    private Boolean isRenew;

    private List<CustChargeInstallment> custChargeInstallmentList;

    public CustChargeDetailsRevenue(CustChargeDetails custChargeDetails) {
        this.id =custChargeDetails.getId();
        this.validity =custChargeDetails.getValidity();
        this.planid =custChargeDetails.getPlanid();
        this.chargeid =custChargeDetails.getChargeid();
        this.chargetype =custChargeDetails.getChargetype();
        this.price =custChargeDetails.getPrice();
        this.actualprice =custChargeDetails.getActualprice();
        if (custChargeDetails.getCharge_date()!=null) {
            this.charge_date = custChargeDetails.getCharge_date().toString();
        }
        if (custChargeDetails.getStartdate()!=null) {
            this.startdate = custChargeDetails.getStartdate().toString();
        }
        if (custChargeDetails.getEnddate()!=null) {
            this.enddate = custChargeDetails.getEnddate().toString();
        }
        this.taxamount =custChargeDetails.getTaxamount();
        this.is_reversed =custChargeDetails.getIs_reversed();
        if (custChargeDetails.getRev_date()!=null) {
            this.rev_date = custChargeDetails.getRev_date().toString();
        }
        this.rev_amt =custChargeDetails.getRev_amt();
        if (custChargeDetails.getCustomer()!=null) {
            this.customerId = custChargeDetails.getCustomer().getId();
        }
        this.isUsed =custChargeDetails.getIsUsed();
        this.purchaseEntityId =custChargeDetails.getPurchaseEntityId();
        this.debitdocid =custChargeDetails.getDebitdocid();
        this.billableCustomerId =custChargeDetails.getBillableCustomerId();
        this.type =custChargeDetails.getType();
        this.planValidity =custChargeDetails.getPlanValidity();
        this.unitsOfValidity =custChargeDetails.getUnitsOfValidity();
        this.taxId =custChargeDetails.getTaxId();
        this.custPlanMapppingId =custChargeDetails.getCustPlanMapppingId();
        if (custChargeDetails.getLastBillDate()!=null) {
            this.lastBillDate = custChargeDetails.getLastBillDate().toString();
        }
        if (custChargeDetails.getNextBillDate()!=null) {
            this.nextBillDate = custChargeDetails.getNextBillDate().toString();
        }
        this.billingCycle =custChargeDetails.getBillingCycle();
        this.isDeleted =custChargeDetails.getIsDeleted();
        this.dbr =custChargeDetails.getDbr();
        this.discount =custChargeDetails.getDiscount();
        this.isInvoiceToOrg =custChargeDetails.getIsInvoiceToOrg();
        this.billTo =custChargeDetails.getBillTo();
        this.newAmount =custChargeDetails.getNewAmount();
        this.staticIPAdrress =custChargeDetails.getStaticIPAdrress();
        this.connection_no =custChargeDetails.getConnection_no();
    }


}
