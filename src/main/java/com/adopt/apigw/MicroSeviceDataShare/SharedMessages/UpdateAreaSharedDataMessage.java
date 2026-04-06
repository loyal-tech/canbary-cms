package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.modules.Pincode.domain.Pincode;
import lombok.Data;

@Data
public class UpdateAreaSharedDataMessage {

    private Long id;

    private String name;
    private String status;
    private Boolean isDeleted = false;


    private Integer countryId;


    private Integer cityId;


    private Integer stateId;


    private Pincode pincode;


    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;
}
