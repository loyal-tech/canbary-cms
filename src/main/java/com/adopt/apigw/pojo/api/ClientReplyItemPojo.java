package com.adopt.apigw.pojo.api;

import javax.validation.constraints.NotNull;

public class ClientReplyItemPojo extends ParentPojo{
	
	private Integer id;
	
	@NotNull
	private Integer clientid;
	
	@NotNull
	private String attribute;
	
	@NotNull
	private String attributevalue;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getClientid() {
		return clientid;
	}

	public void setClientid(Integer clientid) {
		this.clientid = clientid;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getAttributevalue() {
		return attributevalue;
	}

	public void setAttributevalue(String attributevalue) {
		this.attributevalue = attributevalue;
	}

	@Override
	public String toString() {
		return "ClientReplyItemPojo [id=" + id + ", clientid=" + clientid + ", attribute=" + attribute
				+ ", attributevalue=" + attributevalue + "]";
	}
}
