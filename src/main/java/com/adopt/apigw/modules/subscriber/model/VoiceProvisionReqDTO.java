package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

@Data
public class VoiceProvisionReqDTO {

    private Boolean voiceProvisionFlag = false;
    private String remark;
    private Integer custId;
    private String voiceProvision;
}
