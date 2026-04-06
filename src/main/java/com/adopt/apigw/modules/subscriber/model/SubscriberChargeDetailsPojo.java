package com.adopt.apigw.modules.subscriber.model;


import com.adopt.apigw.model.postpaid.Tax;

import lombok.Data;

@Data
public class SubscriberChargeDetailsPojo {
    private Integer id;
    private String name;
    private String desc;
    private String chargetype;
    private double price;
    private double actualprice;
    private Tax tax;
    private Integer discountid;
    private double dbr;
    private Boolean isDelete;
    private String chargecategory;
}
