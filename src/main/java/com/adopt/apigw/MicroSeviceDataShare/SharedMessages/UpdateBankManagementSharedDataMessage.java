package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import lombok.Data;

@Data
public class UpdateBankManagementSharedDataMessage {


    private Long id;

    private String bankname;

    private String accountnum;

    private String ifsccode;

    private String bankholdername;

    private String status;


    private String bankcode;


    private Integer mvnoId;


    private Boolean isDeleted = false;

    private String banktype;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;

    private String lastModifiedByName;
}
