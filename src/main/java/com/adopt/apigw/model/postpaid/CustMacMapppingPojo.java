package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
public class CustMacMapppingPojo extends Auditable {

    private Integer custid;

    private List<CustMacMappping> custMacMapppingList;

    private Integer id;

    private String macAddress;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private CustomersPojo customer;
    private Boolean isDeleted = false;
}
