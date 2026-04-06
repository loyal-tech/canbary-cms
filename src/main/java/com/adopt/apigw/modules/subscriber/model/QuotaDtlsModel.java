package com.adopt.apigw.modules.subscriber.model;

import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

import lombok.Data;

@Data
public class QuotaDtlsModel extends UpdateAbstarctDTO {
    private Integer planId;
    private String operationDataQuota;
    private Integer planmapid;
    private Double valueDataQuota;
    private String unitDataQuota;
    private String operationTimeQuota;
    private Double valueTimeQuota;
    private String unitTimeQuota;
    private String remark;
    private Integer custId;

    public QuotaDtlsModel() {
    }

    public QuotaDtlsModel(String operationDataQuota, Integer planmapid, Double valueDataQuota, String unitDataQuota, String remark, Integer custId) {
        this.operationDataQuota = operationDataQuota;
        this.planmapid = planmapid;
        this.valueDataQuota = valueDataQuota;
        this.unitDataQuota = unitDataQuota;
        this.remark = remark;
        this.custId = custId;
    }
}
