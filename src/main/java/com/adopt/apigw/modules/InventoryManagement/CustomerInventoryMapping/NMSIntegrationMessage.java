package com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NMSIntegrationMessage {
    private List<IntegrationSpecificParamDTO> list = new ArrayList<>();
    private String configName;
    private Integer loggedInUserId;
    private Long mvnoId;
    private String operation;
    private Long custInvenId;
    private Long itemId;
    private Long customerId;
    private String serialNumber;
}

