package com.adopt.apigw.modules.reports.recentrenewal.model;

import lombok.Data;

import java.sql.Date;

@Data
public class ReportRequestModel {

    //commonParam
    private Long custid;
    private Date startDate;
    private Date endDate;
    private Integer page;
    private Integer pageSize;
    //Use For RecentRenewal
    private String purchaseType;
    private String purchaseFrom;
    private String paymentStatus;
    private String purchaseStatus;
    private Double price1;
    private Double price2;
    private String priceCondition;
    //Use For ChargeReport
    private Integer chargeid;
    private String chargetype;
    private String chargecategory;
    private String chargereversal;
}
