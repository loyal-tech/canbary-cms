package com.adopt.apigw.modules.acl.domain;

import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@Table(name = "tblacloperations")
public class AclOperations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opid")
    private Long id;

    @Column(name = "opname")
    private String opName;

    @Column(name = "classid")
    private Long classid;
    
    @Column(name = "parentoperationid")
    private Long parentOperationId;
    
    @Column(name = "is_visible")
    private boolean isVisible;

}