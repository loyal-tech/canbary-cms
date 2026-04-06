package com.adopt.apigw.model.common;

import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "tblstaffrolerel")
@EntityListeners(AuditableListener.class)
public class StaffRoleRel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staffrolerelid", nullable = false, length = 40)
    private Long id;

    @Column(name = "staffid", nullable = false, length = 40)
    private Long staffId;

    @Column(name = "roleid", nullable = false, length = 40)
    private Long roleId;

}
