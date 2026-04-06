package com.adopt.apigw.modules.Area.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.Pincode.model.PincodeDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class AreaDTO extends Auditable implements IBaseDto {

    private Long id;
    private String name;
    private String status;
    private Boolean isDeleted = false;
    private Integer countryId;
    private Integer stateId;
    private Integer cityId;
    private String countryName;
    private String stateName;
    private String cityName;
    private Integer pincodeId;
    private String code;

    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PincodeDTO pincode;
    
    private Integer mvnoId;

    private Long displayId;
    private String displayName;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		return mvnoId;
	}

}
