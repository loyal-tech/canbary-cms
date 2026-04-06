package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Customers;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblpartnercreditdocument")
public class PartnerCreditDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_credit_id", nullable = false, length = 40)
    private Integer id;

    @Column(name = "PAYMODE", nullable = false, length = 40)
    private String paymode;

    @Column(name = "paymentdate", nullable = false, length = 40)
    private LocalDate paymentdate;

    @Column(name = "PAYDETAILS4", nullable = false, length = 40)
    private String paydetails4;

    @Column(name = "amount", nullable = false, length = 40)
    private Double amount = 0.0;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "remarks", length = 40)
    private String remarks;

    @Column(name = "referenceno", nullable = false, length = 40)
    private String referenceno;

    @Column(name = "xmldocument", nullable = false, length = 40)
    private String xmldocument;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;

    @Column(name = "lcoid", length = 40, updatable = false)
    private Integer lcoid;

    @Column(name = "invoiceid", nullable = false, length = 40)
    private Integer invoiceId;

    @Column(name = "paytype")
    private String paytype;

    @Column(name = "type", length = 25)
    private String type;

    @Column(name = "receipt_number")
    private String reciptNo;

    @Column(name = "adjustedamount", nullable = false)
    private Double adjustedAmount;

    @Column(name = "CREATEDATE", nullable = false)
    private LocalDateTime createDate;

    @Transient
    private String invoiceNumber;
}
