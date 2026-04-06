package com.adopt.apigw.pojo.api;

import lombok.Data;

import javax.validation.constraints.NotNull;

import com.adopt.apigw.model.common.Auditable;

@Data
public class CityPojo extends Auditable {
	
	private Integer id;

	@NotNull
    private String name;
	
	@NotNull
    private String status;
	
	@NotNull
	private Integer countryId;

	private String stateName;
	private String countryName;

	@NotNull
    private StatePojo statePojo;

	private Boolean isDelete = false;

	private Integer mvnoId;

	private Integer displayId;
	private String displayName;

	public CityPojo(String name, String stateName) {
		this.name = name;
		this.stateName = stateName;
	}
}
