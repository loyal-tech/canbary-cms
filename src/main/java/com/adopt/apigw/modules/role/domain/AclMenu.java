package com.adopt.apigw.modules.role.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "tblmaclmenu")
@Entity
public class AclMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "code")
    private String code;
    @Column(name = "parentid")
    private Long parentid;
    @Column(name = "ismenu")
    private Boolean ismenu;
    @Column(name = "icon")
    private String icon;
    @Column(name = "url")
    private String url;
    @Column(name = "isweb")
    private Boolean isweb;
    @Column(name = "ismobile")
    private Boolean ismobile;
}
