package com.adopt.apigw.modules.acl.model;

import java.util.ArrayList;
import java.util.List;

import com.adopt.apigw.modules.acl.domain.CustomACLEntry;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id")
public class CustomACLEntryDTO {

	private Integer id;

    private int classid;
        
	private Integer roleId;	
    
    private int permit;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getClassid() {
		return classid;
	}

	public void setClassid(int classid) {
		this.classid = classid;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public int getPermit() {
		return permit;
	}

	public void setPermit(int permit) {
		this.permit = permit;
	}
	
	public CustomACLEntryDTO(CustomACLEntry aclEntry) {
		this.id = aclEntry.getId();
		this.classid = aclEntry.getClassid();
		this.permit = aclEntry.getPermit();
//		if(aclEntry.getRole() != null)
//			this.roleId = aclEntry.getRole().getId().intValue();
	}

	public CustomACLEntryDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
