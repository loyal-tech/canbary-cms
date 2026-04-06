package com.adopt.apigw.modules.Teams.model;


import com.adopt.apigw.core.dto.IBaseDto2;
import com.adopt.apigw.modules.Teams.domain.QueryFieldMapping;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Data
public class TeamHierarchyMappingDTO implements IBaseDto2 {


    private Integer id;


    private Integer teamId;


    private Integer hierarchyId;


    private Boolean isDeleted = false;


    private Integer nextTeamId;


    private Integer orderNumber;

    private String teamAction;

    private String teamCondition;

    private  Integer tat_id;

    private List<QueryFieldDTO> queryFieldMappingList;


    @Override
    public Long getIdentityKey() {
        return null;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }

    @Override
    public Long getBuId() {
        return null;
    }
}
