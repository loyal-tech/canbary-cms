package com.adopt.apigw.modules.PartnerLedger.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.postpaid.Partner;
import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tblmpartnerpayment")
@NoArgsConstructor
public class PartnerPayment implements IBaseData<Long> {

    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partnerpaymentid")
    private Long id;

    private String transcategory;

    @Column(name = "payment_mode")
    private String paymentmode;

    private String refno;
    @DiffIgnore
    private String orderid;
    @DiffIgnore
    private String paymentstatus;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "partner_id")
    @ToString.Exclude
    private Partner partner;

    @CreationTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    @DiffIgnore
    protected LocalDateTime createdate;

    private Double amount = 0.0;
    private Double credit=0.0;
    private String chequenumber;

    private LocalDate chequedate;
    private String remarks;

    private LocalDate paymentdate;
    private String bank_name;
    private String branch_name;

    @Transient
    @DiffIgnore
    private  String chequedateString;
    @Transient
    @DiffIgnore
    private  String paymentdateString;
    @DiffIgnore
    @Column(name = "next_team_hir_mapping_id")
    private Integer nextTeamHierarchyMappingId;

    @DiffIgnore
    @Column(name = "next_staff")
    private Integer nextStaff;

    @Column(name = "status", length = 40)
    private String status;

    @DiffIgnore
    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @Transient
    private String partnerName;

    @Column(name = "online_source")
    private String onlinesource;

    @Column(name = "source_bank")
    private Long sourceBank;

    @Column(name = "destination_bank")
    private Long destinationBank;


    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }

    public PartnerPayment(PartnerPayment partnerPayment) {
        this.id = partnerPayment.getId();
        this.transcategory = partnerPayment.getTranscategory();
        this.paymentmode = partnerPayment.getPaymentmode();
        this.refno = partnerPayment.getRefno();
        this.orderid = partnerPayment.getOrderid();
        this.paymentstatus = partnerPayment.getPaymentstatus();

        this.partner = new Partner(partnerPayment.getPartner());

        this.amount = partnerPayment.getAmount();
        this.credit = partnerPayment.getCredit();
        this.chequenumber = partnerPayment.getChequenumber();
        this.remarks = partnerPayment.getRemarks();
        this.bank_name = partnerPayment.getBank_name();
        this.branch_name = partnerPayment.getBranch_name();
        if(partnerPayment.getChequedate()!=null) {
            this.chequedateString = partnerPayment.getChequedate().toString();
        }
        if(partnerPayment.getPaymentdate()!=null) {
            this.paymentdateString = partnerPayment.getPaymentdate().toString();
        }
        this.nextTeamHierarchyMappingId = partnerPayment.getNextTeamHierarchyMappingId();
        this.nextStaff = partnerPayment.getNextStaff();
        this.status = partnerPayment.getStatus();
        this.isDeleted = partnerPayment.isDeleted;
        this.partnerName = partnerPayment.getPartnerName();
        this.onlinesource = partnerPayment.getOnlinesource();
        this.sourceBank = partnerPayment.getSourceBank();
        this.destinationBank = partnerPayment.getDestinationBank();
    }
}
