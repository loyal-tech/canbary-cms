package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import lombok.Data;

@Data
public class SaveInvestmentCodeSharedDataMessage {


    private Long id;


    private String iccode;


    private String icname;


    private Boolean isDeleted = false;


    private Integer mvnoId;


    private String status;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;

    private String lastModifiedByName;
}
