package com.adopt.apigw.pojo.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PlanDetailsDto {
    private Integer planId;
    private String planName;
    private LocalDateTime expiryDate;

    public PlanDetailsDto(Integer planId, String planName, LocalDateTime expiryDate) {
        this.planId = planId;
        this.planName = planName;
        this.expiryDate = expiryDate;
    }
}
