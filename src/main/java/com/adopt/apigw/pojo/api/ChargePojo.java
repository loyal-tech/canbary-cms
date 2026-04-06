package com.adopt.apigw.pojo.api;

import com.adopt.apigw.modules.servicePlan.domain.Services;
import lombok.Data;

import javax.validation.constraints.NotNull;

import com.adopt.apigw.model.common.Auditable;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ChargePojo extends Auditable {


    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String desc;

    @NotNull
    private String chargetype;

    @NotNull
    private double price;

    private Integer taxid;

    private String taxName;

    private Integer discountid;

    private double dbr;

    private double actualprice;

    private Boolean isDelete = false;
    
    private String chargecategory;

    private String saccode;

    private Double taxamount;
    
    private Integer mvnoId;

    private Long buId;
//    private String service;
    private String status;

    private String ledgerId;

    private Boolean royalty_payable;

    //private Boolean ismapping;

    private List<Long> serviceid;

    private List<Integer> servicesid=new ArrayList<>();

    private List<String> serviceNameList = new ArrayList<>();
    private Services services;

    private Integer displayId;
    private String displayName;

    private String businessType;

    private String pushableLedgerId;
    private Boolean isinventorycharge = false;
    private Long productId;
    private String inventoryChargeType;
    private String mvnoName;
    private String currency;
    @Override
    public String toString() {
        return "ChargePojo [id=" + id +", ledgerId=" + ledgerId  +", name=" + name + ", desc=" + desc + ", chargetype=" + chargetype + ", price="
                + price + ", taxid=" + taxid + ", discountid=" + discountid +", serviceid="+services+"]";
    }

    public ChargePojo(Integer id, String name) {
        this.id = id;
        this.name = name;
    }


    public ChargePojo(
            Integer id, String name, String desc, String chargetype, double price,
            Integer taxid, String taxName, Double taxamount,
            Integer discountid, double dbr, double actualprice, Boolean isDelete,
            String chargecategory, String saccode, Integer mvnoId, Long buId,
            String status, String ledgerId, Boolean royalty_payable, String businessType, String pushableLedgerId,
            Boolean isinventorycharge, Long productId, String inventoryChargeType,
            String mvnoName, String currency
    ) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.chargetype = chargetype;
        this.price = price;
        this.taxid = taxid;
        this.taxName = taxName;
        this.taxamount = taxamount;
        this.discountid = discountid;
        this.dbr = dbr;
        this.actualprice = actualprice;
        this.isDelete = isDelete;
        this.chargecategory = chargecategory;
        this.saccode = saccode;
        this.mvnoId = mvnoId;
        this.buId = buId;
        this.status = status;
        this.ledgerId = ledgerId;
        this.royalty_payable = royalty_payable;
        this.displayId = id;
        this.displayName = name;
        this.businessType = businessType;
        this.pushableLedgerId = pushableLedgerId;
        this.isinventorycharge = isinventorycharge;
        this.productId = productId;
        this.inventoryChargeType = inventoryChargeType;
        this.mvnoName = mvnoName;
        this.currency = currency;
    }

}
