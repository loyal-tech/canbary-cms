package com.adopt.apigw.modules.customerDocDetails.model;

import lombok.Data;

import java.util.List;

@Data
public class CustomerDocDeleteModel {

    private List<Long> docIdList;
    private Integer custId;
}
