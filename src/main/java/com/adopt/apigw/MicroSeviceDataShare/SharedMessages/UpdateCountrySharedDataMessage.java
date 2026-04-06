package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import lombok.Data;

@Data
public class UpdateCountrySharedDataMessage {
    private Integer id;


    private String name;


    private String status;


//    private List<State> stateList = new ArrayList<>();


    private Boolean isDelete ;

    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;

}
