package com.adopt.apigw.modules.Cas.Domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmcaspackagemapping")
public class CasePackageMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "packagename")
    String casname;

    @Column(name = "packageid")
    Long packageid;

    @Column(name = "casepackage_mapping_id")
    Long cpmappingid;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

}
