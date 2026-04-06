package com.adopt.apigw.pojo.api;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VasPlanPojo{

    private Integer id;
    private String name;
    private Integer pauseDaysLimit;
    private Integer pauseTimeLimit;
    private Integer tatId;
    private Integer inventoryReplaceAfterYears;
    private Integer inventoryPaidMonths;
    private Integer inventoryCount;
    private Integer shiftLocationYears;
    private Integer shiftLocationMonths;
    private Integer shiftLocationCount;
    private String paymentType;
    private Integer vasAmount;
    private Integer mvnoId;
    private Integer validity;
    private String unitsOfValidity;
    private Boolean isdefault;
    private List<VasPlanChargePojo> chargeList = new ArrayList<>();
}