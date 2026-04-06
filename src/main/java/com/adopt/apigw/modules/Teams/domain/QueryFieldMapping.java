package com.adopt.apigw.modules.Teams.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "tbltqueryfieldnmapping")
public class QueryFieldMapping implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    //    @ManyToOne(targetEntity = TeamHierarchyMapping.class, cascade = CascadeType.ALL)
    @Column(name = "team_hir_mapping_id")
    Integer teamHirMappingId;

    @Column(name = "query_field", nullable = false)
    String queryField;

    @Column(name = "query_operator",nullable = false)
    String queryOperator;

    @Column(name = "query_value",nullable = false)
    String queryValue;

    @Column(name = "query_condition",nullable = false)
    String queryCondition;
    
    @Column(name ="is_deleted",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
}
