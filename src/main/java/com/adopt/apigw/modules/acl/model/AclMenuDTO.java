package com.adopt.apigw.modules.acl.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AclMenuDTO {


    private Long menuid;
    
	private String name;
	
	private String dispName;
	
	private List<AclClassDTO> submenu;
}
