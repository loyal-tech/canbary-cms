package com.adopt.apigw.model.postpaid;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmplancasmapping")
public class PlanCasMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plancasmappingid")
    private Long id;
    @Column(name = "planid")
    private Long planId;
    @Column(name = "casid")
    private Long casId;
    @Column(name = "packageid")
    private Long packageId;
}
