package com.adopt.apigw.pojo.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class postpaidPlanFetchPojo {

    private Integer id;

    private String name;
    private String displayName;
    private String code;
    private String desc;
    private String category;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

//    private String uploadQOS;
//    private String downloadQOS;
//    private String uploadTs;
//    private String downloadTs;
    private Boolean allowOverUsage;

    private String quotaUnit;
    private Long quota;
    private String planStatus;

    private Long childQuota;
    private String childQuotaUnit;

//    private Long slice;
//    private String sliceUnit;

//    private Boolean attachedToAllHotSpots;
//    private String param1;
//    private String param2;
//    private String param3;

    private Integer mvnoId;
    private String status;
//    private Long taxId;
    private Integer serviceId;
    private String serviceName;
    private Integer timebasepolicyId;
    private String timebasepolicyName;
    private String plantype;
//    private Integer maxChild;

    private Double dbr;
    private String planGroup;
    private Double validity;
//    private String saccode;
    private String maxconcurrentsession;

//    private String quotaunittime;
//    private String quotatime;
    private String quotatype;

    private Double offerprice;
    private Long qospolicyid;
    private String qospolicyName;

    private Boolean isDelete;

    private String createDateString;
    private String updateDateString;

//    private Long quotadid;
//    private Boolean quotaintercom;
//    private Long quotaunitdid;
//    private Boolean quotaunitintercom;

//    private String dataCategory;
    private Double taxamount;


   /* @Transient
    private List<ServiceArea> serviceAreaNameList;*/

    private String quotaResetInterval;
    private String mode;
    private String unitsOfValidity;
//    private Long buId;
//    private String nextStaff;
    private Double newOfferPrice;
//    private String nextTeamHierarchyMapping;
    private Boolean allowdiscount;
//    private String product_category;
//    private String product_type;
//    private Long productId;
    private Double discount;
//    private String ownershipType;

//    private Boolean invoiceToOrg;
//    private Boolean requiredApproval;

//    private String bandwidth;
//    private String link_type;
//    private String connection_type;
//    private String distance;
//    private String ram;
//    private String cpu;
//    private String storage;
//    private String storage_type;
//    private Boolean auto_backup;
//    private Boolean cpanel;
//    private String location;
//    private Integer quantity;
//    private String package_type;
//    private Integer number_of_days;
//    private Integer no_of_users;
//    private String rack_space;
//    private String rack_unit;
//    private String power_consumption;
//    private String network_card;
//    private String ip_or_ip_pool;
//    private Integer no_of_license;
//    private Integer no_of_email_user_license;
//    private Integer no_of_server_license;
//    private Integer no_of_user_license;
//    private Integer no_of_nodes;
//    private Integer event_per_second;
//    private Integer no_of_additional_server;
//    private Integer no_of_additional_storage;
//    private String additional_storage_type;
//    private String eps_License;
//    private String no_of_nodes_license;
//    private String hardware_resource;
//    private String man_power;
//    private Integer no_of_domains;
//    private String security_modules;
//    private String hardware_or_servers;
//    private String country;
//    private Integer no_of_vpn;
//    private String device_throughput;
//    private Boolean retail;

    private Integer displayId;
    private String displayPostpaidName;


    private String businessType;
    private Boolean basePlan;
    private Boolean useQuota;
//    private String chunk;
    private String mvnoName;
    private String usageQuotaType;
//    private Boolean addonToBase;
//    private Integer maxHoldDurationDays;
//    private Integer maxHoldAttempts;
//    private String accessibility;
public postpaidPlanFetchPojo(
        Integer id,
        String name,
        String displayName,
        String code,
        String desc,
        String category,
        LocalDate startDate,
        LocalDate endDate,
        Boolean allowOverUsage,
        String quotaUnit,
        Long quota,
        String planStatus,
        Long childQuota,
        String childQuotaUnit,
        Integer mvnoId,
        String status,
        Integer serviceId,
        Integer timebasepolicyId,
        String plantype,
        Double dbr,
        String planGroup,
        Double validity,
        String maxconcurrentsession,
        String quotaResetInterval,
        String mode,
        String unitsOfValidity,
        Double newOfferPrice,
        Boolean allowdiscount,
        Boolean basePlan,
        Boolean useQuota,
        String mvnoName,
        String usageQuotaType,
        Double taxamount
) {
    this.id = id;
    this.name = name;
    this.displayName = displayName;
    this.code = code;
    this.desc = desc;
    this.category = category;
    this.startDate = startDate;
    this.endDate = endDate;
    this.allowOverUsage = allowOverUsage;
    this.quotaUnit = quotaUnit;
    this.quota = quota;
    this.planStatus = planStatus;
    this.childQuota = childQuota;
    this.childQuotaUnit = childQuotaUnit;
    this.mvnoId = mvnoId;
    this.status = status;
    this.serviceId = serviceId;
    this.timebasepolicyId = timebasepolicyId;
    this.plantype = plantype;
    this.dbr = dbr;
    this.planGroup = planGroup;
    this.validity = validity;
    this.maxconcurrentsession = maxconcurrentsession;
    this.quotaResetInterval = quotaResetInterval;
    this.mode = mode;
    this.unitsOfValidity = unitsOfValidity;
    this.newOfferPrice = newOfferPrice;
    this.allowdiscount = allowdiscount;
    this.basePlan = basePlan;
    this.useQuota = useQuota;
    this.mvnoName = mvnoName;
    this.usageQuotaType = usageQuotaType;
    this.taxamount = taxamount;
}

    public postpaidPlanFetchPojo(Integer id,Integer mvnoId) {
        this.id = id;
        this.mvnoId = mvnoId;
    }


}
