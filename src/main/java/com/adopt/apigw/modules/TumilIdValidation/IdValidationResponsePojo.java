package com.adopt.apigw.modules.TumilIdValidation;

import lombok.Data;

@Data
public class IdValidationResponsePojo {
    private String householdId;
    private String householdType;
    private String fsrId;
    private String fsrName;
    private String townshipName;
    private String wardName;
    private String streetName;
    private String houseNo;
    private String buildingName;
    private Integer mvnoId;
}
