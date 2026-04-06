package com.adopt.apigw.modules.DunningRuleBranchMapping.domain;


import com.adopt.apigw.model.postpaid.DunningRule;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltdunningrulebranchmapping")
public class DunningRuleBranchMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Integer id;

    @Column(name = "branch_id")
    private Long branchId;

    @Column(name ="service_area_id")
    private Long serviceAreaId;

    @Column(name ="partner_id")
    private Long partnerId;


    @Column(name = "dunning_rule_id")
    private Integer dunningRuleId;
}
