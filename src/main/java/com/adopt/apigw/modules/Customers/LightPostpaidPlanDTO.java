package com.adopt.apigw.modules.Customers;

import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LightPostpaidPlanDTO {

    private Integer id;

    private String serviceName;

    private String serviceArea;

    private List<Long> serviceAreaIds = new ArrayList<>();
    private List<HashMap<Long, String>> serviceAreas = new ArrayList<HashMap<Long, String>>();
    private String name ;

    private Double offerprice;

    private String unitsOfValidity;

    private Double validity;

    private Integer mvnoId;

    private String plantype;

    private String planGroupType;

    private Integer serviceId;

    private String uploadSpeed;

    private String downloadSpeed;
    private byte[] profileImage;
    private String logo_file_name;
    private List<Integer> branchIds;
    private Double proRatedCharge;

    private String param1;

    private String param2;

    private String dataQuotaUnit;

    private String timeQuotaUnit;

    private Integer volumeQuota;

    private Double timeQuota;

    private String quotaType;

    private String description;
    private String qosPolicyName;
    private String currency;
    private String qosSpeed;

    private Double actualprice;

    public LightPostpaidPlanDTO(Integer id, String serviceName, String name, Double offerprice, Integer mvnoId, String uploadSpeed, String downloadSpeed) {
        this.id = id;
        this.serviceName = serviceName;
        this.name = name;
        this.offerprice = offerprice;
        this.mvnoId = mvnoId;
        this.uploadSpeed = uploadSpeed;
        this.downloadSpeed = downloadSpeed;
    }
    public LightPostpaidPlanDTO(Integer id, String serviceName, String name, Double offerprice, Integer mvnoId, String uploadSpeed, String downloadSpeed, List<Long> serviceAreaIds) {
        this.id = id;
        this.serviceName = serviceName;
        this.name = name;
        this.offerprice = offerprice;
        this.mvnoId = mvnoId;
        this.uploadSpeed = uploadSpeed;
        this.downloadSpeed = downloadSpeed;
        this.serviceAreaIds = serviceAreaIds;
    }
    public LightPostpaidPlanDTO(Integer id, String serviceName, String name, Double offerprice, Integer mvnoId, String uploadSpeed, String downloadSpeed, byte[] profileImage, String logo_file_name, String planType, Integer serviceId, Double validity, String unitsOfValidity) {
        this.id = id;
        this.serviceName = serviceName;
        this.name = name;
        this.offerprice = offerprice;
        this.mvnoId = mvnoId;
        this.uploadSpeed = uploadSpeed;
        this.downloadSpeed = downloadSpeed;
        this.logo_file_name=logo_file_name;
        this.profileImage=profileImage;
        this.plantype = planType;
        this.serviceId = serviceId;
        this.validity = validity;
        this.unitsOfValidity = unitsOfValidity;

    }

    public LightPostpaidPlanDTO(Integer id, String serviceName, String name, Double offerprice, Integer mvnoId, String uploadSpeed, String downloadSpeed, byte[] profileImage, String logo_file_name, String planType, Integer serviceId, Double validity, String unitsOfValidity,String param1 , String param2) {
        this.id = id;
        this.serviceName = serviceName;
        this.name = name;
        this.offerprice = offerprice;
        this.mvnoId = mvnoId;
        this.uploadSpeed = uploadSpeed;
        this.downloadSpeed = downloadSpeed;
        this.logo_file_name=logo_file_name;
        this.profileImage=profileImage;
        this.plantype = planType;
        this.serviceId = serviceId;
        this.validity = validity;
        this.unitsOfValidity = unitsOfValidity;
        this.param1 = param1;
        this.param2 = param2;

    }

    public LightPostpaidPlanDTO(String name, String qosSpeed) {
        this.name = name;
        this.qosSpeed = qosSpeed;
    }
}
