package com.adopt.apigw.modules.acl.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "tblaclclass")
public class AclClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classid")
    private Long id;

    @Column(name = "classname")
    private String classname;

    @Column(name = "dispname")
    private String dispname;

    @Column(name = "disporder")
    private Long disporder;

    @Column(name = "operallid")
    private Long operallid;

    @OneToMany(mappedBy = "classid", cascade = CascadeType.ALL)
    @OrderBy("id asc")
    private List<AclOperations> aclOperationsList;


}
