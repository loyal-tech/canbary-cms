package com.adopt.apigw.pojo.api;



import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
public class VasPlanChargePojo {
    private Integer id;
    private Integer chargeId;
    private Double billingCycle;
    private Double chargePrice;
    @CreationTimestamp
    private LocalDateTime createDate;
    private Integer vasPlanId;

}
