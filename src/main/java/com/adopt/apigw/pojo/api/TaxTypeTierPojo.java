package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.postpaid.TaxTypeTier;

import javax.validation.constraints.NotNull;

public class TaxTypeTierPojo {

    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String taxGroup;

    @NotNull
    private Double rate;

    private Boolean isDelete = false;


    private Boolean beforeDiscount = false;

    private String ledgerId;

    public Boolean getBeforeDiscount() {
        return beforeDiscount;
    }

    public void setBeforeDiscount(Boolean beforeDiscount) {
        this.beforeDiscount = beforeDiscount;
    }

    public TaxTypeTierPojo() {
    }

    public TaxTypeTierPojo(TaxTypeTier tier) {
        this.id = tier.getId();
        this.name = tier.getName();
        this.rate = tier.getRate();
        this.taxGroup = tier.getTaxGroup();
        this.beforeDiscount = tier.getBeforeDiscount();
        this.ledgerId = tier.getTaxLedgerId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaxGroup() {
        return taxGroup;
    }

    public void setTaxGroup(String taxGroup) {
        this.taxGroup = taxGroup;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean delete) {
        isDelete = delete;
    }

    public String getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(String ledgerId) {
        this.ledgerId = ledgerId;
    }

    @Override
    public String toString() {
        return "TaxTypeTierPojo [id=" + id + ", name=" + name + ", taxGroup=" + taxGroup + ", rate=" + rate + ", ledgerId=" + ledgerId + "]";
    }


}
