package com.adopt.apigw.modules.Branch.model;

import java.util.ArrayList;
import java.util.List;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.BranchService.model.BranchServiceMappingEntity;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Size;

@Data
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class BranchDTO extends Auditable implements IBaseDto {
	
	private Long id;

    private String name;

    private String status;

    private Boolean isDeleted = false;

    private String branch_code;
    
    private Integer mvnoId;
    
    private List<Long> serviceAreaIdsList;

    private List<String> serviceAreaNameList = new ArrayList<>();

    private Boolean revenue_sharing;

    private Double sharing_percentage;

    private String  dunningDays;

    private Integer displayId;
    private String displayName;
    List<BranchServiceMappingEntity> branchServiceMappingEntityList;

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
