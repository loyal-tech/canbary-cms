package com.adopt.apigw.modules.LocationMaster.module;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "LocationMasterMapping",description = "This is data transfer object for LocationMaster which is used to create new LocationMaster")
@NoArgsConstructor
public class LocationMasterMappingDto {

    @ApiModelProperty(notes = "Mapping Id",required=false)
    private Long mappingId;

    @ApiModelProperty(notes = "Name of the mac ",required=true)
    private String mac;

    @ApiModelProperty(notes = "Name of the identity ",required=false)
    private String identity;

    private Boolean isUsed;

    private String name;

    private Long locationId;
    public LocationMasterMappingDto(String name, String mac) {
        this.name = name;
        this.mac = mac;
    }

    public LocationMasterMappingDto(String name, String mac, Long locationId) {
        this.name = name;
        this.mac = mac;
        this.locationId = locationId;
    }
}
