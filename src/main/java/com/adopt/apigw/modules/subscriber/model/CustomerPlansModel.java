package com.adopt.apigw.modules.subscriber.model;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.Customers.CustomerShowDTO;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventorySerialnumberDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Data
public class CustomerPlansModel {

    private Integer planId;
    private String planName;
    private Integer serviceId;
    private Integer planmapid;
    private Integer custPlanMapppingId;
    private String qosPolicyName;
    private Long qosPolicyId;

    private Integer custId;

    private Double validity;
    private String quotaType;

    private String volTotalQuota;
    private String volUsedQuota;
    private String volQuotaUnit;

    private String timeTotalQuota;
    private String timeUsedQuota;
    private String timeQuotaUnit;

    private CustomerShowDTO billablecust;

    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private Date startDate;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private Date endDate;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private Date expiryDate;

    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss")
    private Date startDateString;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss")
    private Date expiryDateString;

    private String plangroup;
    private Double maxsession;
    private String service;
    private String planstage;
    private Long childValidity;
    private Integer plangroupid;
    private String planGroupName;
    private Boolean istrialplan;
    private String custPlanStatus;
    private Boolean isinvoicestop;
    private String nickname;
    private String connection_no;
    private String createbyname;
    private LocalDateTime createdate;
    private boolean isdeleteforVoid;
    private String remarks;
    private Boolean isinvoicestopinpackrel;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss")
    private Date stopServiceDate;
    private String billTo;
    private Double discount;
    private String custPlanCategory;
    private String discountType;
    private Boolean is_dtv;
    private Boolean is_qosv;
    private String invoiceType;
    private Integer nextTeamHierarchyMappingId;
    private Integer nextStaff;
    private Integer customerServiceMappingId;
    private LocalDateTime serviceEndDate;
    private LocalDateTime serviceStartDate;
    private LocalDateTime serviceStopDate;
    private LocalDate discountExpiryDate;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private Date promiseToPayStartDate;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private Date promiseToPayEndDate;
    private Integer promiseToPayDays;
    private Boolean isPromiseToPayTaken;
    private Long promiseToPayCount;
    private LocalDateTime dbStartDate;
    private LocalDateTime dbEndDate;
    private LocalDateTime dbExpiryDate;
    private String custServMappingStatus;
    private Boolean isChildExists;
    private Boolean isHold;
    private Boolean isVoid;
    private Long remainingDays;
    private String extendValidityremarks;
    private String serviceHoldBy;
    private String serviceStartBy;
    private String serviceHoldRemarks;
    private String serviceStartRemarks;
    private LocalDateTime serviceHoldDate;
    private LocalDateTime serviceResumeDate;
    private String serviceResumeBy;
    private String serviceResumeRemarks;
    private Long debitdocid;
    private List<CustomerInventorySerialnumberDto> customerInventorySerialnumberDtos;
    private String promiseToPayRemarks;
    private String qosSpeed;
    private  Boolean isServiceThroughLead;

    private Double offerPrice;

    private Boolean isAllowOverUsage = false;

    private Double totalReserve = 0.0;

    private Boolean isChunkAvailable = false;

    private Integer renewalId;

    private Boolean isPromiseTopay;
    private Integer maxHoldAttempts;
    private Integer maxHoldDurationDays=0;
    private Integer remainingPauseDays=0;
    private Integer remainingHoldAttempts=0;
    private String discountFlowInProcess;
    private Boolean renewalForBooster;


}

