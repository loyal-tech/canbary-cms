package com.adopt.apigw.rabbitMq.message;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(value = "Customer Update", description = "This is data transfer object for customer which is used to update customer data")
public class UpdateCustomerDto {

    @ApiModelProperty(notes = "Name of the user")
    private Integer custId;

    @ApiModelProperty(notes = "Name of the user", required = true)
    private String userName;

    @ApiModelProperty(notes = "Status of the customer", allowableValues = "Active,Inactive", value = "This field accept value only : Active or Inactive", required = true)
    private String customerStatus;

    @ApiModelProperty(notes = "This is fail count", required = false)
    private Long failCount;

    @ApiModelProperty(notes = "Mac address of the user", required = false)
    private String macAddress;

    @ApiModelProperty(notes = "This is customer Qos policy name", required = false)
    private String qosPolicyName;

    @ApiModelProperty(notes = "This is Email Address", required = true)
    private String emailAddress;

    @ApiModelProperty(notes = "This is Mobile No", required = true)
    private String mobileNo;

    @ApiModelProperty(notes = "This is Country Code", required = false)
    private String countryCode;

    @ApiModelProperty(notes = "This is customer concurrent policy count")
    private Integer concurrentPolicyCount;

    @JsonIgnore
    private String sourceName;

    @ApiModelProperty(notes = "This is customer concurrent policy", required = false)
    private String concurrentPolicy;


    @ApiModelProperty(hidden = true)
    private Integer mvnoId;

    @ApiModelProperty(notes = "This is plan id")
    private Long planId;

    @ApiModelProperty(notes = "This is voucher id")
    private Long voucherId;

    @ApiModelProperty(notes = "This is plan type")
    private String planType;

    @ApiModelProperty(notes = "This is plan name")
    private String planName;

    @ApiModelProperty(notes = "This is voucher code")
    private String voucherCode;

    @ApiModelProperty(notes = "Time based total available quota")
    private String timeBasedTotalQuota;

    @ApiModelProperty(notes = "Time based used quota")
    private String timeBasedUsedQuota;

    @ApiModelProperty(notes = "Time based unused quota")
    private String timeBasedUnusedQuota;

    @ApiModelProperty(notes = "Volume based total available quota")
    private String volumeBasedTotalQuota;

    @ApiModelProperty(notes = "Volume based used quota")
    private String volumeBasedUsedQuota;

    @ApiModelProperty(notes = "Volume based unused quota")
    private String volumeBasedUnusedQuota;

    @ApiModelProperty(notes = "This is plan Upload speed")
    private String uploadSpeed;

    @ApiModelProperty(notes = "This is plan Download  speed")
    private String downloadSpeed;

    @ApiModelProperty(notes = "This is Unlimited plan")
    private Boolean unlimitedPlan;

    @ApiModelProperty(notes = "This is Base upload Qos")
    private Long baseUploadQos;


    @ApiModelProperty(notes = "This is Base download Qos")
    private Long baseDownloadQos;
    
    @ApiModelProperty(notes = "This is Allow Cross Recharge flag")
    private Boolean allowCrossRecharge;

    @ApiModelProperty(value = "This is Location Id", required = false)
    private Long locationId;
    
    @ApiModelProperty(notes = "This is Customer start Date")
	private String startDate;
    
    @ApiModelProperty(notes = "This is Customer end Date")
	private String endDate;
    
    @ApiModelProperty(notes = "Quota Reset Interval")
    private String quotaResetInterval;

    @ApiModelProperty(notes = "Override maximum concurrent session")
    private Integer maxconcurrentsession;

    public UpdateCustomerDto(CustomMessage message) {
        Map<String, Object> map = message.getCustomerData();
        if (map.get("concurrentPolicyCount") != null) {
            this.setConcurrentPolicyCount(Integer.parseInt(map.get("concurrentPolicyCount").toString()));
        }
        if (map.get("userName") != null) {
            this.setUserName(map.get("userName").toString());
        }
        if (map.get("macAddress") != null) {
            this.setMacAddress(map.get("macAddress").toString());
        }
        if (map.get("emailId") != null) {
            this.setEmailAddress(map.get("emailId").toString());
        }
        if (map.get("mobileNo") != null) {
            this.setMobileNo(map.get("mobileNo").toString());
        }
        if (map.get("failCount") != null) {
            this.setFailCount(Long.parseLong(map.get("failCount").toString()));
        }
        if (map.get("qosPolicyName") != null) {
            this.setQosPolicyName(map.get("qosPolicyName").toString());
        }
        if (map.get("customerStatus") != null) {
            this.setCustomerStatus(map.get("customerStatus").toString());
        }
        if (map.get("countryCode") != null) {
            this.countryCode = map.get("countryCode").toString();
        }
        if (map.get("mvnoId") != null) {
            this.mvnoId = Integer.parseInt(map.get("mvnoId").toString());
        }
        if (map.get("planId") != null) {
            this.planId = Long.parseLong(map.get("planId").toString());
        }
        if (map.get("voucherId") != null) {
            this.voucherId = Long.parseLong(map.get("voucherId").toString());
        }
        if (map.get("planType") != null) {
            this.planType = map.get("planType").toString();
        }
        if (map.get("planName") != null) {
            this.planName = map.get("planName").toString();
        }
        if (map.get("voucherCode") != null) {
            this.voucherCode = map.get("voucherCode").toString();
        }
        if (map.get("timeBasedTotalQuota") != null) {
            this.timeBasedTotalQuota = map.get("timeBasedTotalQuota").toString();
        }
        if (map.get("timeBasedUsedQuota") != null) {
            this.timeBasedUsedQuota = map.get("timeBasedUsedQuota").toString();
        }
        if (map.get("timeBasedUnusedQuota") != null) {
            this.timeBasedUnusedQuota = map.get("timeBasedUnusedQuota").toString();
        }
        if (map.get("volumeBasedTotalQuota") != null) {
            this.volumeBasedTotalQuota = map.get("volumeBasedTotalQuota").toString();
        }
        if (map.get("volumeBasedUsedQuota") != null) {
            this.volumeBasedUsedQuota = map.get("volumeBasedUsedQuota").toString();
        }
        if (map.get("volumeBasedUnusedQuota") != null) {
            this.volumeBasedUnusedQuota = map.get("volumeBasedUnusedQuota").toString();
        }
        if (map.get("uploadSpeed") != null) {
            this.uploadSpeed = map.get("uploadSpeed").toString();
        }
        if (map.get("downloadSpeed") != null) {
            this.downloadSpeed = map.get("downloadSpeed").toString();
        }
        if (map.get("unlimitedPlan") != null) {
            this.unlimitedPlan = Boolean.parseBoolean(map.get("unlimitedPlan").toString());
        }
        if (map.get("baseUploadQos") != null) {
            this.baseUploadQos = Long.parseLong(map.get("baseUploadQos").toString());
        }
        if (map.get("baseDownloadQos") != null) {
            this.baseDownloadQos = Long.parseLong(map.get("baseDownloadQos").toString());
        }
        if (map.get("allowCrossRecharge") != null) {
        	this.setAllowCrossRecharge(Boolean.parseBoolean(map.get("allowCrossRecharge").toString()));
        }
        if(map.get("quotaResetInterval") != null)
        {
        	this.quotaResetInterval = map.get("quotaResetInterval").toString();
        }
        if(map.get("maxconcurrentsession") != null)
        {
            this.maxconcurrentsession = Integer.valueOf(map.get("maxconcurrentsession").toString());
        }
    }
}
