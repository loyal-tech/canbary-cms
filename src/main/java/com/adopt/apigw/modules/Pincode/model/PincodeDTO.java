package com.adopt.apigw.modules.Pincode.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.Area.model.AreaDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
public class PincodeDTO extends Auditable implements IBaseDto {

    private Long pincodeid;
    private String pincode;
    private String status;
    private Boolean isDeleted = false;
    private Integer countryId;
    private Integer stateId;
    private Integer cityId;
    private String cityName;
    private String stateName;
    private String countryName;
    private String areas;

    private Long displayId;
    private String displayName;

    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<AreaDTO> areaList = new ArrayList<>();
    private Integer mvnoId;
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return pincodeid;
    }

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoId;
	}

    @Override
    public void setMvnoId(Integer mvnoId){
        this.mvnoId = mvnoId;
    }

}
