package com.adopt.apigw.pojo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Generate Otp",description = "This is data transfer object to generate otp")
public class GenerateOtpDto {

    @ApiModelProperty(notes = "This is country code",required = false)
    private String countryCode;
    @ApiModelProperty(notes = "This is mobile no",required = false)
    private String mobileNumber;
    @ApiModelProperty(notes = "This is profile name",required = true)
    private String profile;
    @ApiModelProperty(notes = "This is mobile no",required = false)
    private String emailId;

}
