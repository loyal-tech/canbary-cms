package com.adopt.apigw.pojo;

import lombok.Data;

@Data
public class DocumentDto {
    private Long docId;
    private String documentNumber;
    private String documentType;
}
