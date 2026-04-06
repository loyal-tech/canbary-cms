package com.adopt.apigw.modules.StaffLedgerTransaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class StaffLedgerTransactionMappingDTO {

    private Integer paymentid;
    private Integer transfferedid;
    private Double transfferedamount;
    private LocalDate date;

}
