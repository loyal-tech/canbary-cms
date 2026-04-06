package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ServiceAuditDTO {
    private Long id;
    private LocalDateTime serviceStopTime;
    private Long staffId;
    private String action;
    private Long cprid;
    private String reasonId;
    private String remarks;
    private String reasonCategory;
    private LocalDateTime servicestarttime;
    private String staffName;
    private Integer custServiceMappingId;
    private String reason;
    private LocalDate auditDate;

}
