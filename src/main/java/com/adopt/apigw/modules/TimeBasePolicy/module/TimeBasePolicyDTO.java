package com.adopt.apigw.modules.TimeBasePolicy.module;

import com.adopt.apigw.core.dto.IBaseDto2;
import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TimeBasePolicyDTO extends Auditable implements IBaseDto2 {
    private Long id;
    private String name;
    private String status;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private Long buId;
    private String mvnoName;

    @JsonManagedReference
    private List<TimeBasePolicyDetailsDTO> timeBasePolicyDetailsList = new ArrayList<>();



    @Override
    public Long getIdentityKey() {
        return id;
    }
}
