package com.adopt.apigw.pojo.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;

@Data
public class CustomersMigrationPojo {

    private Integer id;

    private String username;

    private String password;

    private String firstname;

    private String lastname;

    private String email;

    private String title;

    private String custname;

    private String contactperson;

    private String pan;

    private String gst;

    private String aadhar;

    private String status;

    private Integer failcount = 0;

    private String acctno;

    private String custtype;

    private String phone;

    private Integer billday;

    private Integer partnerid;

    private String onuid;

    private boolean isCustomerCreated;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextBillDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastBillDate;

    private String addresstype;

    private String address1;

    private String address2;

    private Integer city;

    private Integer state;

    private Integer country;

    private Integer pincode;

    private Integer area;

//    private String pincode;

    private Double outstanding;

    private String oldpassword1;

    private String newpassword;

    private String oldpassword2;

    private String oldpassword3;

    private String selfcarepwd;

    private long popid;

    private Long oltid;
    private String oltName;

    private Long masterdbid;
    private String masterdbName;
    private Long splitterid;
    private String splitterName;
    @CreationTimestamp
    private LocalDateTime last_password_change;

    private String lastpasswordchangestring;

    private String framedIpBind;

    private String ipPoolNameBind;

    private String flashMsg;

    private Boolean mactelflag = false;

    private Boolean isinvoicestop = false;

    private Boolean istrialplan = false;

    private String mobile;

    // added country code
    private String countryCode;

    private String cafno;

    private String ipv4;

    private String ipv6;

    private String vlan;

    private String altmobile;

    private String altphone;

    private String altemail;

    private String fax;

    private Integer resellerid;

    private Integer salesrepid;

    private String voicesrvtype;

    private Boolean voiceprovision = false;

    private String didno;

    private String childdidno;

    private String intercomno;

    private String intercomgrp;

    private Boolean onlinerenewalflag = false;

    private Boolean voipenableflag = false;

    private Boolean isorgcust = false;

    private String custcategory;

    private Double walletbalance = 0.0;

    private String networktype;

    private Long defaultpoolid;

    private Long serviceareaid;

    private Long networkdevicesid;

    private Long oltslotid;

    private Long oltportid;

    private String strconntype;

    private String stroltname;

    private String strslotname;

    private String strportname;

    @JsonIgnore
    private CustomersMigrationPojo parentCustomers;

    private String OldBNGRouterinterface;

    private String OldVSIName;

    private String ASNNumber;

    private String BNGRouterinterface;

    private String BNGRoutername;

    private String IPPrefixes;

    private String IPV6Prefixes;

    private String LANIP;

    private String LANIPV6;

    private String LLAccountid;

    private String LLConnectiontype;

    private String LLExpirydate;

    private String LLMedium;

    private String LLServiceid;

    private String MACADDRESS;

    private String Peerip;

    private String POOLIP;

    private String QOS;

    private String RDExport;

    private String RDValue;

//    private String VLANID;

    private String vlan_id;

    private String VRFName;

    private String VSIID;

    private String VSIName;

    private String WANIP;

    private String WANIPV6;

    private String billentityname;

    private String addparam1;

    private String addparam2;

    private String addparam3;

    private String addparam4;

    private String purchaseorder;

    private String remarks;

    private String allowedIPAddress;

    private String OldWANIP;

    private String OldLLAccountid;

    private LocalDateTime firstActivationDate;

    private Boolean isDeleted = false;

    private String createDateString;
    private String updateDateString;

    private String latitude;
    private String longitude;
    private String url;
    private String gis_code;
    private String salesremark;
    private String servicetype;

    private String isCustCaf;

    private Integer nextTeamHierarchyMapping;
    private String serviceareaName;
    private String cafApproveStatus;

    private Integer mvnoId;

    private String tinNo;

    private String passportNo;

    private String dunningCategory;

    private Integer plangroupid;
//
//    @Transient
//    private PlanGroupDTO planGroupDTO;

    private Integer parentCustomerId;

    private String parentCustomerName;

    private String invoiceType;

    private String calendarType;

    private double discount = 0d;

    private Long buId;

    private Integer custPackageId;

    private Long partnerLedgerMappingId;

    private String planPurchaseType;

    private String leadSource;
    private String feasibilityRequired;

    private Long branch;

    private String branchName;

    private String regionName;

    private String buVerticals;

    private String valleyType;

    private String customerArea;

    private String customerType;

    private String customerSubType;

    private String customerSector;

    private String customerSubSector;

    private Integer lcoId;

    private Boolean is_from_pwc;
    
    private Long leadId;
    
    private String leadNo;

    HashSet<Integer> oldDebitDocId;

    private String nasPort;

    private String framedIp;

    private Double flatAmount;

    private String ezyBillCustomersId;

    private String ezyBillAccountNumber;


    private String creditDocumentId;

    private String isFromFlutterWave;

    private String paymentOwner;

    private String ezyBillStockId;

    private String feasibility;

    private String feasibilityRemark;
    private String custlabel;
    private Long staffId;
    private String dunningSubSector;
    private String dunningSubType;
    private String dunningType;
    private String dunningSector;
    private String registrationDate;
    private String planName;

    private Integer billableCustomerId;

    private Integer currentAssigneeId;

    private Long rejectReasonId;

    private Long rejectSubReasonId;

    private String rejectReasonName;

    private String rejectSubReasonName;
    private String businessType;

    private String discountType;

    private LocalDate discountExpiryDate;


    private Integer paymentOwnerId;
    private String additionalemail;
    private String salesrepresentative;
    private String skypeid_imid;
    private String organisation;
    private String rating;
    private String automaticnotification;
    private String locationlevel1;
    private String locationlevel2;
    private String locationlevel3;
    private String locationlevel4;
    private String ponumber;
    private String customerbillingid;
    private String businessunit;
    private String subbusinessunit;

    private Boolean isDunningActivate;

    private String dunningActivateFor;

    private LocalDateTime lastDunningDate;

    private Boolean isDunningEnable;

    private String dunningAction;

    private Boolean isNotificationEnable;

    private String parentExperience;

    private LocalDateTime lastStatusChangeDate;

    private String popName;

    private String department;

    private Boolean hasChildCust = false;

    private String subscriptionMode;

    private String validFrom;

    private String validUpto;

    private List<Long> locations;

    private String voucherCode;

    private String cid;

    private LocalDateTime birthDate;

//    private List<CustomerLocationMappingDto> customerLocations;

    private String parentQuotaType;
    private Integer slaTime;
    private String slaUnit;
    private LocalDate nextfollowupdate;
    private LocalTime nextfollowuptime;
    private Integer refMvno;
    private String nasPortId;
    private String nasIpAddress;
    private String framedIpv6Address;
    private Integer maxconcurrentsession;
    private LocalDate discountExpDate;
    private String billableTo;
    private String invoiceToOrganization;
    private String billTo;
    private String planGroupName;
    private String planCategory;
    private String iPPoolNameBind;
    private String nASPortValidate;
    private String nASIP;
    private String StaticIP;
    private String SplitterDB;
    private String MasterDB;
    private String OLT;
    private String POP;

    private String Landmark;
    private String Ward;
    private String Municipality;
    private String Address;
    private String Partner;
    private String PartnerBranch;
    private String ServiceArea;
    private String SalesMark;
    private String ParentCustomer;
    private String DedicatedStaffUserName;
    private String DOB;
    private String CAFNumber;
    private String CustomerSectorType;
    private String CDCustomerSubType;
    private String CDCustomerType;
    private String CustomerCategory;
    private String Telephone;
    private String SecondaryMobile;
    private String PrimaryMobile;
    private String referenceNo;
    private String  DiscountPercentage;
    private String  NewPriceWithDiscount;
    private String ServiceName;
    private List<String> plannameList;
    private String dExpiryDate;
    private String AreaName;
    private Integer earlybillday;
}
