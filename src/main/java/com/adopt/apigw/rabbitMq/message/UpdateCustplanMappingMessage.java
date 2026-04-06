package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.pojo.api.CustPlanMapppingDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCustplanMappingMessage {

    List<CustPlanMapppingDto> custPlanMapppingDtos;
}
