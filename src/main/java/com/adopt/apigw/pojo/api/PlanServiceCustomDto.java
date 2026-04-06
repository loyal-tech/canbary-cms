package com.adopt.apigw.pojo.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanServiceCustomDto {
    private Integer id;
    private String name;
    private String displayName;
    private Integer mvnoId;
    private Integer displayId;
    private List<ServiceParamMappingDto> serviceParamMappingList;
    private String mvnoName;


}
