package com.adopt.apigw.model.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;
@Data
@Entity
@Table(name = "TBLTOTP")
public class OTP {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Integer id;

    @Column(name = "otp" , nullable = false)
    private String otp;

    @Column(name = "mobile_email")
    private String mobile_email;

    @Column(name ="generated_time")
    private ZonedDateTime generatedTime;

    @Column(name = "valid_till_time")
    private ZonedDateTime validTillTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OTPStatus otpStatus;

    @Column(name = "countrycode")
    private String countryCode;

    @ApiModelProperty(notes = "This is mvnoid",required=true)
    @Column (name="mvnoid", nullable = false)
    private Long mvnoId;

    public OTP(String otp, String mobileNumber, ZonedDateTime generatedTime, ZonedDateTime validTillTime, OTPStatus otpStatus, String countryCode)
    {
        super();
        this.otp = otp;
        this.mobile_email = mobileNumber;
        this.generatedTime = generatedTime;
        this.validTillTime = validTillTime;
        this.otpStatus = otpStatus;
        this.countryCode = countryCode;
    }

    public OTP() {
        super();
    }

}
