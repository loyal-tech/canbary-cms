package com.adopt.apigw.pojo.api;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import com.adopt.apigw.model.postpaid.TaxTypeSlab;

public class TaxTypeSlabPojo {

	private Integer id;

	@NotNull
    private String name;

	@NotNull
    private Double rangeFrom;

	@NotNull
    private Double rangeUpTo;

	@NotNull
    private Double rate;

	private Boolean beforeDiscount = false;

	public TaxTypeSlabPojo() {}
	public TaxTypeSlabPojo(TaxTypeSlab tax) {
		this.id=tax.getId();
		this.name=tax.getName();
		this.rangeFrom=tax.getRangeFrom();
		this.rangeUpTo=tax.getRangeUpTo();
		this.rate=tax.getRate();
		this.beforeDiscount=tax.getBeforeDiscount();
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

	public Double getRangeFrom() {
		return rangeFrom;
	}

	public void setRangeFrom(Double rangeFrom) {
		this.rangeFrom = rangeFrom;
	}

	public Double getRangeUpTo() {
		return rangeUpTo;
	}

	public void setRangeUpTo(Double rangeUpTo) {
		this.rangeUpTo = rangeUpTo;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	@Override
	public String toString() {
		return "TaxTypeSlabPojo [id=" + id + ", name=" + name + ", rangeFrom=" + rangeFrom + ", rangeUpTo=" + rangeUpTo
				+ ", rate=" + rate + "]";
	}

}
