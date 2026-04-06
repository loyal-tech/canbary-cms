package com.adopt.apigw.modules.auditLog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditSearchPojo {

    String moduleName;
    String entityName;
    Integer pageIndex;
    Integer pageSize;
    LocalDate startDate ;
    LocalDate endDate;

}
