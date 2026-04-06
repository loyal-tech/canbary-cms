package com.adopt.apigw.pojo.api;

import lombok.Data;

import javax.validation.constraints.NotNull;

import com.adopt.apigw.model.common.Auditable;

@Data
public class StatePojo extends Auditable {

    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String status;

    @NotNull
    private CountryPojo countryPojo;
    private String countryName;
    private Boolean isDeleted = false;
    
    private Integer mvnoId;

    private Integer displayId;
    private String displayName;

}
