package com.adopt.apigw.modules.TimeBasePolicy.module;

import com.adopt.apigw.core.dto.IBaseDto2;
import com.adopt.apigw.modules.PriceGroup.model.PriceBookDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class TimeBasePolicyDetailsDTO implements IBaseDto2 {

    private Long detailsid;
    private String fromDay;
    private String toDay;
    private String fromTime;
    private String toTime;
    private Long qqsid;
    private Boolean access;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private Long buId;
    private String qos_name;
    private Boolean isFreeQuota;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private TimeBasePolicyDTO timeBasePolicy;

    @Override
    public Long getIdentityKey() {
        return detailsid;
    }
}
