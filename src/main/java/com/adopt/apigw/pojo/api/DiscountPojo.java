package com.adopt.apigw.pojo.api;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.adopt.apigw.model.common.Auditable;

public class DiscountPojo extends Auditable {
	
	private Integer id;

	@NotNull
    private String name;

	@NotNull
    private String desc;

	@NotNull
    private String status;

    private List<DiscountMappingPojo> discoundMappingList = new ArrayList<>();

	private List<DiscountPlanMappingPojo> discoundPlanMappingList = new ArrayList<>();

	private Boolean isDelete = false;
	
	private Integer mvnoId;

	private Long buId;
	private String mvnoName;


	public String getMvnoName() {
		return mvnoName;
	}

	public void setMvnoName(String mvnoName) {
		this.mvnoName = mvnoName;
	}



	public Long getBuId() {
		return buId;
	}

	public void setBuId(Long buId) {
		this.buId = buId;
	}

	public Boolean getDelete() {
		return isDelete;
	}

	public void setDelete(Boolean delete) {
		isDelete = delete;
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<DiscountMappingPojo> getDiscoundMappingList() {
		return discoundMappingList;
	}

	public void setDiscoundMappingList(List<DiscountMappingPojo> discoundMappingList) {
		this.discoundMappingList = discoundMappingList;
	}

	public List<DiscountPlanMappingPojo> getDiscoundPlanMappingList() {
		return discoundPlanMappingList;
	}

	public void setDiscoundPlanMappingList(List<DiscountPlanMappingPojo> discoundPlanMappingList) {
		this.discoundPlanMappingList = discoundPlanMappingList;
	}
	
	public Integer getMvnoId() {
		return mvnoId;
	}

	public void setMvnoId(Integer mvnoId) {
		this.mvnoId = mvnoId;
	}

	@Override
	public String toString() {
		return "DiscountPojo [id=" + id + ", name=" + name + ", desc=" + desc + ", status=" + status
				+ ", discoundMappingList=" + discoundMappingList + ", discoundPlanMappingList="
				+ discoundPlanMappingList + "]";
	}
}
