package com.adopt.apigw.modules.LocationMaster.module;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@ApiModel(value = "LocationMaster", description = "This is data transfer object for LocationMaster which is used to Update LocationMaster")
public class UpdateLocationMasterDto {
	@ApiModelProperty(notes = "This is location id to update Location Master", required = true)
	private Long locationMasterId;

	@ApiModelProperty(notes = "Name of the Location Master", required = true)
	private String name;

	@ApiModelProperty(notes = "This is Location Master status. (Active or Inactive)", required = true)
	private String status;

	@ApiModelProperty(notes = "This is Location Master check item.", required = true)
	private String checkItem;
	
	@ApiModelProperty(notes = "This is Location Master locationIdentifyAttribute.", required = false)
	private String locationIdentifyAttribute;
	
	@ApiModelProperty(notes = "This is Location Master locationValue.", required = false)
    private String locationIdentifyValue;

	private List<LocationMasterMappingDto> locationMasterMapping;

}
