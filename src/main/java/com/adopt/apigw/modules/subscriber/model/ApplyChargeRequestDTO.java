package com.adopt.apigw.modules.subscriber.model;

import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ApplyChargeRequestDTO extends UpdateAbstarctDTO {
    private Integer charge_id;
    private String charge_name;
    private LocalDate charge_date;
    private String remarks;
    private Integer custId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startdate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate enddate;
    private Double taxamount;
    private Integer custChargeId;
    private Boolean isUnlimited;
}
