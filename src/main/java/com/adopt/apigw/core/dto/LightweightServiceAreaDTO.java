package com.adopt.apigw.core.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LightweightServiceAreaDTO {
    private Long id;
    private String name;
    private Integer mvnoId;
    private String status;
}
