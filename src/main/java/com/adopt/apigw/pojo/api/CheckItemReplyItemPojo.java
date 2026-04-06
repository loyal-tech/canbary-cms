package com.adopt.apigw.pojo.api;

import java.time.LocalDateTime;

import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;

@Data
public class CheckItemReplyItemPojo extends Auditable {

	private Integer id;	
	
	private Integer radiusProfileCheckItemId;	
	
	private String attribute;
	
	private Integer radiusprofileid;
	
	private String attributevalue;

    @JsonBackReference
	private RadiusProfileCheckItemPojo radiusProfileCheckItem;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRadiusProfileCheckItemId() {
		return radiusProfileCheckItemId;
	}

	public void setRadiusProfileCheckItemId(Integer radiusProfileCheckItemId) {
		this.radiusProfileCheckItemId = radiusProfileCheckItemId;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public Integer getRadiusprofileid() {
		return radiusprofileid;
	}

	public void setRadiusprofileid(Integer radiusprofileid) {
		this.radiusprofileid = radiusprofileid;
	}

	public String getAttributevalue() {
		return attributevalue;
	}

	public void setAttributevalue(String attributevalue) {
		this.attributevalue = attributevalue;
	}

	public LocalDateTime getCreatedate() {
		return super.getCreatedate();
	}

	public void setCreatedate(LocalDateTime createdate) {
		super.setCreatedate(createdate);
	}

	public LocalDateTime getUpdatedate() {
		return super.getUpdatedate();
	}

	public void setUpdatedate(LocalDateTime updatedate) {
		super.setUpdatedate(updatedate);
	}
}
