package com.adopt.apigw.modules.integrations.NexgeVoice.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NexgeVoiceResponseDTO {
    private String id;
    private String authUserId;
    private String webDomain;
    private String webAuthPassword;
    private String parent;
    private String status;
    private String firstName;
    private String middleName;
    private String lastName;
    private String country;
    private String state;
    private String city;
    private String district;
    private String taluk;
    private String pincode;
    private String postoffice;
    private String localArea;
    private List<VoiceAccountDTO> accounts;
    private String emailId;
    private String alternatePhoneNumber;
    private String sipDomain;
    private String sipAuthPassword;
}
