package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import lombok.Data;

@Data
public class SubAreaMessage {
    private Long id;
    private String name;
    private String status;
    private Boolean isDeleted;
    private Integer countryId;
    private Integer cityId;
    private Integer stateId;
    private Integer mvnoId;
    private Long buId;
    private Long areaId;
}
