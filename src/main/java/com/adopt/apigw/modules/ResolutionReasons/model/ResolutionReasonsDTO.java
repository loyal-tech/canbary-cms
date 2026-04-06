package com.adopt.apigw.modules.ResolutionReasons.model;

import com.adopt.apigw.core.dto.IBaseDto2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.tickets.domain.ResoSubCategoryMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class ResolutionReasonsDTO extends Auditable implements IBaseDto2 {
    private Long id;
    private String name;
    private String status;
    private Boolean isDeleted = false;

    private Integer mvnoId;
    private Long buId;
    
    private Integer lcoId;
    private List<ResoSubCategoryMapping> resoSubCategoryMappingList;
    private List<RootCauseResolutionMapping> rootCauseResolutionMappingList;

//    private Integer lcoId;

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

    public Long getBuId() {
        return buId;
    }

    public void setBuId(Long buId) {
        this.buId = buId;
    }
}
