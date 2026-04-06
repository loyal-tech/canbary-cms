package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustPlanMappingMessage {
    private Integer id;
    private String custPlanStatus;
    private String status;
}
