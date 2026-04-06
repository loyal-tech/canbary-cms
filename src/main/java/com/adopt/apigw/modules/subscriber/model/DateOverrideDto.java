package com.adopt.apigw.modules.subscriber.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class DateOverrideDto {

    boolean dateOverrideFlag;
    private LocalDateTime changePlanStartDate;
    private LocalDateTime changePlanEndDate;
}
