package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import lombok.Data;

@Data
public class SaveCustAccountProfileSharedDataMessage {
    private Long id;
    private String name;
    private String prefix;
    private String type;
    private String startFrom;
    private boolean year;
    private boolean month;
    private boolean day;
    private String status;
    private boolean isDelete;
    private Integer mvnoId;
    private String createdByName;
    private String lastModifiedByName;
    private Integer createdById;
    private Integer lastModifiedById;
}
