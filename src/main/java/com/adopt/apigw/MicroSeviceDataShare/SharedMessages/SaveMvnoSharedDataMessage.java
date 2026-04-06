package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.modules.custAccountProfile.CustAccountProfile;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Column;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveMvnoSharedDataMessage {
    private Long id;
    private String name;
    private String username;
    private String password;
    private String suffix;
    private String description;
    private String email;
    private String address;
    private String fullName;
    private String phone;
    private String status;
    private String logfile;
    private String mvnoHeader;
    private String mvnoFooter;
    private Boolean isDelete = false;
    private Integer createdById;
    private Integer lastModifiedById;
    private byte[] profileImage;
    private String logo_file_name;
    private Integer mvnoPaymentDueDays;
    private Integer ispBillDay;
    private String billType;
    private Double ispCommissionPercentage;
    private Long profileId;
    private Long threshold;

}
