package com.adopt.apigw.controller.common.VasPlan;

import com.adopt.apigw.model.common.VasPlanCharge;
import lombok.*;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class VasPlanResponseDTO {
    private String vasName;
    private Integer vasOfferPrice;
    private Integer pauseDaysLimit;
    private Integer pauseTimeLimit;
    private Integer tatId;
    private Integer inventoryReplaceAfterYears;
    private Integer inventoryPaidMonths;
    private Integer inventoryCount;
    private Integer shiftLocationYears;
    private Integer shiftLocationMonths;
    private Integer shiftLocationCount;
    private Integer validity;
    private String unitsOfValidity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime expiryDate;
    private  Boolean isActive;

    private String installmentType;
    private Integer totalInstallments;
    private LocalDate installmentStartDate;
    private LocalDate installmentEndDate;
    private Integer installmentNo;
    private BigDecimal amountPerInstallment;
    private LocalDate installmentNextDate;
    private Boolean installmentEnabled;
    public VasPlanResponseDTO() {

    }
}
