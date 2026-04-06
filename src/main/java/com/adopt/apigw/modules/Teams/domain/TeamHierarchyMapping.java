package com.adopt.apigw.modules.Teams.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tbltteamhierarchymapping")
public class TeamHierarchyMapping implements Serializable{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "team_id",nullable = false)
    Integer teamId;

    @Column(name = "hierarchy_id", nullable = false)
    Integer hierarchyId;

    @Column(name ="is_deleted",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

//    @Column(name = "next_team_id",nullable = false)
//    Integer nextTeamId;

    @Column(name = "order_number",nullable = false)
    Integer orderNumber;

//    @Column(name = "team_action", nullable = false)
//    private String action;

    @OneToMany(targetEntity = QueryFieldMapping.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "team_hir_mapping_id")
    private List<QueryFieldMapping> queryFieldList;


    @Column(name = "team_action")
    private String teamAction;


    @Column(name = "team_condition")
    private String teamCondition;

    @Column(name = "tat_id")
    private Integer tat_id;

    @Column(name = "is_auto_assign")
    private Boolean isAutoAssign;

    @Column(name = "is_auto_approve")
    private Boolean isAutoApprove;


}
