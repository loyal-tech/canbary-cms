package com.adopt.apigw.utils;

import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustInvParamsDto;
import lombok.Data;

import java.util.List;

@Data
public class NMSServiceActivationDTO {
    List<ProductParameterDefaultValueMappingDTO> parameters;
    List<CustInvParamsDto> custInvParams;
    Integer custId;
    Integer custServiceMapId;
    String configName;
    String upstreamprofileuuid;
    String downstreamprofileuuid;
    Integer customerServiceMappingId;
    String username;
    Integer mvnoId;
}
