package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.converter.FieldTypeConverter;
import com.adopt.apigw.model.common.FieldType;
import com.adopt.apigw.model.common.OTPManagement;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OTPProfileMessage {



//    private Long profileId;
//
//
//    private String profileName;
//
//    private Integer otpLength;
//
//
//    private Long otpValidityInMin;
//
//
//    private String generationType;
//
//
//    private List<FieldType> type;
//
//
//
//    private Integer mvnoId;
//
//    @CreationTimestamp
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
//    @DiffIgnore
//    private LocalDateTime createdate;
//
//    @UpdateTimestamp
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
//    private LocalDateTime updatedate;
//
//
//
//    private Integer createdById;
//
//    private Integer lastModifiedById;
//
//
//    private String staticOtp;
//
//
//    private String mvnoName;

    OTPManagement otpManagement = new OTPManagement();




}
