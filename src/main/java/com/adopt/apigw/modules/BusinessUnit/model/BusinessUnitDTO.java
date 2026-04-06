package com.adopt.apigw.modules.BusinessUnit.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.InvestmentCode.Domain.InvestmentCode;
import com.adopt.apigw.modules.InvestmentCodeBUmapping.IcNameBuMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BusinessUnitDTO extends Auditable implements IBaseDto {

    private Long id;
    private String buname;
    private String bucode;
    private String status;
    private Boolean isDeleted = false;
    private String planBindingType;
    private Integer mvnoId;

    private List<Long> investmentCodeid;

    private List<String> icnames=new ArrayList<>();

    private Long displayId;
    private String displayName;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId(){
        return mvnoId;
    }
}
