package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.postpaid.Charge;
import com.adopt.apigw.modules.servicePlan.domain.Services;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargeMessage {
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

    private List<Integer> servicesid = new ArrayList<>();

    private List<String> serviceNameList = new ArrayList<>();
    private String services;

    private String pushableLedgerId;


    public ChargeMessage(Charge obj) {
        this.id = obj.getId();
        this.name = obj.getName();
        this.desc = obj.getDesc();
        this.chargetype = obj.getDesc();
        this.price = obj.getPrice();
        this.taxid = obj.getTax().getId();
        this.taxName = obj.getTax().getName();
        this.discountid = obj.getDiscountid();
        this.dbr = obj.getDbr();
        this.actualprice = obj.getActualprice();
        this.isDelete = obj.getIsDelete();
        this.chargecategory = obj.getChargecategory();
        this.saccode = obj.getSaccode();
        this.taxamount = obj.getTaxamount();
        this.mvnoId = obj.getMvnoId();
        this.buId = obj.getBuId();
        this.status = obj.getStatus();
        this.ledgerId = obj.getLedgerId();
        this.royalty_payable = obj.getRoyalty_payable();
        this.services = obj.getService();
        this.pushableLedgerId = obj.getPushableLedgerId();
    }
}
