package com.adopt.apigw.pojo.api;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TaxDetailCountResDTO {
    private Integer id;
    private String invoiceId;
    private String name;
    private Double percentage;
    private Double absoluteAmount;
    private Integer rangefrom;
    private Integer rangeupto;
    private Integer level;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double taxAmount;
    private String description;
    private Integer tiertaxid;
    private Integer slabtaxid;
}
