package com.adopt.apigw.pojo.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPlanDetail {
    private Integer id;
    private LocalDate expiryDate;
    private Integer validity;
    private Long offerPrice;
    private Long commission;
}
