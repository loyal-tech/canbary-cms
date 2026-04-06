package com.adopt.apigw.modules.TumilIdValidation;

import lombok.Data;
import java.time.LocalDate;

@Data
public class IdValidationResponseMessage {
    private String householdId;
    private String householdType;
    private String fsrId;
    private String fsrName;
    private String brand;
    private String account;
    private String plan;
    private LocalDate activationDate;
    private String townshipName;
    private String wardName;
    private String streetName;
    private String houseNo;
    private String buildingName;
    private Integer mvnoId;
    private Integer customerId;
    private String speed;
    private String customerName;
    private String customerEmail;
    private String customerMobileNumber;
    private String customerAdress;
    private String activationStatus;
}
