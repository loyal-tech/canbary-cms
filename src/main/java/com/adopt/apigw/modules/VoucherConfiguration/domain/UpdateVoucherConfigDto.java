package com.adopt.apigw.modules.VoucherConfiguration.domain;

import com.adopt.apigw.model.common.FieldType;
import com.adopt.apigw.model.radius.VoucherLinkType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(value = "Voucher Config Update",description = "This is data transfer object for voucher config which is used to update voucher config data")
public class UpdateVoucherConfigDto {

	@NotNull
	@ApiModelProperty(notes = "Voucher configurationId to update data",required=true)
	private Long id;
	
	@NotNull
	@NotBlank
	@ApiModelProperty(notes = "Name of the voucher configuration",required=true)
	private String name;
	
	@NotNull
	@Min(1)
	@ApiModelProperty(notes = "Number of vouchers",required=true)
	private Long noOfVoucher;
		
	@NotNull
	@NotBlank
	@ApiModelProperty(notes = "Name of the plan",required=true)
	private String planName;
	
	@NotNull
	@Min(1)
	@ApiModelProperty(notes = "Length of the voucher",required=true)
	private Integer voucherCodeLength;
	
	@ApiModelProperty(notes = "Pre value of the voucher configuration",required=true)
	private String prefix;
	
	@ApiModelProperty(notes = "Post value of the voucher configuration",required=true)
	private String suffix;

	@ApiModelProperty(notes = "Status of the voucher configuration",required=true)
	private String status;
	
	@NotNull
	@ApiModelProperty(notes = "Voucher types",required=true)
	private List<FieldType> voucherCodeFormat;

	@ApiModelProperty(notes = "plan id" , required = false)
	private Integer planId;

	@ApiModelProperty(notes = "plan Validity" , required = true)
	private Long validity;
	@NotNull
	@ApiModelProperty(notes = "link_type" , required = true)
	private VoucherLinkType linkType;

	@ApiModelProperty(notes = "voucher_amount" , required = true)
	private Double voucherAmount;

	public VoucherLinkType getLinkType() {
		return linkType;
	}

	public void setLinkType(VoucherLinkType linkType) {
		this.linkType = linkType;
	}

	public Double getVoucherAmount() {
		return voucherAmount;
	}

	public void setVoucherAmount(Double voucherAmount) {
		this.voucherAmount = voucherAmount;
	}

	public Long getValidity() {
		return validity;
	}

	public void setValidity(Long validity) {
		this.validity = validity;
	}

	public Integer getPlanId() {
		return planId;
	}

	public void setPlanId(Integer planId) {
		this.planId = planId;
	}


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getNoOfVoucher() {
		return noOfVoucher;
	}
	public void setNoOfVoucher(Long noOfVoucher) {
		this.noOfVoucher = noOfVoucher;
	}
	public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	public Integer getVoucherCodeLength() {
		return voucherCodeLength;
	}
	public void setVoucherCodeLength(Integer voucherCodeLength) {
		this.voucherCodeLength = voucherCodeLength;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<FieldType> getVoucherCodeFormat() {
		return voucherCodeFormat;
	}
	public void setVoucherCodeFormat(List<FieldType> voucherCodeFormat) {
		this.voucherCodeFormat = voucherCodeFormat;
	}
}
