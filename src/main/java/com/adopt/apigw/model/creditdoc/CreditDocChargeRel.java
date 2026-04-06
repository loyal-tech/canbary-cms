package com.adopt.apigw.model.creditdoc;

import com.adopt.apigw.model.common.CustomerPayment;
import com.adopt.apigw.model.postpaid.Charge;
import com.adopt.apigw.model.postpaid.CreditDocument;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "tbltcreditdocchargerel")
public class CreditDocChargeRel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "creditdocchargeid", nullable = false, length = 40)
    private Long id;

    @JoinColumn(name = "CHARGEID")
    @OneToOne(cascade = CascadeType.ALL)
    private Charge charge;

    @JoinColumn(name = "CREDITDOCID")
    @OneToOne(cascade = CascadeType.ALL)
    private CreditDocument creditDocument;

    @Column(name = "debit_doc_id", nullable = false, length = 40)
    private Integer debitDocId;

    @Column(name = "charge_amount", nullable = false, length = 40)
    private double chargeAmount;

    @Column(name = "discount", nullable = false, length = 40)
    private double discount;

    @Column(name = "tax_amount", nullable = false, length = 40)
    private double taxAmount;

    @Column(name = "total_amount", nullable = false, length = 40)
    private double totalAmount;

    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "creditDocChargeRel")
    private List<CreditDocTaxRel> creditDocTaxRel;
}
