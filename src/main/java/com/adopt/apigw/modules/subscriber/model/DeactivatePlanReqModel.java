package com.adopt.apigw.modules.subscriber.model;


import com.adopt.apigw.soap.Dto.Override;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DeactivatePlanReqModel {


    private Integer planId;

    private Integer newPlanId;

    private Integer oldCustPlanMappingId;

    private Integer planGroupId;

    private Integer newPlanGroupId;

    private Integer cprId;

    private Double discount = 0d;

    private Integer custServiceMappingId;

    private boolean isBillToOrg = false;

    private Double newAmount;

    private String creditDocumentId;

    private String isFromFlutterWave;

    private int reasonId;
    private String remarks;

    List<Integer> debitDocIds;

    private boolean skipQuotaUpdate;

    private double updatedUsedQuota;

    List<Override> updatedUsedQuotaList;

}
