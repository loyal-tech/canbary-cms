package com.adopt.apigw.modules.Customers;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LightCustomerPlanMappingDTO {

    private Integer custId;

    private String username;

    private String password;

    private String custtype;

    private Integer planId;

    private Integer serviceId;

    private String planName;

    private String planGroup;

    private Double offerPrice;

    private String mobileNumber;

    private Integer custServiceMappingId;

    private Integer custPlanMappingId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime expiryDate;


    private Double usedDataQuota;

    private Double totalDataQuota;

    private Double usedTimeQuota;

    private Double totalTimeQuota;

    private String quotaType;

    private String dataQuotaUnit;

    private String timeQuotaUnit;

    private Boolean isBoosterAvailable;

    private List<Object> BoosterList;


    public LightCustomerPlanMappingDTO(Integer custId, String username, String password,String custtype, Integer planId, Integer serviceId, String planName, String planGroup, Double offerPrice, String mobileNumber, Integer custServiceMappingId, Integer custPlanMappingId, LocalDateTime startDate , LocalDateTime endDate , LocalDateTime expiryDate, String quotaType, Double usedDataQuota , Double totalDataQuota , Double usedTimeQuota , Double totalTimeQuota , String dataQuotaUnit , String timeQuotaUnit, Boolean isBoosterAvailable) {
        this.custId = custId;
        this.username = username;
        this.password = password;
        this.custtype = custtype;
        this.planId = planId;
        this.serviceId = serviceId;
        this.planName = planName;
        this.planGroup = planGroup;
        this.offerPrice = offerPrice;
        this.mobileNumber = mobileNumber;
        this.custServiceMappingId = custServiceMappingId;
        this.custPlanMappingId = custPlanMappingId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expiryDate = expiryDate;
        this.quotaType = quotaType;
        this.usedDataQuota = usedDataQuota;
        this.totalDataQuota = totalDataQuota;
        this.usedTimeQuota = usedTimeQuota;
        this.totalTimeQuota = totalTimeQuota;
        this.dataQuotaUnit= dataQuotaUnit;
        this.timeQuotaUnit = timeQuotaUnit;
        this.isBoosterAvailable = isBoosterAvailable;
    }
}
