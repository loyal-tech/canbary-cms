package com.adopt.apigw.modules.staffLedgerDetails.entity;

import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tblmstaffledgerdetails")
@EntityListeners(AuditableListener.class)
public class StaffLedgerDetails extends Auditable implements IBaseData2<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @ManyToOne(targetEntity = StaffUser.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "staff_id", referencedColumnName = "staffid")
    StaffUser staff;

    @Column(name = "cust_id")
    private Long custId;

    @Column(name = "credit_doc_id")
    private Long creditDocId;

    @Column(name = "payment_mode")
    private String paymentMode;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "amount", nullable = false, length = 40)
    private Double amount;

    @Column(name = "action")
    private String action;

    @Column(name = "bank_id")
    private Long bankId;

    @Column(name = "bankname")
    private String bankName;
    @Column(name = "remarks")
    private String remarks;

    @Column(name = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;


    @Column(name ="transferredamount")
    private Double transferredamount;

    @Column(name = "status")
    private String status;

    @Column(name = "chequedate")
    private LocalDate chequedate;

    @Column(name = "chequeno")
    private String chequeno; //ChequeNo

    @Transient
    private String currency;

    @Transient
    private String custName;

    @Transient
    private String debitDocumentNumber;

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    @Override
    public Long getPrimaryKey() {
        return null;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }

    @Override
    public void setBuId(Long buId) {

    }
}
