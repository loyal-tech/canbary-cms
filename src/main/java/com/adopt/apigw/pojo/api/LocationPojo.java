package com.adopt.apigw.pojo.api;

import lombok.Data;

import javax.validation.Valid;

@Data
public class LocationPojo extends ParentPojo{

	private Integer id;

	@Valid
    private String name;
	
	@Valid
    private String status;

	private Boolean isDelete=false;
}
