package com.adopt.apigw.model.creditdoc;

import com.adopt.apigw.model.postpaid.Charge;
import com.adopt.apigw.model.postpaid.CreditDocument;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltcreditdoctaxrel")
@AllArgsConstructor
@NoArgsConstructor
public class CreditDocTaxRel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "creditdoctaxid", nullable = false, length = 40)
    private Long id;

    @JoinColumn(name = "CHARGEID")
    @OneToOne(cascade = CascadeType.ALL)
    private Charge charge;

    @JoinColumn(name = "CREDITDOCID")
    @OneToOne(cascade = CascadeType.ALL)
    private CreditDocument creditDocument;

    @Column(name = "tax_amount", nullable = false, length = 40)
    private Double taxAmount;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creditdocchargeid")
    private CreditDocChargeRel creditDocChargeRel;

    public CreditDocTaxRel(Charge charge, CreditDocument creditDocument, Double taxAmount, CreditDocChargeRel creditDocChargeRel) {
        this.charge = charge;
        this.creditDocument = creditDocument;
        this.taxAmount = taxAmount;
        this.creditDocChargeRel = creditDocChargeRel;
    }
}
