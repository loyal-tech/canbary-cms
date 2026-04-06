package com.adopt.apigw.modules.subscriber.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DeactivatePlanReqDTO {

    private Integer custId;

    private boolean isPlanGroupChange;

    private boolean isPlanGroupFullyChanged;

    private List<DeactivatePlanReqModel> deactivatePlanReqModels;

    private String paymentOwner;

    private Integer billableCustomerId=null;

    private Integer paymentOwnerId;

    private Boolean isParent;

    List<Integer> debitDocIds;

    private RecordPaymentRequestDTO recordPaymentDTO;

    private String changePlanDate;

    private boolean skipQuotaUpdate;

}
