package com.adopt.apigw.pojo;

import java.util.List;

import com.adopt.apigw.model.postpaid.CustChargeDetails;

public class CustChargeOverride {

	Integer custid;
	List<CustChargeDetails> chargeList;
		
	public Integer getCustid() {
		return custid;
	}
	public void setCustid(Integer custid) {
		this.custid = custid;
	}
	public List<CustChargeDetails> getChargeList() {
		return chargeList;
	}
	public void setChargeList(List<CustChargeDetails> chargeList) {
		this.chargeList = chargeList;
	}
		
}
