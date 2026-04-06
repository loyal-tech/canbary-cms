package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.util.List;

@Data
public class ChangePlanDTO {
    private CurrentPlanDTO currentPlanDTO;
    private List<CustomPlanDto> customPlanDtoList;
}
