package com.adopt.apigw.modules.reports.recentrenewal.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.sql.Date;

@Data
public class ChargeReportResponseModel {

    private String chargename;
    private String category;
    private String chargetype;
    private Double chargeprice;
    private Integer custchargeid;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private String chargedate;
    private String poolname;
    private String ip;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private String startdate;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private String enddate;
    private String reversed;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private String revdate;
    private Double reversedamount;
    private Double validity;
    private String createbyname;
    private String username;
    private String mobile;
    private String name;
    private String email;
    private String status;
    private String acctnumber;
    private String gst;
    private String pan;
}
