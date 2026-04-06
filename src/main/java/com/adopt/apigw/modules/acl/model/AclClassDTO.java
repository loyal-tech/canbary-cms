package com.adopt.apigw.modules.acl.model;

import lombok.Data;

import java.util.List;

import com.adopt.apigw.modules.acl.domain.AclOperations;

@Data
public class AclClassDTO {

    private Long id;

    private String classname;

    private String dispname;

    private Long disporder;

    private Long operallid;

    private List<AclOperationsDTO> aclOperationsList;

}
