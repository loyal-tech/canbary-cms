package com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationSpecificParamDTO {
    private String paramName;
    private String paramValue;
}
