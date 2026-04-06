package com.adopt.apigw.modules.workflow.domain;

import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

    @Data
    @Entity
    @Table(name = "tblworkflowassignstaffmapping")
    public class WorkflowAssignStaffMapping {

        @Id
        @DiffIgnore
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "event_name")
        private String eventName;
        @DiffIgnore
        @Column(name = "entity_id")
        private Integer entityId;
        @DiffIgnore
        @Column(name = "staff_id")
        private Integer staffId;
    }

