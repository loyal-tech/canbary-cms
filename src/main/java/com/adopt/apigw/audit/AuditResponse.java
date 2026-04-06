package com.adopt.apigw.audit;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AuditResponse {

    private int id;

    private String author;

    private String userName;

    private String userId;

    private String ipAddress;

    private String operationType;

    private LocalDateTime commitDate;

    private String entityState;

    private String createdate;

    private String updatedate;

    private Integer createdById;

    private Integer lastModifiedById;

    private String classPath;

    private Integer entityId;

    private String entityObject;

    private String className;

    private Double metadataId;

    private String changesObject;

    private List<ChangePojo> changePojoList;

}
