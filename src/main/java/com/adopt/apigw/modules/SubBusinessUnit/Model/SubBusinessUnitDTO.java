package com.adopt.apigw.modules.SubBusinessUnit.Model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@Data
public class SubBusinessUnitDTO extends Auditable implements IBaseDto {

    private Long id;

    private String subbuname;

    private String subbucode;

    private Long businessunitid;

    private Boolean isDeleted = false;

    private Integer mvnoId;
    @NotNull
    private String status;

    private Integer displayId;
    private String displayName;

    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

    @Override
    public void setMvnoId(Integer mvnoId){
        this.mvnoId = mvnoId;
    }
}
