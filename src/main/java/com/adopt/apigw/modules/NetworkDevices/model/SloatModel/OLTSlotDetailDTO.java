package com.adopt.apigw.modules.NetworkDevices.model.SloatModel;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class OLTSlotDetailDTO extends Auditable implements IBaseDto {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String status;
    private Boolean isDeleted = false;

    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OLTPortDTO> oltPortDetailsList = new ArrayList<>();

    private Long networkId;

    private Integer mvnoId;
    
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
