package com.adopt.apigw.controller.common.VasPlan;

import lombok.Data;

@Data
public class VasPlanUpdateDTO {

    private Integer oldVasId;

    private Integer newVasId;

    private Integer custId;

    private String installmentFrequency;

    private Integer installment_no;

    private Integer totalInstallments;

}
