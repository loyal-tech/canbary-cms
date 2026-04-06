package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import lombok.Data;

@Data
public class UpdateSubBusinessVerticalsSharedDataMessage {

    private Long id;

    private String sbvname;

    private Integer businessVerticalId ;

    private String status;

    private Boolean isDeleted = false;

    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;
}
