package com.adopt.apigw.model.common;

import com.adopt.apigw.modules.LocationMaster.domain.CustomerLocationMapping;
import com.adopt.apigw.modules.cafRejectReason.Entity.RejectReason;
import com.adopt.apigw.modules.cafRejectReason.Entity.RejectSubReason;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.model.radius.RadiusProfile;
import com.adopt.apigw.modules.NetworkDevices.domain.NetworkDevices;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.customerDocDetails.domain.CustomerDocDetails;
import com.adopt.apigw.modules.linkacceptance.domain.LinkAcceptance;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.context.annotation.Primary;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.integration.annotation.Gateway;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "tblcustomers")
@EntityListeners(AuditableListener.class)
public class Customers extends Auditable {

    @DiffIgnore
    @Column(name = "is_notification_enable")
    public Boolean isNotificationEnable;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custid", nullable = false, length = 40)
    private Integer id;
    //@Column(nullable = false, length = 40)
    private String title;
    @Column(nullable = false, length = 40)
    private String username;
    @Column(length = 40)
    private String password;
    @Column(nullable = false, length = 40)
    private String firstname;
    @Column(nullable = false, length = 40)
    private String lastname;
    @Column(name = "custname", nullable = false, length = 40)
    private String custname;
    @Column(name = "ipv4")
    private String ipv4;
    @Column(name = "ipv6")
    private String ipv6;
    @Column(name = "vlan")
    private String vlan;
    @ManyToOne(targetEntity = PlanGroup.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "plangroupid", referencedColumnName = "plangroupid")
    @DiffIgnore
    private PlanGroup plangroup;
    @Column(nullable = false, length = 40)
    private String contactperson;
    @DiffIgnore
    @Column(length = 40)
    private String cafno;
    @DiffIgnore
    @Column(length = 25)
    private String pan;
    @DiffIgnore
    @Column(length = 25)
    private String gst;
    @DiffIgnore
    @Column(length = 25)
    private String aadhar;
    @Column(nullable = false, unique = true)
    private String email;
    @DiffIgnore
    @Column(columnDefinition = "Boolean default false")
    private Boolean mactelflag = false;
    @DiffIgnore
    @Column(nullable = false)
    private String mobile;
    @DiffIgnore
    private String countryCode;
    @Column
    @DiffIgnore
    private String altmobile;
    @DiffIgnore
    @Column(length = 50)
    private String altphone;
    @DiffIgnore
    @Column(length = 100)
    private String altemail;
    @DiffIgnore
    @Column(length = 100)
    private String fax;
    @DiffIgnore
    private Integer resellerid;
    @ManyToOne
    @DiffIgnore
    @JoinColumn(name = "salesrepid")
    private StaffUser salesrep;
    @DiffIgnore
    @Column(nullable = false, length = 75)
    private String voicesrvtype;
    @Column
    @DiffIgnore
    private Boolean voiceprovision = false;
    @DiffIgnore
    @Column(nullable = false, length = 75)
    private String didno;
    @DiffIgnore
    @Column(nullable = false, length = 75)
    private String childdidno;
    @DiffIgnore
    private String intercomno;
    @DiffIgnore
    @Column(nullable = false, length = 75)
    private String intercomgrp;
    @DiffIgnore
    @Column(columnDefinition = "Boolean default false")
    private Boolean onlinerenewalflag = false;
    @DiffIgnore
    private Boolean voipenableflag = false;
    @DiffIgnore
    private Boolean isorgcust = false;
    @DiffIgnore
    @Column(columnDefinition = "Boolean default false", name = "isinvoicestop")
    private Boolean isinvoicestop = false;
    @Column(columnDefinition = "Boolean default false", name = "istrialplan")
    private Boolean istrialplan = false;
    @Column(nullable = false, length = 75)
    private String custcategory;
    private Double walletbalance = 0.0;
    @DiffIgnore
    @Column(length = 50)
    private String networktype;
    @DiffIgnore
    private Long defaultpoolid;
    @ManyToOne
    @JoinColumn(name = "servicearea_id")
    @DiffIgnore
    private ServiceArea servicearea;
    @ManyToOne
    @JoinColumn(name = "network_device_id")
    @DiffIgnore
    private NetworkDevices networkdevices;
    @DiffIgnore
    private Long oltslotid;
    @DiffIgnore
    private Long oltportid;
    @DiffIgnore
    @Column(length = 75)
    private String strconntype;
    @DiffIgnore
    @Column(length = 75)
    private String stroltname;
    @DiffIgnore
    @Column(length = 75)
    private String strslotname;
    @DiffIgnore
    @Column(length = 75)
    private String strportname;
    @Column(name = "cstatus", nullable = false, length = 100)
    private String status;
    @Column(name = "invoiceoption", nullable = false, length = 100)
    private String invoiceOption;
    @DiffIgnore
    @Column(name = "failcount", nullable = false, length = 100)
    private Integer failcount;
    @DiffIgnore
    @UpdateTimestamp
    @Column(name = "last_password_change", nullable = false, updatable = true)
    private LocalDateTime last_password_change;
    @DiffIgnore
    @Column(name = "accountnumber", length = 100)
    private String acctno;
    @Column(name = "customertype", nullable = false, length = 100)
    private String custtype; //Postpaid,Prepaid
    @Column(name = "phone", nullable = false, length = 100)
    private String phone;
    @UpdateTimestamp
    @DiffIgnore
    @Column(name = "laststatuschangedate", nullable = false, length = 100)
    private LocalDateTime lastStatusChangeDate;
    @Column(name = "BILLDAY")
    private Integer billday;

    @Column(name = "grace_day")
    private Integer graceDay= 0;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "partnerid")
    @DiffIgnore
    private Partner partner;
    @DiffIgnore
    @OneToMany(targetEntity = CustomerPayment.class, cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "custid", referencedColumnName = "custid")
    private List<CustomerPayment> customerPayments;
    @Column(name = "NEXTBILLDATE", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextBillDate;
    @Column(name = "LASTBILLDATE", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastBillDate;
    @ManyToMany

    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tblradiusprocustrel", joinColumns = {@JoinColumn(name = "custid")}, inverseJoinColumns = {@JoinColumn(name = "radiusprofileid")})
    @DiffIgnore
    private List<RadiusProfile> radiusProfiles = new ArrayList<>();
    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "customer", orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private List<CustPlanMappping> planMappingList = new ArrayList<>();
    @DiffIgnore
    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "customer")
    @OrderBy("id")
    @EqualsAndHashCode.Exclude
    private List<CustomerAddress> addressList = new ArrayList<>();
    @DiffIgnore
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @OrderBy("id desc")
    @Where(clause = "planid is not null")
    private List<DebitDocument> debitDocList = new ArrayList<>();
    @DiffIgnore
    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("id desc")
    private List<CreditDocument> creditDocuments = new ArrayList<>();
    @DiffIgnore
    @JsonManagedReference
    @OneToOne(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private CustomerLedger custLeger;
    @DiffIgnore
    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", cascade = CascadeType.ALL)
    @OrderBy("id desc")
    private List<CustomerLedgerDtls> ledgerDtls = new ArrayList<>();
    @DiffIgnore
    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.MERGE)
    @Where(clause = "chargetype != 'CUSTOMER_DIRECT'") //Chargetype=1 Plan Overrider Charge, 1=Manual Charge
    @OrderBy("id desc")
    private List<CustChargeDetails> overChargeList = new ArrayList<>();
    @DiffIgnore
    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", orphanRemoval = true, cascade = CascadeType.ALL)
    @Where(clause = "chargetype = 'CUSTOMER_DIRECT'") //Chargetype=1 Plan Overrider Charge, 1=Manual Charge
    @OrderBy("id desc")
    private List<CustChargeDetails> indiChargeList = new ArrayList<>();
    @DiffIgnore
    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer", cascade = CascadeType.ALL)
    @OrderBy("id desc")
    @EqualsAndHashCode.Exclude
    private List<CustMacMappping> custMacMapppingList = new ArrayList<>();
    @DiffIgnore
    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @OrderBy("docId desc")
    private List<CustomerDocDetails> custDocList = new ArrayList<>();
    @JsonIgnore
    @DiffIgnore
    @ManyToOne
    @JoinColumn(name = "parentcustid")
    private Customers parentCustomers;
    @Column(name = "invoice_type")
    private String invoiceType;
    @DiffIgnore
    @Column(name = "onuid")
    private String onuid;
    @DiffIgnore
    @Column(name = "last_login_time")
    private LocalDateTime last_login_time;
    @Transient
    private String addresstype;
    @Transient
    @DiffIgnore
    private String address1;
    @Transient
    @DiffIgnore
    private String address2;
    @Transient
    private Integer city;
    @Transient
    private Integer state;
    @Transient
    private Integer country;
    @Transient
    private Integer pincode;

//    @Transient
//    private String pincode;
    @Transient
    private Integer area;
    @Transient
    @DiffIgnore
    private String command;
    @DiffIgnore
    @Column(name = "outstandingbalance", nullable = false, updatable = false)
    private Double outstanding;
    @Transient
    private String newpassword;
    @Transient
    @DiffIgnore
    private String OldBNGRouterinterface;
    @Transient
    @DiffIgnore
    private String OldVSIName;
    @DiffIgnore
    @Column(name = "ASNNumber", columnDefinition = "text")
    private String ASNNumber;
    @DiffIgnore
    @Column(name = "BNGRouterinterface", columnDefinition = "text")
    private String BNGRouterinterface;
    @DiffIgnore
    @Column(name = "BNGRoutername", columnDefinition = "text")
    private String BNGRoutername;
    @DiffIgnore
    @Column(name = "IPPrefixes", columnDefinition = "text")
    private String IPPrefixes;
    @DiffIgnore
    @Column(name = "IPV6Prefixes", columnDefinition = "text")
    private String IPV6Prefixes;
    @DiffIgnore
    @Column(name = "LANIP", columnDefinition = "text")
    private String LANIP;
    @DiffIgnore
    @Column(name = "LANIPV6", columnDefinition = "text")
    private String LANIPV6;
    @DiffIgnore
    @Column(name = "LLAccountid", columnDefinition = "text")
    private String LLAccountid;
    @DiffIgnore
    @Column(name = "LLConnectiontype", columnDefinition = "text")
    private String LLConnectiontype;
    @DiffIgnore
    @Column(name = "LLExpirydate", columnDefinition = "text")
    private String LLExpirydate;
    @DiffIgnore
    @Column(name = "LLMedium", columnDefinition = "text")
    private String LLMedium;
    @DiffIgnore
    @Column(name = "LLServiceid", columnDefinition = "text")
    private String LLServiceid;
    @Column(name = "MACADDRESS", columnDefinition = "text")
    private String MACADDRESS;
    @DiffIgnore
    @Column(name = "Peerip", columnDefinition = "text")
    private String Peerip;
    @DiffIgnore
    @Column(name = "POOLIP", columnDefinition = "text")
    private String POOLIP;
    @Column(name = "QOS", columnDefinition = "text")
    private String QOS;
    @DiffIgnore
    @Column(name = "RDExport", columnDefinition = "text")
    private String RDExport;
    @DiffIgnore
    @Column(name = "RDValue", columnDefinition = "text")
    private String RDValue;
    @DiffIgnore
    @Column(name = "VLANID", columnDefinition = "text")
    private String vlan_id;
    @DiffIgnore
    @Column(name = "VRFName", columnDefinition = "text")
    private String VRFName;
    @DiffIgnore
    @Column(name = "VSIID", columnDefinition = "text")
    private String VSIID;
    @DiffIgnore
    @Column(name = "VSIName", columnDefinition = "text")
    private String VSIName;
    @DiffIgnore
    @Column(name = "WANIP", columnDefinition = "text")
    private String WANIP;
    @DiffIgnore
    @Column(name = "WANIPV6", columnDefinition = "text")
    private String WANIPV6;
    @DiffIgnore
    @Column(name = "billentityname ", length = 200)
    private String billentityname;
    @DiffIgnore
    @Column(name = "addparam1", columnDefinition = "text")
    private String addparam1;
    @DiffIgnore
    @Column(name = "addparam2", columnDefinition = "text")
    private String addparam2;
    @DiffIgnore
    @Column(name = "addparam3", columnDefinition = "text")
    private String addparam3;
    @DiffIgnore
    @Column(name = "addparam4", columnDefinition = "text")
    private String addparam4;
    @DiffIgnore
    @Column(name = "purchaseorder", length = 200)
    private String purchaseorder;
    @Column(name = "remarks", columnDefinition = "text")
    private String remarks;
    @DiffIgnore
    @Column(nullable = false, length = 40)
    private String oldpassword1;
    @DiffIgnore
    @Column(nullable = false, length = 40)
    private String oldpassword2;
    @DiffIgnore
    @Column(nullable = false, length = 40)
    private String oldpassword3;
    @DiffIgnore
    private String selfcarepwd;
    @DiffIgnore
    @Column(name = "allowedipaddrs", length = 100)
    private String allowedIPAddress;
    @Transient
    @DiffIgnore
    private Integer parentCustomersId;
    @Transient
    @DiffIgnore
    private String OldWANIP;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    @Transient
    @DiffIgnore
    private String OldLLAccountid;
    @DiffIgnore
    @Column(name = "firstactivationdate")
    private LocalDateTime firstActivationDate;
    @DiffIgnore
    @Column(name = "activationbyname")
    private String activationByName;
    private String otp;
    private LocalDateTime otpvalidate;
    private String latitude;
    private String longitude;
    @DiffIgnore
    private String url;
    @DiffIgnore
    private String gis_code;
    @DiffIgnore
    private String salesremark;
    @DiffIgnore
    private String servicetype;
    @Column(name = "next_team_hir_mapping")
    private Integer nextTeamHierarchyMapping;
    @Column(name = "caf_approve_status")
    private String cafApproveStatus;
    @Transient
    @DiffIgnore
    private Integer billRunCustPackageRelId;
    @Transient
    @DiffIgnore
    private String ConnectionMode;
    @DiffIgnore
    @Column(name = "passport_no", length = 25)
    private String passportNo;
    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;
    @Column(name = "dunning_category", nullable = false, length = 40)
    private String dunningCategory;
    @Transient
    private String fullName;
    private transient String partnerName;
    private transient String serviceAreName;
    @Column(name = "tin_no")
    @DiffIgnore
    private String tinNo;
    @Column(name = "calendartype", nullable = false, length = 100, columnDefinition = "varchar(100) default 'English'")
    private String calendarType;
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    @DiffIgnore
    private Long buId;
    @DiffIgnore
    @Column(name = "plan_purchase_type")
    private String planPurchaseType;
    @Column(name = "lead_source")
    @DiffIgnore
    private String leadSource;
    @DiffIgnore
    @Column(name = "feasibility_required")
    private String feasibilityRequired;
    @DiffIgnore
    @Column(name = "branchid")
    private Long branch;
    @DiffIgnore
    @Column(name = "VALLEY_TYPE")
    private String valleyType;
    @DiffIgnore
    @Column(name = "CUSTOMER_AREA")
    private String customerArea;
    @DiffIgnore
    @Column(name = "CUSTOMER_TYPE")
    private String customerType;
    @DiffIgnore
    @Column(name = "CUSTOMER_SUB_TYPE")
    private String customerSubType;
    @DiffIgnore
    @Column(name = "CUSTOMER_SECTOR")
    private String customerSector;
    @DiffIgnore
    @Column(name = "CUSTOMER_SUB_SECTOR")
    private String customerSubSector;
    @DiffIgnore
    @Column(name = "lco_id")
    private Integer lcoId;
    @DiffIgnore
    @Column(name = "is_from_pwc")
    private Boolean is_from_pwc;
    @DiffIgnore
    @Column(name = "popid")
    private Long popid;
    @DiffIgnore
    @Column(name = "oltid")
    private Long oltid;
    @DiffIgnore
    @Column(name = "masterdbid")
    private Long masterdbid;
    @DiffIgnore
    @Column(name = "splitterid")
    private Long splitterid;
    @DiffIgnore
    @Column(name = "lead_id")
    private Long leadId;
    @DiffIgnore
    @Column(name = "lead_no")
    private String leadNo;
    @DiffIgnore
    @Column(name = "nas_port")
    private String nasPort;
    @DiffIgnore
    @Column(name = "framed_ip")
    private String framedIp;
    @Column(name = "dunning_sub_sector", length = 40)
    private String dunningSubSector;
    @Column(name = "dunning_sub_type", length = 40)
    private String dunningSubType;
    @Column(name = "dunning_type", length = 40)
    private String dunningType;
    @DiffIgnore
    @Column(name = "dunning_sector", length = 40)
    private String dunningSector;
    @DiffIgnore
    @Column(name = "ezybill_customers_id")
    private String ezyBillCustomersId;
    @DiffIgnore
    @Column(name = "ezybill_account_number", length = 100)
    private String ezyBillAccountNumber;
    @DiffIgnore
    @Column(name = "feasibility")
    private String feasibility;
    @DiffIgnore
    @Column(name = "feasibility_remark")
    private String feasibilityRemark;
    @DiffIgnore
    @Column(name = "customerlabel")
    private String custlabel;
    @Column(name = "staffid")
    @DiffIgnore
    private Long staffId;
    @DiffIgnore
    @Column(name = "framed_ip_bind")
    private String framedIpBind;
    @DiffIgnore
    @Column(name = "ip_pool_name_bind")
    private String ipPoolNameBind;
    @Transient
    @DiffIgnore
    private String registrationDate;
    @Transient
    private String planName;
    @DiffIgnore
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = LinkAcceptance.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(referencedColumnName = "custid", name = "custid")
    private List<LinkAcceptance> linkAcceptanceList = new ArrayList<>();
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = CustomerServiceMapping.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(referencedColumnName = "custid", name = "custid")
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CustomerServiceMapping> customerServiceMappingList = new ArrayList<>();
    @Column(name = "current_assignee_id")
    @DiffIgnore
    private Integer currentAssigneeId;
    @DiffIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reject_reason_id")
    private RejectReason rejectReason;
    @DiffIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reject_sub_reason_id")
    private RejectSubReason rejectSubReason;
    @Column(name = "reject_caf_time")
    private LocalDateTime rejectCafTime;
    @Column(name = "business_type")
    private String businessType;
    @DiffIgnore
    @Column(name = "additionalemail")
    private String additionalemail;
    @DiffIgnore
    @Column(name = "salesrepresentative")
    private String salesrepresentative;
    @DiffIgnore
    @Column(name = "skypeid_imid")
    private String skypeid_imid;
    @DiffIgnore
    @Column(name = "organisation")
    private String organisation;
    @DiffIgnore
    @Column(name = "rating")
    private String rating;
    @DiffIgnore
    @Column(name = "automaticnotification")
    private String automaticnotification;
    @DiffIgnore
    @Column(name = "locationlevel1")
    private String locationlevel1;
    @DiffIgnore
    @Column(name = "locationlevel2")
    private String locationlevel2;
    @DiffIgnore
    @Column(name = "locationlevel3")
    private String locationlevel3;
    @DiffIgnore
    @Column(name = "locationlevel4")
    private String locationlevel4;
    //    @DiffIgnore
    @Column(name = "ponumber")
    private String ponumber;
    @DiffIgnore
    @Column(name = "customerbillingid")
    private String customerbillingid;
    @DiffIgnore
    @Column(name = "businessunit")
    private String businessunit;
    @DiffIgnore
    @Column(name = "subbusinessunit")
    private String subbusinessunit;
    @DiffIgnore
    @Column(name = "is_dunning_activate")
    private Boolean isDunningActivate;
    @DiffIgnore
    @Column(name = "dunning_activate_for")
    private String dunningActivateFor;
    @DiffIgnore
    @Column(name = "last_dunning_date")
    private LocalDateTime lastDunningDate;
    @DiffIgnore
    @Column(name = "billable_customer_id")
    private Integer billableCustomerId;
    @Column(name = "is_dunning_enable")
    private Boolean isDunningEnable;
    @Column(name = "dunning_action")
    private String dunningAction;
    @DiffIgnore
    @Column(name = "parent_experience")
    private String parentExperience;

    @DiffIgnore
    @Column(name = "department")
    private String department;

    @DiffIgnore
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = CustomerLocationMapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "custid", referencedColumnName = "custid")
    private List<CustomerLocationMapping> customerLocations;

    @DiffIgnore
    @Column(name = "maxconcurrentsession")
    private Integer maxconcurrentsession;

    @DiffIgnore
    @Column(name = "birthdate")
    private LocalDateTime birthDate;
    @DiffIgnore
    @Column(name = "sla_time")
    private Integer slaTime;

    @DiffIgnore
    @Column(name = "sla_unit")
    private String slaUnit;

    @DiffIgnore
    @Column(name = "nextfollowupdate")
    private LocalDate nextfollowupdate;
    @DiffIgnore
    @Column(name = "nextfollowuptime")
    private LocalTime nextfollowuptime;
    @Transient
    private Integer refMvno;

    @Column(name = "mvno_deactivation_flag")
    private Boolean mvnoDeactivationFlag;

    @OneToMany(targetEntity = CustIpMapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "custid", referencedColumnName = "custid")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<CustIpMapping> custIpMappingList;

    @Column(name = "nas_port_id")
    private String nasPortId;

    @Column(name = "nas_ip_address")
    private String nasIpAddress;

    @Column(name = "framed_ipv6_address")
    private String framedIpv6Address;

    @Column(name = "earlybilldays")
    private Integer earlybilldays;

    @Column(name = "earlybilldate")
    private LocalDate earlybilldate;

    @Column(name = "earlybillday")
    private Integer earlybillday;
    @Column(name = "framed_ip_netmask")
    private String framedIPNetmask;

    @Column(name = "framed_ipv6_prefix")
    private String framedIPv6Prefix;

    @Column(name = "gateway_ip")
    private String gatewayIP;
    @Column(name = "primary_dns")
    private String primaryDNS;
    @Column(name = "primary_ipv6_dns")
    private String primaryIPv6DNS;
    @Column(name = "secondary_ipv6_dns")
    private String secondaryIPv6DNS;
    @Column(name = "secondary_dns")
    private String secondaryDNS;
    @Column(name = "mac_provision")
    private Boolean mac_provision;

    @Column(name = "mac_auth_enable")
    private Boolean mac_auth_enable;
    @Column(name = "macretentionperiod")
    private Integer macRetentionPeriod;
    @Column(name = "macretentionunit")
    private String macRetentionUnit;
    @Column(name = "delegatedprefix")
    private String delegatedprefix;
    @Column(name = "framedroute")
    private String framedroute;
    @Column(name = "blockno")
    private String blockNo;

    @Column(name = "driving_licence")
    private String drivingLicence;

    @Column(name = "customer_nid")
    private String customerNid;

    @Column(name = "customer_vrn")
    private String customerVrn;


    @Column(name = "nextquotaresetdate", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextQuotaResetDate;

    @Column(name = "renew_plan_limit")
    private Integer renewPlanLimit;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name ="currency")
    private String currency;

    @Column(name ="bill_day_updated")
    private boolean billDayUpdated;

    @Column(name = "previous_billday")
    private Integer previousBillday;


    @Column(name ="is_password_auto_generated")
    private Boolean isPasswordAutoGenerated;

//    @LazyCollection(LazyCollectionOption.FALSE)
//    @OneToMany(targetEntity = CustomerChargeHistory.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//        @JoinColumn(referencedColumnName = "custid", name = "custid")
//    private List<CustomerChargeHistory> customerChargeHistories = new ArrayList<>();
    @Transient
    private Boolean isCustomerFromProvisionPortal = false;

    @Column(name ="onu_interface")
    private String onuInterface;

    public Customers() {
    }

    public Customers(Customers customers) {
        this.id = customers.getId();
        this.title = customers.getTitle();
        this.username = customers.getUsername();
//        this.password = customers.;
        this.firstname = customers.getFirstname();
        this.lastname = customers.getLastname();
        this.custname = customers.getCustname();
        this.plangroup = customers.getPlangroup();
        this.contactperson = customers.getContactperson();
        this.cafno = customers.getCafno();
        this.pan = customers.getPan();
        this.gst = customers.getGst();
        this.aadhar = customers.getAadhar();
        this.email = customers.getEmail();
        //      this.mactelflag = mactelflag;
        this.mobile = customers.getMobile();
        this.countryCode = customers.getCountryCode();
        this.altmobile = customers.getAltmobile();
        this.altphone = customers.getAltphone();
        this.altemail = customers.getAltemail();
//      //  this.fax = fax;
//        this.resellerid = resellerid;
//        this.salesrep = salesrep;
//        this.voicesrvtype = voicesrvtype;
//        this.voiceprovision = voiceprovision;
//        this.didno = didno;
//        this.childdidno = childdidno;
//        this.intercomno = intercomno;
//        this.intercomgrp = intercomgrp;
//        this.onlinerenewalflag = onlinerenewalflag;
//        this.voipenableflag = voipenableflag;
        this.isorgcust = customers.getIsorgcust();
        this.isinvoicestop = customers.getIsinvoicestop();
        this.istrialplan = customers.getIstrialplan();
        this.custcategory = customers.getCustcategory();
        this.walletbalance = customers.getWalletbalance();
//        this.networktype = networktype;
//        this.defaultpoolid = defaultpoolid;
//        this.servicearea = servicearea;
//        this.networkdevices = networkdevices;
//        this.oltslotid = oltslotid;
//        this.oltportid = oltportid;
//        this.strconntype = strconntype;
//        this.stroltname = stroltname;
//        this.strslotname = strslotname;
//        this.strportname = strportname;
        this.status = customers.getStatus();
        this.invoiceOption = customers.getInvoiceOption();
//        this.failcount = failcount;
//        this.last_password_change = last_password_change;
//        this.acctno = acctno;
        this.custtype = customers.getCusttype();
        this.phone = customers.getPhone();
        this.lastStatusChangeDate = customers.getLastStatusChangeDate();
        this.billday = customers.getBillday();
        this.graceDay = customers.getGraceDay();
        this.partner = customers.getPartner();
        this.customerPayments = customers.getCustomerPayments();
        this.nextBillDate = customers.getNextBillDate();
        this.lastBillDate = customers.getLastBillDate();
        this.radiusProfiles = customers.getRadiusProfiles();
        this.planMappingList = customers.getPlanMappingList();
        this.addressList = customers.getAddressList();
        this.debitDocList = customers.getDebitDocList();
        this.creditDocuments = customers.getCreditDocuments();
        this.custLeger = customers.getCustLeger();
        this.ledgerDtls = customers.getLedgerDtls();
        this.overChargeList = customers.getOverChargeList();
        this.indiChargeList = customers.getIndiChargeList();
        this.custMacMapppingList = customers.getCustMacMapppingList();
        this.custDocList = customers.getCustDocList();
        this.parentCustomers = customers.getParentCustomers();
        this.invoiceType = customers.getInvoiceType();
        this.onuid = customers.getOnuid();
        this.addresstype = customers.getAddresstype();
        this.address1 = customers.getAddress1();
        this.address2 = customers.getAddress2();
        this.city = customers.getCity();
        this.state = customers.getState();
        this.country = customers.getCountry();
        this.pincode = customers.getPincode();
        this.area = customers.getArea();
        this.command = customers.getCommand();
        this.outstanding = customers.getOutstanding();
        //  this.newpassword = customers;
//        this.OldBNGRouterinterface = customers;
//        this.OldVSIName = customers;
//        this.ASNNumber = customers;
//        this.BNGRouterinterface = customers;
//        this.BNGRoutername = customers;
//        this.IPPrefixes = customers;
//        this.IPV6Prefixes = customers;
//        this.LANIP = customers;
//        this.LANIPV6 = customers;
//        this.LLAccountid = customers.getLLAccountid();
//        this.LLConnectiontype = customers.getLLConnectiontype();
//        this.LLExpirydate = customers.getLLExpirydate();
//        this.LLMedium = customers.getLLMedium();
//        this.LLServiceid = customers.getLLServiceid();
//        this.MACADDRESS = customers.getMACADDRESS();
//        this.Peerip = customers.getPeerip();
//        this.POOLIP = customers.getPOOLIP();
        this.QOS = customers.getQOS();
//        this.RDExport = RDExport;
//        this.RDValue = RDValue;
//        this.VLANID = VLANID;
//        this.VRFName = VRFName;
//        this.VSIID = VSIID;
//        this.VSIName = VSIName;
//        this.WANIP = WANIP;
//        this.WANIPV6 = WANIPV6;
        this.billentityname = customers.getBillentityname();
        this.addparam1 = customers.getAddparam1();
        this.addparam2 = customers.getAddparam2();
        this.addparam3 = customers.getAddparam3();
        this.addparam4 = customers.getAddparam4();
        this.purchaseorder = customers.getPurchaseorder();
        this.remarks = customers.getRemarks();
//        this.oldpassword1 = oldpassword1;
//        this.oldpassword2 = oldpassword2;
//        this.oldpassword3 = oldpassword3;
//        this.selfcarepwd = selfcarepwd;
        this.allowedIPAddress = customers.getAllowedIPAddress();
        this.parentCustomersId = customers.getParentCustomersId();
        this.OldWANIP = customers.getOldWANIP();
        this.isDeleted = customers.getIsDeleted();
//        OldLLAccountid = oldLLAccountid;
//        this.firstActivationDate = firstActivationDate;
//        this.otp = otp;
//        this.otpvalidate = otpvalidate;
//        this.latitude = latitude;
//        this.longitude = longitude;
//        this.url = url;
//        this.gis_code = gis_code;
//        this.salesremark = salesremark;
        this.servicetype = customers.getServicetype();
        this.nextTeamHierarchyMapping = customers.getNextTeamHierarchyMapping();
        this.cafApproveStatus = customers.getCafApproveStatus();
        this.billRunCustPackageRelId = customers.getBillRunCustPackageRelId();
        this.ConnectionMode = customers.getConnectionMode();
        //     this.passportNo = passportNo;
        this.mvnoId = customers.getMvnoId();
        this.dunningCategory = customers.getDunningCategory();
        this.fullName = customers.getFullName();
        this.partnerName = customers.getPartnerName();
        this.serviceAreName = customers.getServiceAreName();
        this.tinNo = customers.getTinNo();
        this.calendarType = customers.getCalendarType();
        this.buId = customers.getBuId();
        this.planPurchaseType = customers.getPlanPurchaseType();
        this.leadSource = customers.getLeadSource();
        this.feasibilityRequired = customers.getFeasibilityRequired();
        this.branch = customers.getBranch();
        this.valleyType = customers.getValleyType();
        this.customerArea = customers.getCustomerArea();
        this.customerType = customers.getCustomerType();
        this.customerSubType = customers.getCustomerSubType();
        this.customerSector = customers.getCustomerSector();
        this.customerSubSector = customers.getCustomerSubSector();
        this.lcoId = customers.getLcoId();
//        this.is_from_pwc = is_from_pwc;
//        this.popid = popid;
//        this.oltid = customers.getOltid();
        this.masterdbid = customers.getMasterdbid();
        this.splitterid = customers.getSplitterid();
        this.leadId = customers.getLeadId();
        this.leadNo = customers.getLeadNo();
//        this.nasPort = nasPort;
//        this.framedIp = framedIp;
//        this.dunningSubSector = dunningSubSector;
//        this.dunningSubType = dunningSubType;
//        this.dunningType = dunningType;
//        this.dunningSector = dunningSector;
//        this.ezyBillCustomersId = ezyBillCustomersId;
//        this.ezyBillAccountNumber = ezyBillAccountNumber;
        this.feasibility = customers.getFeasibility();
        this.feasibilityRemark = customers.getFeasibilityRemark();
        this.custlabel = customers.getCustlabel();
        this.staffId = customers.getStaffId();
//        this.framedIpBind = framedIpBind;
//        this.ipPoolNameBind = ipPoolNameBind;
        this.registrationDate = customers.getRegistrationDate();
        this.planName = customers.getPlanName();
        this.linkAcceptanceList = customers.getLinkAcceptanceList();
        this.customerServiceMappingList = customers.getCustomerServiceMappingList();
        this.currentAssigneeId = customers.getCurrentAssigneeId();
        this.rejectReason = customers.getRejectReason();
        this.rejectSubReason = customers.getRejectSubReason();
        this.rejectCafTime = customers.getRejectCafTime();
        this.businessType = customers.getBusinessType();
        this.additionalemail = customers.getAdditionalemail();
        this.salesrepresentative = customers.getSalesrepresentative();
        this.skypeid_imid = customers.getSkypeid_imid();
        this.organisation = customers.getOrganisation();
        this.rating = customers.getRating();
        this.automaticnotification = customers.getAutomaticnotification();
        this.locationlevel1 = customers.getLocationlevel1();
        this.locationlevel2 = customers.getLocationlevel2();
        this.locationlevel3 = customers.getLocationlevel3();
        this.locationlevel4 = customers.getLocationlevel4();
        this.ponumber = customers.getPonumber();
        this.customerbillingid = customers.getCustomerbillingid();
        this.businessunit = customers.getBusinessunit();
        this.subbusinessunit = customers.getSubbusinessunit();
        this.isDunningActivate = customers.getIsDunningActivate();
        this.dunningActivateFor = customers.getDunningActivateFor();
        this.lastDunningDate = customers.getLastDunningDate();
        this.billableCustomerId = customers.getBillableCustomerId();
        this.isDunningEnable = customers.getIsDunningEnable();
        this.dunningAction = customers.getDunningAction();
        this.isNotificationEnable = customers.getIsNotificationEnable();
        this.parentExperience = customers.getParentExperience();
        this.department = customers.getDepartment();
        this.blockNo = customers.getBlockNo();
    }

    /// /        this.servicearea = customers.getServicearea();
//        this.LLConnectiontype = customers.getLLConnectiontype();
//        this.passportNo = customers.getPassportNo();
//        this.calendarType = customers.getCalendarType();
//        this.branch = customers.getBranch();
//        //this.billday = customers.getBillday();
//        this.valleyType = customers.getValleyType();
//        this.customerArea = customers.getCustomerArea();
//        this.popid = customers.getPopid();
//        this.staffId = customers.getStaffId();
//        this.department = customers.getDepartment();
//    }
    public Customers(Customers customers, Integer id) {
        this.id = customers.id;
        this.voicesrvtype = customers.getVoicesrvtype();
        this.didno = customers.getDidno();
        this.childdidno = customers.getChilddidno();
        this.intercomgrp = customers.getIntercomgrp();
        this.intercomno = customers.getIntercomno();
        this.status = customers.getStatus();
        this.mobile = customers.getMobile();
        this.altmobile = customers.getAltmobile();
        this.email = customers.getEmail();
        this.altemail = customers.getAltemail();
        this.phone = customers.getPhone();
        this.altphone = customers.getAltphone();
        this.fax = customers.getFax();
        this.title = customers.getTitle();
        this.firstname = customers.getFirstname();
        this.aadhar = customers.getAadhar();
        this.contactperson = customers.getContactperson();
        this.gst = customers.getGst();
        this.pan = customers.getPan();
        this.networktype = customers.getNetworktype();
        this.defaultpoolid = customers.getDefaultpoolid();
        this.oltportid = customers.getOltportid();
        this.oltslotid = customers.getOltslotid();
        this.onuid = customers.getOnuid();
//    this.servicearea = customers.getServiceareaid();
        this.LLConnectiontype = customers.getLLConnectiontype();
        this.passportNo = customers.getPassportNo();
        this.calendarType = customers.getCalendarType();
        //this.billday = customers.getBillday();
        this.mvnoId = customers.getMvnoId();
        this.branch = customers.getBranch();
        this.valleyType = customers.getValleyType();
        this.customerArea = customers.getCustomerArea();
        this.popid = customers.getPopid();
        this.staffId = customers.getStaffId();
        this.leadId = customers.getLeadId();
        this.leadNo = customers.getLeadNo();
        this.birthDate = customers.getBirthDate();
    }

    public Customers(String firstname, String username, String mobile, String email, String acctno, String customerType, String cafno, String partnerName, String serviceArea) {

        this.mobile = mobile;
        this.email = email;
        this.firstname = firstname;
        this.username = username;
        this.acctno = acctno;
        this.custtype = customerType;
        this.cafno = cafno;
        this.partnerName = partnerName;
        this.serviceAreName = serviceArea;
        //this.billday = customers.getBillday();
    }

    public Customers(Integer id) {
        this.id = id;
    }

    public Customers(Integer id, String mobile, String email, String countryCode, String username, Integer mvnoId, String status, String custtype, Double walletbalance, Long buId) {
        this.id = id;
        this.mobile = mobile;
        this.email = email;
        this.countryCode = countryCode;
        this.username = username;
        this.mvnoId = mvnoId;
        this.status = status;
        this.custtype = custtype;
        this.walletbalance = walletbalance;
        this.buId = buId;
    }

    public Customers(Integer id, String username, String firstname, String lastname, String countryCode, String mobile, String acctno, Integer mvnoId) {
        this.mvnoId = mvnoId;
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.countryCode = countryCode;
        this.mobile = mobile;
        this.acctno = acctno;
    }

    public Customers(Integer id, Integer mvnoId, Long buId, LocalDate nextBillDate, Double walletbalance, LocalDate nextQuotaResetDate, String custtype) {
        this.id = id;
        this.mvnoId = mvnoId;
        this.buId = buId;
        this.nextBillDate = nextBillDate;
        this.walletbalance = walletbalance;
        this.nextQuotaResetDate = nextQuotaResetDate;
        this.custtype = custtype;
    }

    public Customers(Integer id, Integer mvnoId, Long buId, Integer nextTeamHierarchyMapping) {
        this.id = id;
        this.mvnoId = mvnoId;
        this.buId = buId;
        this.nextTeamHierarchyMapping = nextTeamHierarchyMapping;
    }

    public Boolean getIs_from_pwc() {
        return is_from_pwc;
    }

    public void setIs_from_pwc(Boolean is_from_pwc) {
        this.is_from_pwc = is_from_pwc;
    }

    public Integer getLcoId() {
        return lcoId;
    }

    public void setLcoId(Integer lcoId) {
        this.lcoId = lcoId;
    }

    public Long getBranch() {
        return branch;
    }

    public void setBranch(Long branch) {
        this.branch = branch;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSelfcarepwd() {
        return selfcarepwd;
    }

    public void setSelfcarepwd(String selfcarepwd) {
        this.selfcarepwd = selfcarepwd;
    }

    public String getAllowedIPAddress() {
        return allowedIPAddress;
    }

    public void setAllowedIPAddress(String allowedIPAddress) {
        this.allowedIPAddress = allowedIPAddress;
    }

    public LocalDateTime getFirstActivationDate() {
        return firstActivationDate;
    }

    public void setFirstActivationDate(LocalDateTime firstActivationDate) {
        this.firstActivationDate = firstActivationDate;
    }

    public String getActivationByName() {
        return activationByName;
    }

    public void setActivationByName(String activationByName) {
        this.activationByName = activationByName;
    }

    public Integer getParentCustomersId() {
        return parentCustomersId;
    }

    public void setParentCustomersId(Integer parentCustomersId) {
        this.parentCustomersId = parentCustomersId;
    }

    public Customers getParentCustomers() {
        if (parentCustomers == null) {
            return null;
        } else {
            return parentCustomers;
        }
    }

    public void setParentCustomers(Customers parentCustomers) {
        this.parentCustomers = parentCustomers;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedate() {
        return super.getCreatedate();
    }

    public void setCreatedate(LocalDateTime createdate) {
        super.setCreatedate(createdate);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedate() {
        return super.getUpdatedate();
    }

    public void setUpdatedate(LocalDateTime updatedate) {
        super.setUpdatedate(updatedate);
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public List<RadiusProfile> getRadiusProfiles() {
        return radiusProfiles;
    }

    public void setRadiusProfiles(List<RadiusProfile> radiusProfiles) {
        this.radiusProfiles = radiusProfiles;
    }

    public LocalDateTime getLast_password_change() {
        return last_password_change;
    }

    public void setLast_password_change(LocalDateTime last_password_change) {
        this.last_password_change = last_password_change;
    }

    public Integer getFailcount() {
        return failcount;
    }

    public void setFailcount(Integer failcount) {
        this.failcount = failcount;
    }

    public String getAcctno() {
        return acctno;
    }

    public void setAcctno(String acctno) {
        this.acctno = acctno;
    }

    public String getCusttype() {
        return custtype;
    }

    public void setCusttype(String custtype) {
        this.custtype = custtype;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getBillday() {
        return null != billday ? billday : null;
    }

    public void setBillday(Integer billday) {
        this.billday = billday;
    }

    public LocalDate getNextBillDate() {
        return nextBillDate;
    }

    public void setNextBillDate(LocalDate nextBillDate) {
        this.nextBillDate = nextBillDate;
    }

    public LocalDate getLastBillDate() {
        return lastBillDate;
    }

    public void setLastBillDate(LocalDate lastBillDate) {
        this.lastBillDate = lastBillDate;
    }

    public List<CustPlanMappping> getPlanMappingList() {
        return planMappingList;
    }

    public void setPlanMappingList(List<CustPlanMappping> planMappingList) {
        this.planMappingList = planMappingList;
    }

    public List<CustomerAddress> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<CustomerAddress> addressList) {
        this.addressList = addressList;
    }

    @JsonIgnore
    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public LocalDateTime getLastStatusChangeDate() {
        return lastStatusChangeDate;
    }

    public void setLastStatusChangeDate(LocalDateTime lastStatusChangeDate) {
        this.lastStatusChangeDate = lastStatusChangeDate;
    }

    public List<DebitDocument> getDebitDocList() {
        return debitDocList;
    }

    public void setDebitDocList(List<DebitDocument> debitDocList) {
        this.debitDocList = debitDocList;
    }

    public String getAddresstype() {
        return addresstype;
    }

    public void setAddresstype(String addresstype) {
        this.addresstype = addresstype;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getCountry() {
        return country;
    }

    public void setCountry(Integer country) {
        this.country = country;
    }

    public Integer getPincode() {
        return pincode;
    }

    public void setPincode(Integer pincode) {
        this.pincode = pincode;
    }

    public Double getOutstanding() {
        if (this.custLeger != null) return this.custLeger.getTotalpaid() - this.custLeger.getTotaldue();
        else return 0.0;
    }

    public void setOutstanding(Double outstanding) {
        this.outstanding = outstanding;
    }

    public String getASNNumber() {
        return ASNNumber;
    }

    public void setASNNumber(String aSNNumber) {
        ASNNumber = aSNNumber;
    }

    public String getBNGRouterinterface() {
        return BNGRouterinterface;
    }

    public void setBNGRouterinterface(String bNGRouterinterface) {
        BNGRouterinterface = bNGRouterinterface;
    }

    public String getBNGRoutername() {
        return BNGRoutername;
    }

    public void setBNGRoutername(String bNGRoutername) {
        BNGRoutername = bNGRoutername;
    }

    public String getIPPrefixes() {
        return IPPrefixes;
    }

    public void setIPPrefixes(String iPPrefixes) {
        IPPrefixes = iPPrefixes;
    }

    public String getIPV6Prefixes() {
        return IPV6Prefixes;
    }

    public void setIPV6Prefixes(String iPV6Prefixes) {
        IPV6Prefixes = iPV6Prefixes;
    }

    public String getLANIP() {
        return LANIP;
    }

    public void setLANIP(String lANIP) {
        LANIP = lANIP;
    }

    public String getLANIPV6() {
        return LANIPV6;
    }

    public void setLANIPV6(String lANIPV6) {
        LANIPV6 = lANIPV6;
    }

    public String getLLAccountid() {
        return LLAccountid;
    }

    public void setLLAccountid(String lLAccountid) {
        LLAccountid = lLAccountid;
    }

    public String getLLConnectiontype() {
        return LLConnectiontype;
    }

    public void setLLConnectiontype(String lLConnectiontype) {
        LLConnectiontype = lLConnectiontype;
    }

    public String getLLExpirydate() {
        return LLExpirydate;
    }

    public void setLLExpirydate(String lLExpirydate) {
        LLExpirydate = lLExpirydate;
    }

    public String getLLMedium() {
        return LLMedium;
    }

    public void setLLMedium(String lLMedium) {
        LLMedium = lLMedium;
    }

    public String getLLServiceid() {
        return LLServiceid;
    }

    public void setLLServiceid(String lLServiceid) {
        LLServiceid = lLServiceid;
    }

    public String getMACADDRESS() {
        return MACADDRESS;
    }

    public void setMACADDRESS(String mACADDRESS) {
        MACADDRESS = mACADDRESS;
    }

    public String getPeerip() {
        return Peerip;
    }

    public void setPeerip(String peerip) {
        Peerip = peerip;
    }

    public String getPOOLIP() {
        return POOLIP;
    }

    public void setPOOLIP(String pOOLIP) {
        POOLIP = pOOLIP;
    }

    public String getQOS() {
        return QOS;
    }

    public void setQOS(String qOS) {
        QOS = qOS;
    }

    public String getRDExport() {
        return RDExport;
    }

    public void setRDExport(String rDExport) {
        RDExport = rDExport;
    }

    public String getRDValue() {
        return RDValue;
    }

    public void setRDValue(String rDValue) {
        RDValue = rDValue;
    }

    public String getVLANID() {
        return vlan_id;
    }

    public void setVLANID(String vlan_id) {
        vlan_id = vlan_id;
    }

    public String getVRFName() {
        return VRFName;
    }

    public void setVRFName(String vRFName) {
        VRFName = vRFName;
    }

    public String getVSIID() {
        return VSIID;
    }

    public void setVSIID(String vSIID) {
        VSIID = vSIID;
    }

    public String getVSIName() {
        return VSIName;
    }

    public void setVSIName(String vSIName) {
        VSIName = vSIName;
    }

    public String getWANIP() {
        return WANIP;
    }

    public void setWANIP(String wANIP) {
        WANIP = wANIP;
    }

    public String getWANIPV6() {
        return WANIPV6;
    }

    public void setWANIPV6(String wANIPV6) {
        WANIPV6 = wANIPV6;
    }

    public Integer getLastInvoiceId() {
        if (debitDocList != null && debitDocList.size() > 0) {
            return debitDocList.get(0).getId();
        } else {
            return -1;
        }
    }

    public CustomerLedger getCustLeger() {
        return custLeger;
    }

    public void setCustLeger(CustomerLedger custLeger) {
        this.custLeger = custLeger;
    }

    public Double getOutStandingAmount() {
        if (this.custLeger != null) return this.custLeger.getTotaldue() - this.custLeger.getTotalpaid();
        else return 0.0;
    }

    public String getBillentityname() {
        return billentityname;
    }

    public void setBillentityname(String billentityname) {
        this.billentityname = billentityname;
    }

    public String getPurchaseorder() {
        return purchaseorder;
    }

    public void setPurchaseorder(String purchaseorder) {
        this.purchaseorder = purchaseorder;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<CustChargeDetails> getOverChargeList() {
        return overChargeList;
    }

    public void setOverChargeList(List<CustChargeDetails> overChargeList) {
        this.overChargeList = overChargeList;
    }

    public List<CustChargeDetails> getIndiChargeList() {
        return indiChargeList;
    }

    public void setIndiChargeList(List<CustChargeDetails> indiChargeList) {
        this.indiChargeList = indiChargeList;
    }

    public String getAddparam1() {
        return addparam1;
    }

    public void setAddparam1(String addparam1) {
        this.addparam1 = addparam1;
    }

    public String getAddparam2() {
        return addparam2;
    }

    public void setAddparam2(String addparam2) {
        this.addparam2 = addparam2;
    }

    public String getAddparam3() {
        return addparam3;
    }

    public void setAddparam3(String addparam3) {
        this.addparam3 = addparam3;
    }

    public String getAddparam4() {
        return addparam4;
    }

    public void setAddparam4(String addparam4) {
        this.addparam4 = addparam4;
    }

    public String getInvoiceOption() {
        return invoiceOption;
    }

    public void setInvoiceOption(String invoiceOption) {
        this.invoiceOption = invoiceOption;
    }

    public String getOldBNGRouterinterface() {
        return OldBNGRouterinterface;
    }

    public void setOldBNGRouterinterface(String oldBNGRouterinterface) {
        OldBNGRouterinterface = oldBNGRouterinterface;
    }

    public String getOldpassword1() {
        return oldpassword1;
    }

    public void setOldpassword1(String oldpassword1) {
        this.oldpassword1 = oldpassword1;
    }

    public String getOldpassword2() {
        return oldpassword2;
    }

    public void setOldpassword2(String oldpassword2) {
        this.oldpassword2 = oldpassword2;
    }

    public String getOldpassword3() {
        return oldpassword3;
    }

    public void setOldpassword3(String oldpassword3) {
        this.oldpassword3 = oldpassword3;
    }

    public String getOldVSIName() {
        return OldVSIName;
    }

    public void setOldVSIName(String oldVSIName) {
        OldVSIName = oldVSIName;
    }

    public String getOldWANIP() {
        return OldWANIP;
    }

    public void setOldWANIP(String oldWANIP) {
        OldWANIP = oldWANIP;
    }

    public String getOldLLAccountid() {
        return OldLLAccountid;
    }

    public void setOldLLAccountid(String oldLLAccountid) {
        OldLLAccountid = oldLLAccountid;
    }

    public List<CustMacMappping> getCustMacMapppingList() {
        return custMacMapppingList;
    }

    public void setCustMacMapppingList(List<CustMacMappping> custMacMapppingList) {
        this.custMacMapppingList = custMacMapppingList;
    }

    public String getValleyType() {
        return valleyType;
    }

    public void setValleyType(String valleyType) {
        this.valleyType = valleyType;
    }

    public String getCustomerArea() {
        return customerArea;
    }

    public void setCustomerArea(String customerArea) {
        this.customerArea = customerArea;
    }

    public String getLeadNo() {
        return leadNo;
    }

//    public Customers(Customers customers) {
//        this.voicesrvtype = customers.getVoicesrvtype();
//        this.didno = customers.getDidno();
//        this.childdidno = customers.getChilddidno();
//        this.intercomgrp = customers.getIntercomgrp();
//        this.intercomno = customers.getIntercomno();
//        this.status = customers.getStatus();
//        this.mobile = customers.getMobile();
//        this.altmobile = customers.getAltmobile();
//        this.email = customers.getEmail();
//        this.altemail = customers.getAltemail();
//        this.phone = customers.getPhone();
//        this.altphone = customers.getAltphone();
//        this.fax = customers.getFax();
//        this.title = customers.getTitle();
//        this.firstname = customers.getFirstname();
//        this.aadhar = customers.getAadhar();
//        this.contactperson = customers.getContactperson();
//        this.gst = customers.getGst();
//        this.pan = customers.getPan();
//        this.networktype = customers.getNetworktype();
//        this.defaultpoolid = customers.getDefaultpoolid();
//        this.oltportid = customers.getOltportid();
//        this.oltslotid = customers.getOltslotid();
//        this.onuid = customers.getOnuid();

    public void setLeadNo(String leadNo) {
        this.leadNo = leadNo;
    }

    public Long getLeadId() {
        return leadId;
    }

    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "Customer toString Override :" + username;
    }

    @PostLoad
    protected void defaultInitialize() {
        try {
            fullName = "";
            if (null != getTitle() && !getTitle().isEmpty() && getTitle().trim().length() > 0) {
                fullName = getTitle();
            }
            if (null != getFirstname() && !getFirstname().isEmpty() && getFirstname().trim().length() > 0) {
                fullName += " " + getFirstname();
            }
            if (null != getLastname() && !getLastname().isEmpty() && getLastname().trim().length() > 0) {
                fullName += " " + getLastname();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFeasibility() {
        return feasibility;
    }

    public void setFeasibility(String feasibility) {
        this.feasibility = feasibility;
    }

    public String getFeasibilityRemark() {
        return feasibilityRemark;
    }

    public void setFeasibilityRemark(String feasibilityRemark) {
        this.feasibilityRemark = feasibilityRemark;
    }

    public String getFeasibilityRequired() {
        return feasibilityRequired;
    }

    public void setFeasibilityRequired(String feasibilityRequired) {
        this.feasibilityRequired = feasibilityRequired;
    }

    public RejectReason getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(RejectReason rejectReason) {
        this.rejectReason = rejectReason;
    }

    public RejectSubReason getRejectSubReason() {
        return rejectSubReason;
    }

    public void setRejectSubReason(RejectSubReason rejectSubReason) {
        this.rejectSubReason = rejectSubReason;
    }

    public LocalDateTime getRejectCafTime() {
        return rejectCafTime;
    }

    public void setRejectCafTime(LocalDateTime rejectCafTime) {
        this.rejectCafTime = rejectCafTime;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getNextQuotaResetDate() {
        return nextQuotaResetDate;
    }

    public void setNextQuotaResetDate(LocalDate nextQuotaResetDate) {
        this.nextQuotaResetDate = nextQuotaResetDate;
    }

    public Customers(Integer id, String mobile, String email, String countryCode, String username, Integer mvnoId, String status, String custtype, Double walletbalance, Long buId,String firstname,String lastname,String acctno) {
        this.id = id;
        this.mobile = mobile;
        this.email = email;
        this.countryCode = countryCode;
        this.username = username;
        this.mvnoId = mvnoId;
        this.status = status;
        this.custtype = custtype;
        this.walletbalance = walletbalance;
        this.buId = buId;
        this.firstname=firstname;
        this.lastname=lastname;
        this.acctno=acctno;
    }

}
