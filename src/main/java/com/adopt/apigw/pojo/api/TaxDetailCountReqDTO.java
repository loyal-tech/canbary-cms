package com.adopt.apigw.pojo.api;

import lombok.Data;

@Data
public class TaxDetailCountReqDTO {

    private Integer planId;
    private Integer locationId;
    private Integer custId;
    private Integer chargeId;

    public TaxDetailCountReqDTO() {
    }

    public TaxDetailCountReqDTO(Integer planId, Integer locationId, Integer custId, Integer chargeId) {
        this.planId = planId;
        this.locationId = locationId;
        this.custId = custId;
        this.chargeId = chargeId;
    }
}
