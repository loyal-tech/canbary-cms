package com.adopt.apigw.modules.integrations.NexgeVoice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DIDProfile {
    @JsonProperty("DIDNumber")
    private String DIDNumber;
    private NumberBlockSIPDTO numberBlock;
}
