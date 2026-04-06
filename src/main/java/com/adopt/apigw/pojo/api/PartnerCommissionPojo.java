package com.adopt.apigw.pojo.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PartnerCommissionPojo extends ParentPojo {
    private Integer id;
    private Integer customerid;
    private String customerName;
    private Integer partnerid;
    private String commtype;
    private Double commrelval = 0.0;
    private Double commval = 0.0;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss a")
    private LocalDateTime createdate;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private LocalDateTime billdate;
    private String status;
}
