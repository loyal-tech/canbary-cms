package com.adopt.apigw.model.common;

import com.adopt.apigw.converter.FieldTypeConverter;
import com.adopt.apigw.pojo.api.OTPManagementDto;
import com.adopt.apigw.pojo.api.UpdateOTPManagementDto;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLMOTPMANAGEMENT")
//@EntityListeners(AuditableListener.class)
public class OTPManagement{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @Column(name = "profile_name")
    private String profileName;

    @Column(name = "otp_length")
    private Integer otpLength;

    @Column(name = "otp_validity")
    private Long otpValidityInMin;

    @Column(name = "generation_type")
    private String generationType;

    @Column(name = "type")
    @Convert(converter = FieldTypeConverter.class)
    private List<FieldType> type;


    @DiffIgnore
    @ApiModelProperty(notes = "This is mvnoid",required=true)
    @Column (name="mvnoid", nullable = true, updatable = false)
    private Integer mvnoId;

    @CreationTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    @DiffIgnore
    private LocalDateTime createdate;

    @UpdateTimestamp
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "LASTMODIFIEDDATE")
    @DiffIgnore
    private LocalDateTime updatedate;


    @Column(name = "CREATEDBYSTAFFID", nullable = false, length = 40, updatable = false)
    @DiffIgnore
    private Integer createdById;

    @Column(name = "LASTMODIFIEDBYSTAFFID", nullable = false, length = 40)
    @DiffIgnore
    private Integer lastModifiedById;

    @ApiModelProperty(notes = "This is static OTP for all", required = false)
    @Column(name = "static_otp",nullable = true)
    private String staticOtp;

    @Transient
    private String mvnoName;




    public OTPManagement(OTPManagementDto otpManagementDto)
    {
        this.profileName = otpManagementDto.getProfileName();
        this.otpLength = otpManagementDto.getOtpLength();
        this.otpValidityInMin = otpManagementDto.getOtpValidityInMin();
        this.generationType = otpManagementDto.getGenerationType();
        this.type =  otpManagementDto.getType();
        if(otpManagementDto.getStaticOtp() != null) {
            this.staticOtp = otpManagementDto.getStaticOtp();
        }

        /* setCreateDate(LocalDateTime.now());
        setCreatedBy(otpManagementDto.getCreatedBy());*/
    }

    public OTPManagement(UpdateOTPManagementDto updateOTPManagementDto) {
        this.profileId=updateOTPManagementDto.getProfileId();
        this.otpLength = updateOTPManagementDto.getOtpLength();
        this.otpValidityInMin = updateOTPManagementDto.getOtpValidityInMin();
        this.generationType = updateOTPManagementDto.getGenerationType();
        this.type =  updateOTPManagementDto.getType();
        if(updateOTPManagementDto.getStaticOtp() != null) {
            this.staticOtp = updateOTPManagementDto.getStaticOtp();
        }
       /*  setLastModifiedDate(LocalDateTime.now());
        setLastModifiedBy(updateOTPManagementDto.getLastModifiedBy());*/
    }


    public OTPManagement(OTPManagement otpManagement){
        this(otpManagement.profileId,otpManagement.profileName,otpManagement.otpLength,otpManagement.otpValidityInMin,otpManagement.generationType,otpManagement.type,otpManagement.mvnoId,otpManagement.createdate,otpManagement.updatedate,otpManagement.createdById,otpManagement.lastModifiedById,otpManagement.staticOtp,otpManagement.mvnoName);
    }
}
