package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.model.common.VasPlanCharge;
import lombok.Data;

import java.util.List;

@Data
public class UpdateVasSharedDataMessage {
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
    private Boolean isdelete;
    private Boolean isdefault;
    private List<VasPlanCharge> chargeList;
}
