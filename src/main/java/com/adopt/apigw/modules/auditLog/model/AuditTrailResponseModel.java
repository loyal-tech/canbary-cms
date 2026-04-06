package com.adopt.apigw.modules.auditLog.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditTrailResponseModel {

    private Integer commitId;
    private String moduleName;
    private String actionType;
    private String columnName;
    private String oldValue;
    private String newValue;
    private String authorUserName;
    private String authorUserTeams;
    private Integer authorUserId;
    private Integer mvnoId;
    private Integer recordId;
    private String recordName;
    private LocalDateTime updatedOn;
    private String ipAddress;
    private String entityName;
    private String submoduleName;
    private String snapshot;
    private String entityType;


}
