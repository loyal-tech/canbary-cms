package com.adopt.apigw.modules.integrations.NexgeVoice.model;

import lombok.Data;

import java.util.List;

@Data
public class NexgeVoiceUpdateResDTO {
    private String id;
    private String accountType;
    private String billType;
    private String servicePlanId;
    private Double creditLimit;
    private List<DIDProfile> DIDProfile;
    private List<VoiceAccountDTO> accounts;
}
