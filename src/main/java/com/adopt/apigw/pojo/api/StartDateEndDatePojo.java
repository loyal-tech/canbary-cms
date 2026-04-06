package com.adopt.apigw.pojo.api;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StartDateEndDatePojo {

    LocalDate startDate;
    LocalDate endDate;

}
