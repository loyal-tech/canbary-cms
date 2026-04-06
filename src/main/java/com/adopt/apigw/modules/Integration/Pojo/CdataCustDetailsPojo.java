package com.adopt.apigw.modules.Integration.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CdataCustDetailsPojo {

    String customerName; //cdatapojo
    String custUserName; //cdatapojo
    String custPassword; //cdatapojo
    String configName = "ADOPT-CDATA";  // static
    Integer custServMappingId;  //cdatapojo
    String loggedInUser; //from token
    Integer loggedInUserMvnoId; //from token
    String serialNumber; //cdatapojo
    String manufacturer; //cdatapojo
    Integer customerId; //cdatapojo
    String model;
}
