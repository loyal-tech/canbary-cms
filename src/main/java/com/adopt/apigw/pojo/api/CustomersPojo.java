package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.CustIpMapping;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.postpaid.CustMacMapppingPojo;
import com.adopt.apigw.model.postpaid.CustomerLedgerPojo;
import com.adopt.apigw.modules.LocationMaster.domain.CustomerLocationMapping;
import com.adopt.apigw.modules.TumilIdValidation.IdValidationResponse;
import com.adopt.apigw.modules.TumilIdValidation.IdValidationResponsePojo;
import com.adopt.apigw.modules.customerDocDetails.model.CustomerDocDetailsDTO;
import com.adopt.apigw.modules.linkacceptance.model.LinkAcceptanceDTO;
import com.adopt.apigw.rabbitMq.message.CustomerQuotaInfo;
import com.fasterxml.jackson.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Data
public class CustomersPojo extends Auditable {

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

    private Integer graceDay;

    private Integer partnerid;

    private String onuid;

    private boolean isCustomerCreated;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextBillDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextQuotaResetDate;

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

    private Long popid;

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

    @JsonManagedReference
    private List<CustPlanMapppingPojo> planMappingList = new ArrayList<>();

    private List<LinkAcceptanceDTO> linkAcceptanceList = new ArrayList<>();
    @JsonManagedReference
    private List<CustomerAddressPojo> addressList = new ArrayList<>();

    private List<Integer> radiusprofileIds = new ArrayList<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<DebitDocumentPojo> debitDocList = new ArrayList<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<CreditDocumentPojo> creditDocuments = new ArrayList<>();

    @JsonManagedReference
    private List<CustChargeDetailsPojo> overChargeList = new ArrayList<>();

    @JsonManagedReference
    private List<CustomerDocDetailsDTO> custDocList = new ArrayList<>();

    @JsonManagedReference
    private List<CustChargeDetailsPojo> indiChargeList = new ArrayList<>();

    @JsonManagedReference
    private CustomerLedgerPojo custLeger;

    @JsonManagedReference
    private List<CustMacMapppingPojo> custMacMapppingList = new ArrayList<>();

    @JsonManagedReference
    private List<CustLedgerDtlsPojo> ledgerDtls = new ArrayList<>();

    private RecordPaymentPojo paymentDetails;

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
    private CustomersPojo parentCustomers;

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

    private  String activationByName;

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

    @Transient
    private PlanGroupDTO planGroupDTO;

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

    private String discountType="One-time";

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

    private List<CustomerLocationMappingDto> customerLocations;

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

    private List<CustIpMapping> custIpMappingList = new ArrayList<>();
    private List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>();

    private CustomerPaymentDto customerPaymentDto;

    private String referenceNo;

    private Integer earlybilldays;

    private Integer earlybillday;

    private LocalDate earlybilldate;
    private String framedIPNetmask;
    private String framedIPv6Prefix;
    private String  gatewayIP;
    private String  primaryDNS;
    private String  primaryIPv6DNS;
    private String  secondaryIPv6DNS;
    private String  secondaryDNS;
    private Boolean mac_provision;
    private Boolean mac_auth_enable;
    private String  macRetentionUnit;
    private Integer  macRetentionPeriod;
    private String delegatedprefix;
    private String framedroute;
    private LocalDate nearestMacRetentionDate;
    private LocalDate quotaResetDate;
    private String blockNo;

    private String drivingLicence;

    private String customerNid;

    private String customerVrn;

    private Integer renewPlanLimit;

    private Boolean isCredentialMatchWithAccountNo = false;

    private Boolean isPasswordAutoGenerated = false;

    private String loginUsername;

    private String loginPassword;

    private Integer departmentId;

    private String currency;
    private Boolean isCustomerFromProvisionPortal = false;

    private boolean billDayUpdated;

    private Integer previousBillday;


    private List<IdValidationResponsePojo> houseHoldIdList = new ArrayList<>();

    private String onuInterface;


    @Override
    public String toString() {
        return "CustomersPojo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", RegistrationDate='" + registrationDate + '\'' +
                ", planName='" + planName + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", failcount=" + failcount +
                ", acctno='" + acctno + '\'' +
                ", custType='" + custtype + '\'' +
                ", phone='" + phone + '\'' +
                ", billday=" + billday +
                ", graceDay=" + graceDay +
                ", partnerid=" + partnerid +
                ", nextBillDate=" + nextBillDate +
                ", lastBillDate=" + lastBillDate +
                ", addresstype='" + addresstype + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", city=" + city +
                ", state=" + state +
                ", country=" + country +
                ", pincode='" + pincode + '\'' +
                ", outstanding=" + outstanding +
                ", oldpassword1='" + oldpassword1 + '\'' +
                ", newpassword='" + newpassword + '\'' +
                ", oldpassword2='" + oldpassword2 + '\'' +
                ", oldpassword3='" + oldpassword3 + '\'' +
                ", last_password_change=" + last_password_change +
                ", planMappingPojoList=" + planMappingList +
                ", addressList=" + addressList +
                ", serviceareaName=" + serviceareaName +
                ", passportNo=" + passportNo +
                ", flashMsg='" + flashMsg + '\'' +
                ", discount='" + discount + '\'' +
                ", branch='" + branch + '\'' +
                ", valleyType='" + valleyType + '\'' +
                ", customerArea='" + customerArea + '\'' +
                ", customerType='" + customerType + '\'' +
                ", customerSubType='" + customerSubType + '\'' +
                ", customerSector='" + customerSector + '\'' +
                ", customerSubSector='" + customerSubSector + '\'' +
                ", popid='" + popid + '\'' +
                ", oltid='" + oltid + '\'' +
                ", masterdbid='" + masterdbid + '\'' +
                ", splitterid='" + splitterid + '\'' +
                ", leadId='" + leadId + '\'' +
                ", leadNo='" + leadNo + '\'' +
                ",  feasibilityRequired'"+ feasibilityRequired +'\''+
                ", feasibility'"+ feasibility +'\''+
                ",  feasibilityRemark'"+ feasibilityRemark +'\''+
                ",  hasChildCust'"+ hasChildCust +'\''+
                ", birthDate'"+ birthDate +'\''+
                ", houseHoldIdList='" + houseHoldIdList + '\'' +
                '}';
    }

}
