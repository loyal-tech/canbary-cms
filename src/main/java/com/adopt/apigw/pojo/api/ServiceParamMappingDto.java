package com.adopt.apigw.pojo.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServiceParamMappingDto {
    private Long id;
    private Long serviceid;
    private String serviceParamName;
    private Long serviceParamId;

    public ServiceParamMappingDto(Long id, Long serviceid, String serviceParamName, Long serviceParamId) {
        this.id = id;
        this.serviceid = serviceid;
        this.serviceParamName = serviceParamName;
        this.serviceParamId = serviceParamId;
    }

}
