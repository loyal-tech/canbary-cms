package com.adopt.apigw.pojo.api;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
public class PostpaidPlanChargePojo {

    private Integer id;

    private ChargePojo charge;

    private Integer billingCycle;

    @CreationTimestamp
    private LocalDateTime createdate;

    @JsonBackReference
    @ToString.Exclude
    private PostpaidPlanPojo plan;

    private Double chargeprice;

    private  Integer chargeId;

    private String chargeName;

    private Integer planId;

}
