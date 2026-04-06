package com.adopt.apigw.pojo.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@NoArgsConstructor
@Data
public class CustomerLocationMappingDto {

    private Long id;

    private Long custId;

    private Long locationId;

    private String locationName;

    private Boolean isDelete;

    private Boolean isActive;

    private Boolean isParentLocation;

    private String mac;

    private Integer mvnoId;

    private boolean isUsed;

    public CustomerLocationMappingDto(Long locationId,String locationName, Boolean isParentLocation, String mac) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.isParentLocation = isParentLocation;
        this.mac = mac;
    }
}
