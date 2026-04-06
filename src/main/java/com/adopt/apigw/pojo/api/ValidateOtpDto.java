package com.adopt.apigw.pojo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Validate Otp",description = "This is data transfer object to validate otp")
public class ValidateOtpDto {
    @ApiModelProperty(notes = "This is mobile no",required = false)
    private String mobileNumber;
    @ApiModelProperty(notes = "This is generated otp",required = true)
    private String otp;
    @ApiModelProperty(notes = "This is mobile no",required = false)
    private String emailId;
}
