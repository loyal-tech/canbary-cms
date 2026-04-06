package com.adopt.apigw.modules.reports.recentrenewal.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.sql.Date;

@Data
public class RecentRenewalReportResponseModel {
    private Long custid;
    private String acctnumber;
    private String username;
    private String name;
    private String status;
    private String address;
    private String area;
    private String city;
    private String state;
    private String email;
    private String paymentmethod;
    private String zip;
    private String mobile;
    private String planname;
    private Double planprice;
    private Double walletbalused;
    private String poolname;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private String paymentdate;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private String activationdate;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private String creationdate;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private String expirydate;
    private Double allotedtime;
    private Double allotedtotaldatatransfer;
    private Double usedtime;
    private Double useddatatransfer;
    private String partner;
    private String renewaltype;
    private String createdfrom;
    private String rechargeby;
    private Double paidamount;
    private String purchasefrom;
    private String pgtransid;
    private String transid;
    private String purchasestatus;
    private String paymentstatus;
    private String gst;
    private String pan;
}
