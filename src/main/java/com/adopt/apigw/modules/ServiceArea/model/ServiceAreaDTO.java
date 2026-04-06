package com.adopt.apigw.modules.ServiceArea.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceAreaPincodeRel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ServiceAreaDTO extends Auditable implements IBaseDto {
    private Long id;
    private String name;
    private String status;
    private Boolean isDeleted = false;
    private String latitude;
    private String longitude;
    
    private Long areaid;
    private Integer mvnoId;

    private List<Integer> pincodes;

    private Long cityid;

    private Long displayId;
    private String displayName;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoId;
	}

}
