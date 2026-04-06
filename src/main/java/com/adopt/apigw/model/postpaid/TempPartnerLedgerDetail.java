package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.core.data.IBaseData;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="tbltmppartnerledgerdetails")
public class TempPartnerLedgerDetail implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="partnerledgerdtls_id")
    private Long id;

    private String transtype;
    private String transcategory;
    private Double amount;
    @Column(name = "CREATEDATE")
    private LocalDateTime CREATE_DATE;
    private String description;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "partner_id")
    @ToString.Exclude
    private Partner partner;


    private Integer custid;
    private Double offerprice;
    private Double agr_amount;
    private Double tds_amount;
    private Double tax;
    private Double commission;
    private String invoice_id;
    private Double royalty;

    private String planid;

    @Transient
    private String customer_name;

    @Column(name="is_deleted",columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @Transient
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime END_DATE;

    @Transient
    private Double balAmount;

    @Column(name="debit_doc_id",columnDefinition = "Boolean default false")
    private Long debitDocId;

    @Column(name = "partner_tax")
    Double partnerTax = 0d;

    @Column(name = "gross_offer_price")
    Double grossOfferPrice = 0d;

    @Column(name = "is_payment_done_fran")
    Integer paymentStatus;

    @Column(name = "staff_id")
    Integer staffUserId;

    @Column(name = "royalty_base_price")
    Double royaltyBasePrice = 0d;

    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }
    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted=deleteFlag;
    }
    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }
}
