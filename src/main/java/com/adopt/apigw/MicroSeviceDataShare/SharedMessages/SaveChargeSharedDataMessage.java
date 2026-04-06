package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.model.postpaid.Tax;
import com.adopt.apigw.modules.servicePlan.domain.Services;
import lombok.Data;


import java.util.ArrayList;
import java.util.List;

@Data
public class SaveChargeSharedDataMessage {

    private Integer id;
    private String name;
    private String desc;
    private String chargetype;
    private double price;
    private double actualprice;
    private Integer taxId;
   // private Tax tax;
    private Integer discountid;
    private double dbr;
    private Boolean isDelete;
    private String saccode;
    private List<Services> serviceList;
    private Integer mvnoId;
    private Long buId;
    private String service;
    private String status;
    private String ledgerId;
    private Boolean royalty_payable;
    private Double taxamount;
    private String businessType;
    private String pushableLedgerId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String chargecategory;
    private Boolean isinventorycharge;
    private Long productId;
    private String inventoryChargeType;
}
