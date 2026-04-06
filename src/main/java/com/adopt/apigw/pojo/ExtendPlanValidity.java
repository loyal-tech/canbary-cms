package com.adopt.apigw.pojo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExtendPlanValidity {

    private Integer custPlanMapppingId;

    private String extend_validity_remarks;

    private Long validityDays = 0L;

    private boolean extentionforChild;

    private boolean isPlanGroup;

    private Integer planGroupId;

    private LocalDate downStartDate;

    private LocalDate downEndDate;
}
