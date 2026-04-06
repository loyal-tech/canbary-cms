package com.adopt.apigw.rabbitMq.message;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ServiceChnageStatus {
    private Integer custId;
    private Integer ServiceId;
    private Integer totalDays;
    private Integer custPlanmappigId;
    private Integer serviceMappingId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long debitDocId;
}
