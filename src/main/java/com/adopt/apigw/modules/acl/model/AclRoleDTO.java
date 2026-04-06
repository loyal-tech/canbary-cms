package com.adopt.apigw.modules.acl.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import com.adopt.apigw.modules.acl.domain.AclOperations;

@Data
public class AclRoleDTO {

    private Long roleid;

    private List<AclRoleOperationsDTO> operations = new ArrayList<AclRoleOperationsDTO>();
}
