package com.adopt.apigw.model.postpaid;


import com.adopt.apigw.spring.security.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbltcreditdebitmapping")
public class  CreditDebitDocMapping {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "creddebtmappingid", nullable = false, length = 40)
    private Integer id;


    @Column(name="CREDITDOCID", length = 40)
    private Integer creditDocId;

    @Column(name="debitdocumentid", length = 40)
    private Integer debtDocId;

    @Transient
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;


    @Column(name = "adjustedamount", length = 40)
    private Double adjustedAmount;

    @Column(name = "amount", length = 40)
    private Double amount;

    @Column(name = "abbs_amount", length = 40)
    private Double abbsAmount;

    @Column(name = "tds_amount", length = 40)
    private Double tdsAmount;



    @Column(name="withdrawal_id", length = 40)
    private Integer withdrawId;




}
