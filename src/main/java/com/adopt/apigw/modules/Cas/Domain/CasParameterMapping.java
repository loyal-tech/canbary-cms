package com.adopt.apigw.modules.Cas.Domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmcasparametermapping")
public class CasParameterMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "parameter_name")
    String paramName;

    @Column(name = "parameter_value")
    String paramValue;

    @Column(name = "cas_param_mapping_id")
    private Long casId;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

}
