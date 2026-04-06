package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblplanservicearearel")
@Data
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
public class PostPaidPlanServiceAreaMapping {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "planid", nullable = false, length = 40)
    private Integer planId;

    @Column(name = "serviceareaid", nullable = false, length = 40)
    private  Integer serviceId;

    @Column(name = "created_on", nullable = false, length = 40)
    private LocalDateTime createdOn;

    @Column(name = "lastmodified_on", nullable = false, length = 40)
    private LocalDateTime lastmodifiedOn ;

    @Transient
    private String createdOnString;

    @Transient
    private String lastmodifiedOnString ;

    public PostPaidPlanServiceAreaMapping(PostPaidPlanServiceAreaMapping postPaidPlanServiceAreaMapping) {
        this.id = postPaidPlanServiceAreaMapping.getId();
        this.planId = postPaidPlanServiceAreaMapping.getPlanId();
        this.serviceId = postPaidPlanServiceAreaMapping.getServiceId();
        this.createdOnString = postPaidPlanServiceAreaMapping.getCreatedOnString();
        this.lastmodifiedOnString = postPaidPlanServiceAreaMapping.getLastmodifiedOnString();
    }

    public PostPaidPlanServiceAreaMapping(Integer serviceId, Integer planId) {
        this.serviceId = serviceId;
        this.planId = planId;
    }
}
