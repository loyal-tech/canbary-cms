package com.adopt.apigw.audit;

import lombok.Data;

@Data
public class AuditSearchRequest {

    private String fromDate;

    private String toDate;

    private String author;

    private String entity;

    private Integer entityId;

    private String classPath;

    private String className;

    private Double metadataId;

}
