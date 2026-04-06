package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class SubscriberAllocatedIP {
    private Long currentPoolDetailsId;
    private Long currentPoolId;
    private String chargeName;
    private Integer currentChargeId;
    private Double price;
    private String ipAddress;
    private Long currentAllocatedId;
    private Date activationDate;
    private Date expiryDate;
    private String poolName;

    public SubscriberAllocatedIP() {
    }

    public SubscriberAllocatedIP(Long poolDetailsId, Long poolId, String chargeName, Double price, String ipAddress, Long allocatedId, Date activationDate, Date expiryDate, String poolName,Integer chargeId) {
        this.currentPoolDetailsId = poolDetailsId;
        this.currentPoolId = poolId;
        this.chargeName = chargeName;
        this.price = price;
        this.ipAddress = ipAddress;
        this.currentAllocatedId = allocatedId;
        this.activationDate = activationDate;
        this.expiryDate = expiryDate;
        this.poolName = poolName;
        this.currentChargeId = chargeId;
    }
}
