package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.pojo.api.CustPlanMapppingDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OrganizationInvoiceRejectMesssage {
    List<CustPlanMapppingDto> custPlanMapppingDtos;
}
