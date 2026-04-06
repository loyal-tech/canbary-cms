package com.adopt.apigw.model.common;


import com.adopt.apigw.model.postpaid.CreditDocument;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;


@Entity
@Data
@ToString
@Table(name = "tbltbatchpaymentmapping")
public class BatchPaymentMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Long id;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "credit_doc_id")
    private CreditDocument creditDocument;

    @ToString.Exclude
    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "batch_id")
    private BatchPayment batchPayment;

    @Column(name = "is_deleted")
    private Boolean is_deleted=false;
}
