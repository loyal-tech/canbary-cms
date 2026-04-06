package com.adopt.apigw.pojo.api;

import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostpaidPlanMigrationPojo {

    private Integer id;

    @NotNull(message = "Please enter name")
    private String name;

    @NotNull(message = "Please enter display name")
    private String displayName;

    @NotNull(message = "Please enter code")
    private String code;

    @NotNull(message = "Please enter description")
    private String desc;

    @NotNull(message = "Please enter category")
    private String category;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private String uploadQOS;

    private String downloadQOS;

    private String uploadTs;

    private String downloadTs;

    private Boolean allowOverUsage;

    private String quotaUnit;

    private Long quota;

    private String planStatus;

    private Long childQuota;

    private String childQuotaUnit;

    private Long slice;

    private String sliceUnit;

    private String attachedToAllHotSpots;

    private String param1;

    private String param2;

    private String param3;

    private Integer mvnoId;

    @NotNull(message = "Please enter status")
    private String status;

    private Integer taxId;

    private Integer serviceId;
    
    private String serviceName;

    private Integer timebasepolicyId;

    private String timebasepolicyName;

    @NotNull(message = "Please enter plantype")
    private String plantype;

    private Integer maxChild;

//    @JsonManagedReference
//    private List<PostpaidPlanChargePojo> chargeList = new ArrayList<>();
//    private List<String> chargeNameList=new ArrayList<>();

    private Double dbr;

    private String planGroup;

    private Double validity;

    //@NotNull(message = "Please enter sac code")
    private String saccode;

    @NotNull(message = "Please enter maxconcurrentsession")
    private String maxconcurrentsession;

    private String quotaunittime;

    private Double quotatime;

    @NotNull(message = "Please enter quota type")
    private String quotatype;

    @NotNull(message = "Please enter offerprice")
    private Double offerprice;

    private Long qospolicyid;
    
    private String qospolicyName;

    private List<Integer> radiusprofileIds = new ArrayList<>();

    private Boolean isDelete = false;

    private String createDateString;
    private String updateDateString;

    private Double quotadid;
    private Double quotaintercom;
    private String quotaunitdid;
    private String quotaunitintercom;
    private String dataCategory;
    private Double taxamount;


    private List<Long> serviceAreaIds = new ArrayList<>();
    private List<ServiceAreaDTO> serviceAreaNameList = new ArrayList<>();
    private String quotaResetInterval;
    private String mode = "NORMAL";
    
    private String unitsOfValidity;

    private Long buId;

   private Integer nextStaff;

    private Double newOfferPrice;

    private Integer nextTeamHierarchyMapping;

    private String Accessibility;

    private Boolean allowdiscount;

    private Long product_category;

   private String product_type;

   private Long productId;

    private String discount;
    private String ownershipType;
    private String startDateString;
    private String endDateString;

//    private List<Productplanmappingdto> productplanmappingList =  new ArrayList<>();

    private List<Long> productplanmappingids = new ArrayList<>();

    private Boolean invoiceToOrg;

    private Boolean requiredApproval;

//    private List<PlanCasMapping> planCasMappingList;

    private String bandwidth;
    private String link_type;
    private String connection_type;
    private String distance;
    private String ram;
    private String cpu;
    private String storage;
    private String storage_type;
    private String auto_backup;
    private String cpanel;
    private String location;
    private String quantity;
    private String package_type;
//    private String type;
    private String number_of_days;
    private String no_of_users;
    private String rack_space;
    private String rack_unit;
    private String power_consumption;
    private String network_card;
    private String ip_or_ip_pool;
    private String no_of_license;
    private String no_of_email_user_license;
    private String no_of_server_license;
    private String no_of_user_license;
    private String no_of_nodes;
    private String event_per_second;
    private String no_of_additional_server;
    private String no_of_additional_storage;
    private String additional_storage_type;
    private String eps_License;
    private String no_of_nodes_license;
    private String hardware_resource;
    private String man_power;
    private String no_of_domains;
    private String security_modules;
    private String hardware_or_servers;
    private String country;
    private String no_of_vpn;
    private String device_throughput;
    private String retail;

    private Long displayId;
    private String displayPostpaidName;

//    private List<PlanQosMappingPojo> planQosMappingEntityList;
//
//    private List<PlanQosMappingEntity> viewplanQosMappingEntityList;
    private List<Integer> qosPolicyId=new ArrayList<>();

    private String businessType;

    private  Boolean basePlan = false;

    private boolean useQuota;

    private Integer chunk;

    private String mvnoName;
    private String chargeName;
    private Integer billCycle;
    private List<String> serviceAreas = new ArrayList<>();
    private List<String> serviceNames = new ArrayList<>();
    private List<String> chargenameList = new ArrayList<>();
}
