package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.radius.RadiusProfile;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.dto.Productplanmappingdto;
import com.adopt.apigw.modules.PlanQosMapping.PlanQosMappingEntity;
import com.adopt.apigw.modules.PlanQosMapping.PlanQosMappingPojo;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.qosPolicy.domain.QOSPolicy;
//import com.adopt.apigw.modules.tickets.domain.TicketSubCategoryGroupReasonMapping;
import com.adopt.apigw.pojo.api.DiscountMappingPojo;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "TBLMPOSTPAIDPLAN")
@EntityListeners(AuditableListener.class)
public class PostpaidPlan extends Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POSTPAIDPLANID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "DISPLAYNAME", nullable = false, length = 40)
    private String displayName;

    @Column(name = "PLANCODE", nullable = false, length = 40)
    private String code;

    @Column(name = "DESCRIPTION", nullable = false, length = 40)
    private String desc;

    @Column(name = "PLANCATEGORY", nullable = false, length = 40)
    private String category;

    @DiffIgnore
    @Column(name = "MAXALLOWEDCHILD", length = 40)
    private Integer maxChild;

//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    @JsonProperty("startDate")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DiffIgnore
    @Column(name = "STARTDATE", length = 40)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate startDate;

//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    @JsonProperty("endDate")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DiffIgnore
    @Column(name = "ENDDATE", length = 40)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate endDate;

    @DiffIgnore
    @Column(name = "QUOTA", length = 40)
    private Long quota;

    @Column(name = "QUOTAUNIT", length = 40)
    private String quotaUnit;

    @Column(name = "UPLOADQOS", length = 40)
    private String uploadQOS;

    @Column(name = "DOWNLOADQOS", length = 40)
    private String downloadQOS;

    @Column(name = "UPLOADTS", length = 40)
    private String uploadTs;

    @Column(name = "DOWNLOADTS", length = 40)
    private String downloadTs;

    @Column(name = "allowoverusage", length = 3)
    private Boolean allowOverUsage;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    @Column(name = "PLANSTATUS", length = 40)
    private String planStatus;

    @Column(name = "CHILDQUOTA", length = 40)
    private Long childQuota;

    @Column(name = "CHILDQUOTAUNIT", length = 40)
    private String childQuotaUnit;

    @Column(name = "SLICE", length = 40)
    private Long slice;

    @Column(name = "SLICEUNIT", length = 40)
    private String sliceUnit;

    @Column(name = "ATTACHEDTOALLHOTSPOT", length = 40)
    private String attachedToAllHotSpots;

    @Column(name = "PARAM1", length = 40)
    private String param1;

    @Column(name = "PARAM2", length = 40)
    private String param2;

    @Column(name = "PARAM3", length = 40)
    private String param3;

    @DiffIgnore
    @Column(name = "MVNOID", length = 40, updatable = false)
    private Integer mvnoId;

    @DiffIgnore
    @Column(name = "TAXID", length = 40)
    private Integer taxId;

    @DiffIgnore
    @Column(name = "serviceid", length = 40)
    private Integer serviceId;

    @Column(name = "timebasepolicyid", length = 40)
    private Integer timebasepolicyId;

    @Column(name = "plantype", nullable = false, length = 40)
    private String plantype;

    @Column(name = "dbr", nullable = false, length = 40)
    private Double dbr;

    @Column(name = "mvnoName")
    private String mvnoName;


    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference
    @EqualsAndHashCode.Exclude
    private List<PostpaidPlanCharge> chargeList = new ArrayList<>();


    @Column(name = "plangroup", nullable = false, length = 100)
    private String planGroup;

    @Column(name = "validity", length = 4)
    private Double validity;

    @Column(nullable = true)
    private String saccode;

    @Column(nullable = false)
    private String maxconcurrentsession;

    private String quotaunittime;

    private Double quotatime;


    @Column(nullable = false)
    private String quotatype;

    private Double offerprice;

    private Double quotadid;
    private Double quotaintercom;
    private String quotaunitdid;
    private String quotaunitintercom;

    @ManyToOne
    @JoinColumn(name = "qospolicy_id")
    private QOSPolicy qospolicy;

    @ManyToMany
    @JoinTable(name = "tblpostpaidplanradiusprofilerel", joinColumns = {@JoinColumn(name = "POSTPAIDPLANID")}, inverseJoinColumns = {@JoinColumn(name = "radiusprofileid")})
   @LazyCollection(LazyCollectionOption.FALSE)
    private List<RadiusProfile> radiusprofile;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "datacategory")
    private String dataCategory;

    @DiffIgnore
    private Double taxamount;

    @Transient
    private String serviceName;
    @Transient
    private String timebasepolicyName;
    @ManyToMany
   @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tblplanservicearearel", joinColumns = {@JoinColumn(name = "planid")}, inverseJoinColumns = {@JoinColumn(name = "serviceareaid")})
    @ToString.Exclude
    @DiffIgnore
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();
    @Column(name = "quotarestinterval", nullable = false, length = 40)
    private String quotaResetInterval;
    @Column(nullable = false, length = 40, columnDefinition = "varchar(255) default 'NORMAL'")
    private String mode;
    @Column(name = "unitsofvalidity", nullable = false, length = 40, columnDefinition = "varchar(100) default 'Days'")
    private String unitsOfValidity;
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;
    @Column(name = "next_team_hir_mapping")
    private Integer nextTeamHierarchyMapping;
    @DiffIgnore
    @Column(name = "next_staff")
    private Integer nextStaff;
    @Column(name = "new_offer_price")
    private Double newOfferPrice;
    @Column(name = "Accessibility")
    private String Accessibility;
    @Column(name = "allowdiscount")
    private boolean allowdiscount;
    @Column(name = "product_id")
    private Long productId;
    @Transient
    private List<Productplanmappingdto> productplanmappingList = new ArrayList<>();

//    private String product_category;
//
//    private String product_type;
    @Column(name = "invoicetoorg")
    private Boolean invoiceToOrg;
    @Column(name = "requiredapproval")
    private Boolean requiredApproval;
   @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = PlanCasMapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "planid")
    private List<PlanCasMapping> planCasMappingList;
    @Column(name = "bandwidth")
    private String bandwidth;
    @Column(name = "linktype")
    private String link_type;
    @Column(name = "connectiontype")
    private String connection_type;
    @Column(name = "distance")
    private String distance;
    @Column(name = "ram")
    private String ram;
    @Column(name = "cpu")
    private String cpu;
    @Column(name = "storage")
    private String storage;
    @Column(name = "storagetype")
    private String storage_type;
    @Column(name = "autobackup")
    private String auto_backup;
    @Column(name = "cpanel")
    private String cpanel;
    @Column(name = "location")
    private String location;
    @Column(name = "quantity")
    private String quantity;
    @Column(name = "packagetype")
    private String package_type;
    //    @Column(name = "type")
//    private String type;
    @Column(name = "numberofdays")
    private String number_of_days;
    @Column(name = "numberoofusers")
    private String no_of_users;
    @Column(name = "rackspace")
    private String rack_space;
    @Column(name = "rackunit")
    private String rack_unit;
    @Column(name = "powerconsumption")
    private String power_consumption;
    @Column(name = "networkcard")
    private String network_card;
    @Column(name = "iporippool")
    private String ip_or_ip_pool;
    @Column(name = "numberoflicense")
    private String no_of_license;
    @Column(name = "noofemailuserlicense")
    private String no_of_email_user_license;
    @Column(name = "noofserverlicense")
    private String no_of_server_license;
    @Column(name = "noofuserlicense")
    private String no_of_user_license;
    @Column(name = "noofnodes")
    private String no_of_nodes;
    @Column(name = "eventpersecond")
    private String event_per_second;
    @Column(name = "noofadditionalserver")
    private String no_of_additional_server;
    @Column(name = "noofadditionalstorage")
    private String no_of_additional_storage;
    @Column(name = "additionalstoragetype")
    private String additional_storage_type;
    @Column(name = "epslicense")
    private String eps_License;
    @Column(name = "noofnodeslicense")
    private String no_of_nodes_license;
    @Column(name = "hardwareresource")
    private String hardware_resource;
    @Column(name = "manpower")
    private String man_power;
    @Column(name = "noofdomains")
    private String no_of_domains;
    @Column(name = "securitymodules")
    private String security_modules;
    @Column(name = "hardwareorservers")
    private String hardware_or_servers;
    @Column(name = "country")
    private String country;
    @Column(name = "noofvpn")
    private String no_of_vpn;
    @Column(name = "devicethroughput")
    private String device_throughput;
    @Column(name = "retail")
    private String retail;
    @Column(name = "business_type")
    private String businessType;
    @Column(name = "baseplan", columnDefinition = "Boolean default false")
    private Boolean basePlan = false;

    @Column(name = "addon_to_base")
    private boolean addonToBase;

    @Column(name = "template_id", length = 40)
    private Long templateId;
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference
    @OneToMany(targetEntity = PlanQosMappingEntity.class, cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "planid")
    private List<PlanQosMappingEntity> planQosMappingEntities;
    @Transient
    private List<PlanQosMappingPojo> planQosMappingEntityList;
    @Transient
    private List<PostPaidPlanServiceAreaMapping> postPaidPlanServiceAreaMappingList;
    @Transient
    private Boolean isApprove = false;
    @Column(name = "use_quota")
    private boolean useQuota;
    @Column(name = "chunk")
    private Double chunk;
    @Column(name = "usage_quota_type")
    private String usageQuotaType;
    public PostpaidPlan() {
    }

    @Column(name = "max_hold_duration_days")
    private Integer maxHoldDurationDays=0;

    @Column(name = "max_hold_attempts")
    private Integer maxHoldAttempts=0;

    @Column(name ="currency")
    private String currency;

    @Transient
    private List<DiscountMappingPojo> discountList;

    public PostpaidPlan(PostpaidPlan plan) {
        this.id = plan.getId();
        this.name = plan.getName();
        this.displayName = plan.getDisplayName();
        this.code = plan.getCode();
        this.desc = plan.getDesc();
        this.category = plan.getCategory();
        this.maxChild = plan.getMaxChild();
        this.startDate = plan.getStartDate();
        this.endDate = plan.getEndDate();
        this.quota = plan.getQuota();
        this.quotaUnit = plan.getQuotaUnit();
        this.uploadQOS = plan.getUploadQOS();
        this.downloadQOS = plan.getDownloadQOS();
        this.uploadTs = plan.getUploadTs();
        this.downloadTs = plan.getDownloadTs();
        this.allowOverUsage = plan.getAllowOverUsage();
        this.status = plan.getStatus();
        this.planStatus = plan.getPlanStatus();
        this.childQuota = plan.getChildQuota();
        this.childQuotaUnit = plan.getChildQuotaUnit();
        this.slice = plan.getSlice();
        this.sliceUnit = plan.getSliceUnit();
        this.attachedToAllHotSpots = plan.getAttachedToAllHotSpots();
        this.param1 = plan.getParam1();
        this.param2 = plan.getParam2();
        this.param3 = plan.getParam3();
        this.mvnoId = plan.getMvnoId();
        this.taxId = plan.getTaxId();
        this.serviceId = plan.getServiceId();
        this.timebasepolicyId = plan.getTimebasepolicyId();
        this.plantype = plan.getPlantype();
        this.dbr = plan.getDbr();
        this.chargeList = plan.getChargeList();
        this.planGroup = plan.getPlanGroup();
        this.validity = plan.getValidity();
        this.saccode = plan.getSaccode();
        this.maxconcurrentsession = plan.getMaxconcurrentsession();
        this.quotaunittime = plan.getQuotaunittime();
        this.quotatime = plan.getQuotatime();
        this.quotatype = plan.getQuotatype();
        this.offerprice = plan.getOfferprice();
        this.quotadid = plan.getQuotadid();
        this.quotaintercom = plan.getQuotaintercom();
        this.quotaunitdid = plan.getQuotaunitdid();
        this.quotaunitintercom = plan.getQuotaunitintercom();
        this.qospolicy = plan.getQospolicy();
        this.radiusprofile = plan.getRadiusprofile();
        this.isDelete = plan.getIsDelete();
        this.dataCategory = plan.getDataCategory();
        this.taxamount = plan.getTaxamount();
        this.serviceName = plan.getServiceName();
        this.timebasepolicyName = plan.getTimebasepolicyName();
        // this.serviceAreaNameList = serviceAreaNameList;
        this.quotaResetInterval = plan.getQuotaResetInterval();
        this.mode = plan.getMode();
        this.unitsOfValidity = plan.getUnitsOfValidity();
        this.buId = plan.getBuId();
        this.nextTeamHierarchyMapping = plan.getNextTeamHierarchyMapping();
        this.nextStaff = plan.getNextStaff();
        this.newOfferPrice = plan.getNewOfferPrice();
        this.Accessibility = plan.getAccessibility();
        this.allowdiscount = plan.isAllowdiscount();
        this.productId = plan.getProductId();
        this.productplanmappingList = plan.getProductplanmappingList();
        this.invoiceToOrg = plan.getInvoiceToOrg();
        this.requiredApproval = plan.getRequiredApproval();
        this.planCasMappingList = plan.getPlanCasMappingList();
//        this.bandwidth = bandwidth;
//        this.link_type = link_type;
//        this.connection_type = connection_type;
//        this.distance = distance;
//        this.ram = ram;
//        this.cpu = cpu;
//        this.storage = storage;
//        this.storage_type = storage_type;
//        this.auto_backup = auto_backup;
//        this.cpanel = cpanel;
//        this.location = location;
//        this.quantity = quantity;
//        this.package_type = package_type;
//        this.number_of_days = number_of_days;
//        this.no_of_users = no_of_users;
//        this.rack_space = rack_space;
//        this.rack_unit = rack_unit;
//        this.power_consumption = power_consumption;
//        this.network_card = network_card;
//        this.ip_or_ip_pool = ip_or_ip_pool;
//        this.no_of_license = no_of_license;
//        this.no_of_email_user_license = no_of_email_user_license;
//        this.no_of_server_license = no_of_server_license;
//        this.no_of_user_license = no_of_user_license;
//        this.no_of_nodes = no_of_nodes;
//        this.event_per_second = event_per_second;
//        this.no_of_additional_server = no_of_additional_server;
//        this.no_of_additional_storage = no_of_additional_storage;
//        this.additional_storage_type = additional_storage_type;
//        this.eps_License = eps_License;
//        this.no_of_nodes_license = no_of_nodes_license;
//        this.hardware_resource = hardware_resource;
//        this.man_power = man_power;
//        this.no_of_domains = no_of_domains;
//        this.security_modules = security_modules;
//        this.hardware_or_servers = hardware_or_servers;
//        this.country = country;
//        this.no_of_vpn = no_of_vpn;
//        this.device_throughput = device_throughput;
//        this.retail = retail;
//        this.businessType = businessType;
        this.basePlan = plan.getBasePlan();
        //   this.templateId = templateId;
        this.planQosMappingEntities = plan.getPlanQosMappingEntities();
        this.planQosMappingEntityList = plan.getPlanQosMappingEntityList();
        this.isApprove = plan.getIsApprove();
        this.useQuota = plan.useQuota;
        this.chunk = plan.getChunk();
        this.usageQuotaType = plan.getUsageQuotaType();
    this.addonToBase = plan.isAddonToBase();
   }
    public PostpaidPlan(Integer id, String displayName, String planType, Double validity, String unitsOfValidity,
                        LocalDate startDate, LocalDate endDate, String category, Double newOfferPrice,
                        Double offerPrice, Double taxAmount, String status, String quotaType,
                        String createdByName, String mvnoName, Integer nextStaff,String currency, String planGroup) {
        this.id = id;
        this.displayName = displayName;
        this.plantype = planType;
        this.validity = validity;
        this.unitsOfValidity = unitsOfValidity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
        this.newOfferPrice = newOfferPrice;
        this.offerprice = offerPrice;
        this.taxamount = taxAmount;
        this.status = status;
        this.usageQuotaType = quotaType;
        this.createdByName = createdByName;
        this.mvnoName = mvnoName;
        this.nextStaff = nextStaff;
        this.currency = currency;
        this.planGroup = planGroup;
    }

    public PostpaidPlan(Integer id, String name,Integer serviceId, String plantype, String planGroup, Double offerprice, Double newOfferPrice) {
        this.id = id;
        this.name = name;
        this.serviceId = serviceId;
        this.plantype = plantype;
        this.planGroup = planGroup;
        this.offerprice = offerprice;
        this.newOfferPrice = newOfferPrice;
    }

    public PostpaidPlan(Integer id, String displayName,String name, String planType, Double validity, String unitsOfValidity,
                        LocalDate startDate, LocalDate endDate, String category, Double newOfferPrice,
                        Double offerPrice, Double taxAmount, String status, String quotaType,
                        String createdByName, String mvnoName, Integer nextStaff,String currency, String planGroup) {
        this.id = id;
        this.displayName = displayName;
        this.name = name;
        this.plantype = planType;
        this.validity = validity;
        this.unitsOfValidity = unitsOfValidity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
        this.newOfferPrice = newOfferPrice;
        this.offerprice = offerPrice;
        this.taxamount = taxAmount;
        this.status = status;
        this.usageQuotaType = quotaType;
        this.createdByName = createdByName;
        this.mvnoName = mvnoName;
        this.nextStaff = nextStaff;
        this.currency = currency;
        this.planGroup = planGroup;
    }


    public PostpaidPlan(Integer id,String name,String plantype ,Integer maxHoldDurationDays,Integer maxHoldAttempts ) {
        this.id = id;
        this.name = name;
        this.plantype = plantype;
        this.maxHoldDurationDays=maxHoldDurationDays;
        this.maxHoldAttempts=maxHoldAttempts;

    }

    public PostpaidPlan(Integer id) {
        this.id = id;
    }
}
