package com.adopt.apigw.modules.NetworkDevices.model.SloatModel;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
public class OLTPortDTO extends Auditable implements IBaseDto {

    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String status;
    private Boolean isDeleted = false;

    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OLTSlotDetailDTO oltslots;
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
