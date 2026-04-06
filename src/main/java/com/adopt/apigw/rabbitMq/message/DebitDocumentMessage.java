package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.postpaid.CreditDocument;
import com.adopt.apigw.model.postpaid.DebitDocDetails;
import com.adopt.apigw.model.postpaid.DebitDocument;
import com.adopt.apigw.model.postpaid.DebitDocumentTAXRel;
import com.adopt.apigw.modules.PurchaseOrder.Domain.PurchaseOrder;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebitDocumentMessage {
    private Integer id;
    private String docnumber;

//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    @JsonBackReference
//    private CustomersPojo customer;

    private Integer planId;
    private String billdate;
    private String startdate;
    private String endate;
    private String duedate;
    private String latepaymentdate;
    private Double subtotal;
    private Double tax;
    private Double discount;
    private Double totalamount;
    private Double previousbalance;
    private Double latepaymentfee;
    private Double currentpayment;
    private Double currentdebit;
    private Double currentcredit;
    private Double totaldue;
    private String amountinwords;
    private String dueinwords;
    private Integer billrunid;
    private String billrunstatus;
    private String document;
    private Boolean isDelete = false;
    private Long cstchargeid;
    private Integer custid;
    private String customerName;
    private String custType;
    private String paymentStatus;
    private Double adjustedAmount;
    private List<CreditDocument> creditDocumentList;
    private String custRefName;
    private  String refundAbleAmount;
    private List<DebitDocumentTAXRel> debitDocumentTAXRels;
    private Integer nextStaff;
    private Integer nextTeamHierarchyMappingId;
    private String status;
    private List<DebitDocDetails> debitDocDetails;
    private Boolean  isDirectChargeInvoice;
    private Integer lcoId;
    private String paymentowner;
    private PurchaseOrder purchaseorder;
    private List<DebitDocDetails> debitDocDetailsList;
    private Integer  customerId;

    private Long inventoryMappingId;


    public DebitDocumentMessage(DebitDocument obj){
        this.id=obj.getId();
        this.document=obj.getDocument();
        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        this.billdate= obj.getBilldate().toString();
        this.startdate=obj.getStartdate().toString();
        this.endate=obj.getEndate().toString();
        this.duedate= obj.getDuedate().toString();
        this.latepaymentdate= obj.getLatepaymentdate().toString();
        this.subtotal=obj.getSubtotal();
        this.tax=obj.getTax();
        this.discount=obj.getDiscount();
        this.totalamount=obj.getTotalamount();
        this.previousbalance=obj.getPreviousbalance();
        this.latepaymentfee=obj.getLatepaymentfee();
        this.currentpayment=obj.getCurrentpayment();
        this.currentdebit=obj.getCurrentdebit();
        this.currentcredit=obj.getCurrentcredit();
        this.totaldue=obj.getTotaldue();
        this.amountinwords=obj.getAmountinwords();
        this.dueinwords=obj.getDueinwords();
        this.billrunid=obj.getBillrunid();
        this.billrunstatus=obj.getBillrunstatus();
        this.status=obj.getStatus();
        this.isDelete=obj.getIsDelete();
        this.cstchargeid=obj.getCstchargeid();
        this.paymentowner=obj.getPaymentowner();
        this.debitDocumentTAXRels=obj.getDebitDocumentTAXRels();
        this.debitDocDetailsList=obj.getDebitDocDetailsList();
        this.docnumber=obj.getDocnumber();
        this.customerId=obj.getCustomer().getId();
        this.docnumber=obj.getDocnumber();
        this.custRefName=obj.getCustRefName();
        this.inventoryMappingId=obj.getInventoryMappingId();
    }
}

