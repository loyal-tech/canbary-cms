package com.adopt.apigw.modules.role.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.acl.model.CustomACLEntryDTO;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id")
public class RoleDTO extends Auditable implements IBaseDto {
	

	private Long id;

	@NotNull
    private String rolename;
    
	@NotNull
    private String status;

	@NotNull
	private Boolean sysRole = false;

    //private Set<Integer> staffuserIds;
	
	private List<CustomACLEntryDTO> aclEntryPojoList = new ArrayList<>();
	
    private Integer mvnoId;

	private Integer lcoId;


	public Boolean getDelete() {
		return isDelete;
	}

	public void setDelete(Boolean delete) {
		isDelete = delete;
	}

	private Boolean isDelete = false;

//	private Integer lcoId;

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
