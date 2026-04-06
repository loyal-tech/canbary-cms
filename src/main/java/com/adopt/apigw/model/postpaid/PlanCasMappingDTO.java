package com.adopt.apigw.model.postpaid;

import lombok.Data;

@Data
public class PlanCasMappingDTO {
    private long id;
    private long planId;
    private long casId;
    private  long packageId;
}
