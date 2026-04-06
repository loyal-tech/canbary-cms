package com.adopt.apigw.modules.BranchService.model;


import lombok.Data;
import lombok.ToString;

import javax.persistence.*;


@Entity
@Data
@ToString
@Table(name = "tbltbranchservicemapping")
public class BranchServiceMappingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Integer id;

    @Column(name = "branch_mapping_id")
    private Long branchId;

    @Column(name = "serviceid")
    private Integer serviceId;

    @Column(name = "revenue_share_percentage")
    private Double revenueShareper;
    @Column(name = "is_deleted" ,columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;
}
