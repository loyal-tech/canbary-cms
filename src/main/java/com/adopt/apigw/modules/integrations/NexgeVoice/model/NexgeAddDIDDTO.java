package com.adopt.apigw.modules.integrations.NexgeVoice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NexgeAddDIDDTO {

    @JsonProperty("DIDProfile")
    List<DIDProfile> DIDProfile = new ArrayList<>();
}
