package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustInvParamsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class UuidDataDTO {

    Integer customerServiceMappingId;
    String uuid;
    String configName;
    String userName;
    List<CustInvParamsDto> custInvParamsDtoList = new ArrayList<>();
    String cdataUuid;
    String cdataTemplate;
}
