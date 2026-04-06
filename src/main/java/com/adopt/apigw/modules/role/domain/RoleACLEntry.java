package com.adopt.apigw.modules.role.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Setter
@Getter
@Table(name = "tblmaclentry")
@NoArgsConstructor
public class RoleACLEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

//    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "roleid")
//    @ToString.Exclude

    @Column(nullable = false, name = "roleid")
    private Long roleId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private int menuid;

    public RoleACLEntry(Role role, String code, int menuid, Long id) {
        this.id = id;
        this.roleId = role.getId();
        this.code = code;
        this.menuid = menuid;
    }
}
