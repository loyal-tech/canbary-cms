package com.adopt.apigw.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomPeriodInvoiceDTO {
    private Integer customerId;
    private LocalDate customStartDate;
    private LocalDate customEndDate;
    private String invoiceType;
    private Integer createdById;
    private String createdByName;
}
