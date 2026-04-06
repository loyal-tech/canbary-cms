package com.adopt.apigw.modules.acl.model;

import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class AclMenuStructDTO {
	private Long menuid;
	private String name;
	private String dispName;
	private Long classid;
	Set<AclMenuStructDTO> submenu = new HashSet<>();

	private List<Integer> permits;

	public AclMenuStructDTO(Long menuid, String name, String dispName, Long classid) {
		this.menuid = menuid;
		this.name = name;
		this.dispName = dispName;
		this.classid = classid;
	}

	public AclMenuStructDTO(Long menuid, String name, String dispName, Long classid, List<Integer> permits) {
		this.menuid = menuid;
		this.name = name;
		this.dispName = dispName;
		this.classid = classid;
		this.permits = permits;
	}

//	public AclMenuStructDTO(Long menuid,
//							String name,
//							String dispName,
//							Long classid, List<Integer> permits) {
//		this.menuid = menuid;
//		this.name = name;
//		this.dispName = dispName;
//		this.classid = classid;
//		this.permits = permits;
//	}
}
