package com.adopt.apigw.rabbitMq.message;

import java.util.ArrayList;
import java.util.List;

import com.adopt.apigw.modules.acl.domain.CustomACLEntry;
import com.adopt.apigw.modules.acl.model.CustomACLEntryDTO;
import com.adopt.apigw.modules.role.domain.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleMessage {

	private Long id;

	private String rolename;
    
    private String status;

	private Boolean sysRole = false;
	
	private List<CustomACLEntryDTO> aclEntryDTOList = new ArrayList<>();
	
    private Integer mvnoId;
    
    private Boolean isDelete = false;
    
    public RoleMessage(Role role) {
    	this.id = role.getId();
    	this.rolename = role.getRolename();
    	this.status = role.getStatus();
    	this.sysRole = role.getSysRole();
    	this.mvnoId = role.getMvnoId();
    	this.isDelete = role.getIsDelete();
//    	if(role.getAclEntry() != null && role.getAclEntry().size() > 0) {
//    		List<CustomACLEntryDTO> customACLEntryDTOList = new ArrayList<CustomACLEntryDTO>();
//    		for (CustomACLEntry customACLEntry : role.getAclEntry()) {
//    			customACLEntryDTOList.add(new CustomACLEntryDTO(customACLEntry));
//			}
//    		this.aclEntryDTOList = customACLEntryDTOList;
//       	}
    }
    
}
