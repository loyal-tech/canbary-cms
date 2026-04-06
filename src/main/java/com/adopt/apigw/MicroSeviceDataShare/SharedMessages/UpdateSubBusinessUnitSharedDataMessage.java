package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateSubBusinessUnitSharedDataMessage {

    private Long id;


    private String subbuname;


    private String subbucode;


    private Long businessunitid;

    private Boolean isDeleted = false;


    private Integer mvnoId;


    private String status;    private String createdByName;

    private String lastModifiedByName;

    private Integer createdById;
    private Integer lastModifiedById;
    private LocalDateTime createdate;

}
