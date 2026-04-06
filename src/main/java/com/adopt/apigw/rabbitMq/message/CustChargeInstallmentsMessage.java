package com.adopt.apigw.rabbitMq.message;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CustChargeInstallmentsMessage {

    private Integer custChargeDetailsId;
    private Integer custChargeHistoryId;
    private LocalDate installmentStartDate;
    private LocalDate nextInstallmentDate;
    private LocalDate lastInstallmentDate;
    private Integer installmentNo;

    public CustChargeInstallmentsMessage(Integer custChargeDetailsId, LocalDate nextInstallmentDate, LocalDate lastInstallmentDate, Integer installmentNo){
        this.custChargeDetailsId = custChargeDetailsId;
        this.nextInstallmentDate = nextInstallmentDate;
        this.lastInstallmentDate = lastInstallmentDate;
        this.installmentNo = installmentNo;
    }

}
