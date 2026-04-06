package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.model.postpaid.State;
import lombok.Data;

@Data
public class SaveCitySharedDataMessage {

    private Integer id;


    private String name;


    private String status;


    private Integer countryId;


    private State state;


    private Boolean isDelete = false;


    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;
}
