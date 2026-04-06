package com.adopt.apigw.modules.InventoryManagement.inventoryMapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NMSIntegrationResDto {
    private String apiMessage;
    private boolean apiFlag;
}
