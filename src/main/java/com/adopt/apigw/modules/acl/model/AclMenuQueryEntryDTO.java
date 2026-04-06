package com.adopt.apigw.modules.acl.model;

import java.util.List;

import lombok.Data;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.List;
import java.util.Set;

@Data
public class AclMenuQueryEntryDTO {
    private Long menuid;
    private String name;
    private String dispName;
    private Long classid;
    private Long parentid;
    private Long level;
    private Long aclid;
    private List<Integer> permits;
}
