package com.adopt.apigw.pojo.api;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class CustLedgerDtlsPojo {

    private Integer id;

    private String transtype;

    private String transcategory;

    private double amount;

    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CustomersPojo customer;

    private Integer creditdocid;

    private Integer debitdocid;

    private String description;
}
