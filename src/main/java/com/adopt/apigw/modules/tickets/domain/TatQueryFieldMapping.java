package com.adopt.apigw.modules.tickets.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tbltsubdomaintatqueryfieldnmapping")
public class TatQueryFieldMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "query_field")
    String queryField;

    @Column(name = "query_operator")
    String queryOperator;

    @Column(name = "query_value")
    String queryValue;

    @Column(name = "query_condition")
    String queryCondition;

    @Column(name = "tat_mapping_id")
    Integer tatMappingId;

    @Column(name ="is_deleted",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
}
