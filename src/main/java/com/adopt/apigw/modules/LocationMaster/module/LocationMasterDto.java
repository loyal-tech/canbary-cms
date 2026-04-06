package com.adopt.apigw.modules.LocationMaster.module;

import com.adopt.apigw.modules.LocationMaster.domain.LocationMasterMapping;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@ApiModel(value = "LocationMaster",description = "This is data transfer object for LocationMaster which is used to create new LocationMaster")
@NoArgsConstructor
public class LocationMasterDto {
	@ApiModelProperty(notes = "Name of the Location Master",required=true)
	private String name;
	
	@ApiModelProperty(notes = "This is Location Master status. (Active or Inactive)",required=true)
	private String status;
	
	@ApiModelProperty(notes = "This is Location Master check item.",required=false)
	private String checkItem;
	
    @ApiModelProperty(notes = "This is Location Master locationIdentifyAttribute.", required = false)
	private String locationIdentifyAttribute;

    @ApiModelProperty(notes = "This is Location Master locationValue.", required = false)
    private String locationIdentifyValue;
	@ApiModelProperty(notes = "This is mvno Id.", required = true)
	private Long mvnoName;


	private List<LocationMasterMappingDto> locationMasterMapping;

	public LocationMasterDto(LocationMasterDto locationMasterDto) {
		this.name = locationMasterDto.getName();
		this.status = locationMasterDto.getStatus();
		this.checkItem = locationMasterDto.getCheckItem();
		this.locationIdentifyAttribute = locationMasterDto.getLocationIdentifyAttribute();
		this.locationIdentifyValue = locationMasterDto.getLocationIdentifyValue();
		this.locationMasterMapping = locationMasterDto.getLocationMasterMapping();
	}


}
