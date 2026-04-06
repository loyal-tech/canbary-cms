package com.adopt.apigw.modules.acl.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class AclMenuQueryEntry {

    @Id
    private Long menuid;
    private String name;
    @Column(name = "dispname")
    private String dispName;
    private Long classid;
    private Long parentid;
    private Long level;
    private Long aclid;
}
