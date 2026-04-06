package com.adopt.apigw.model.postpaid;

import lombok.Data;

import javax.persistence.*;
@Data
@Entity
@Table(name = "tbltdepartmentplanmapping")
public class DepartmentPlanMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    @ManyToOne
    @JoinColumn(name = "plan_id")
    private PostpaidPlan planId;
}
