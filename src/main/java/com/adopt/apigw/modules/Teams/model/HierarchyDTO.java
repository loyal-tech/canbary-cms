package com.adopt.apigw.modules.Teams.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.core.dto.IBaseDto2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.Teams.domain.TeamHierarchyMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HierarchyDTO  implements IBaseDto2 {


    private Long id;

    //private String  flowName;

    //private String status;

    private String hierarchyName;

    private String eventName;

    private List<TeamHierarchyMapping> teamHierarchyMappingList ;

    private Integer mvnoId;

    private Boolean isDeleted = false;

    private Long buId;

    private Integer lcoId;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }
}
