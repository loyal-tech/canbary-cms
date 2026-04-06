package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.FieldType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "OTPManagementDto", description = "This is data transfer object for OTP Profile which is used to create new profile")
public class OTPManagementDto {


    @ApiModelProperty(notes = "This is OTP profile name", required = true)
    private String profileName;

    @ApiModelProperty(notes = "This is OTP length", required = true)
    private Integer otpLength;

    @ApiModelProperty(notes = "This is OTP validity in minute", required = true)
    private Long otpValidityInMin;

    @ApiModelProperty(notes = "This is generation type", required = true)
    private String generationType;

    @ApiModelProperty(notes = "Profile types", required = true)
    private List<FieldType> type;

    @ApiModelProperty(notes = "This is static OTP for all", required = false)
    private String staticOtp;



}
