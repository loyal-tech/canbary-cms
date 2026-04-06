package com.adopt.apigw.pojo.api;

import com.adopt.apigw.modules.subscriber.model.ChangePlanRequestDTO;
import com.adopt.apigw.modules.subscriber.model.DateOverrideDto;
import com.adopt.apigw.modules.subscriber.model.RecordPaymentRequestDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChangePlanRequestDTOList {
    private List<ChangePlanRequestDTO> changePlanRequestDTOList;
    private List<CustChargeOverrideDTO> custChargeDetailsList;
    private RecordPaymentPojo recordPayment;
    private DateOverrideDto dateOverrideDtos;
    private Boolean isTriggerCoaDm;
    private Boolean skipQuotaUpdate;
    private Boolean renewalForBooster;
    private Integer childId;
}
