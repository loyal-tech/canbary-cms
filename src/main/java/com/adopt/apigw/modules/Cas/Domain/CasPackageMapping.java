package com.adopt.apigw.modules.Cas.Domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmcaspackagemapping")
public class CasPackageMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "packagename")
    String packageName;

    @Column(name = "packageid")
    Long packageId;

    @Column(name = "casepackage_mapping_id")
    Long casMasterId;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

}