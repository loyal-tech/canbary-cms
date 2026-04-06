package com.adopt.apigw.modules.PartnerLedger.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PartnerLedgerAllInfoPojo {
    private Integer partnerId;
    private String username;
    private String partnername;
    private String plan;
    private String address;
    private String zonename;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate lastrenewDate;
    private Long duration;
    private String status;
    private PartnerLedgerInfoPojo partnerLedgerInfoPojo;
}
