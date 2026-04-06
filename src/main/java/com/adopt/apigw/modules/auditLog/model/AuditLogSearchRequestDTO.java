package com.adopt.apigw.modules.auditLog.model;

import lombok.Data;

import java.sql.Date;
import java.time.LocalDate;

import com.adopt.apigw.core.dto.PaginationRequestDTO;

@Data
public class AuditLogSearchRequestDTO extends PaginationRequestDTO {

    private LocalDate fromDate;
    private LocalDate toDate;
    private String module;
    private String auditFor;
    private String operation;
    private Integer partnerId;
    private Integer customerId;

}
