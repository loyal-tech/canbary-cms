package com.adopt.apigw.pojo.api;

import lombok.Data;

@Data
public class CustNetworkDetailsDTO {

    private Long networkdeviceid;
    private Long serviceareaid;
    private Long slotid;
    private Long portid;
    private String networkdevicename ;
    private String serviceareaname;
    private String slotname;
    private String portname;

}
