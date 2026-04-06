package com.adopt.apigw.pojo.api;

import javax.validation.constraints.NotNull;

import com.adopt.apigw.model.common.LeasedLineCircuitDetails;

public class LeasedLineCircuitDetailsPojo {
	
	private Integer id;
	
	@NotNull
    private String llcIdentifier;
	
	@NotNull
    private String llcLabel;
	
	@NotNull
    private String llcType;
	
	@NotNull
    private String llcBandwidthSpeed;
	
	@NotNull
    private String llcStaticIP;
	
	@NotNull
    private String llcDeviceType;
	
	private Boolean isDelete=false;
	
    private Integer packageId;
    
    private String planName;

	private Long buId;

	public LeasedLineCircuitDetailsPojo() {}

	public LeasedLineCircuitDetailsPojo(LeasedLineCircuitDetails leasedLineCircuitDetails,String planName) {
		this.id = leasedLineCircuitDetails.getId();
		this.llcIdentifier = leasedLineCircuitDetails.getLlcIdentifier();
		this.llcLabel = leasedLineCircuitDetails.getLlcLabel();
		this.llcType = leasedLineCircuitDetails.getLlcType();	
		this.packageId = leasedLineCircuitDetails.getPackageId();	
		this.llcStaticIP = leasedLineCircuitDetails.getLlcStaticIP();
		this.llcDeviceType = leasedLineCircuitDetails.getLlcDeviceType();
		if(planName != null) {
			this.planName = planName;
		}
	}


	@Override
	public String toString() {
		return "LeasedLineCircuitDetailsPojo [llcIdentifier=" + llcIdentifier + ", llcLabel=" + llcLabel + ", llcType="
				+ llcType + ", llcBandwidthSpeed=" + llcBandwidthSpeed + ", llcStaticIP=" + llcStaticIP
				+ ", llcDeviceType=" + llcDeviceType + ", isDelete=" + isDelete + ", packageId=" + packageId + "]";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLlcIdentifier() {
		return llcIdentifier;
	}

	public void setLlcIdentifier(String llcIdentifier) {
		this.llcIdentifier = llcIdentifier;
	}

	public String getLlcLabel() {
		return llcLabel;
	}

	public void setLlcLabel(String llcLabel) {
		this.llcLabel = llcLabel;
	}

	public String getLlcType() {
		return llcType;
	}

	public void setLlcType(String llcType) {
		this.llcType = llcType;
	}

	public String getLlcBandwidthSpeed() {
		return llcBandwidthSpeed;
	}

	public void setLlcBandwidthSpeed(String llcBandwidthSpeed) {
		this.llcBandwidthSpeed = llcBandwidthSpeed;
	}

	public String getLlcStaticIP() {
		return llcStaticIP;
	}

	public void setLlcStaticIP(String llcStaticIP) {
		this.llcStaticIP = llcStaticIP;
	}

	public String getLlcDeviceType() {
		return llcDeviceType;
	}

	public void setLlcDeviceType(String llcDeviceType) {
		this.llcDeviceType = llcDeviceType;
	}

	public Boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}

	public Integer getPackageId() {
		return packageId;
	}

	public void setPackageId(Integer packageId) {
		this.packageId = packageId;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}
}
