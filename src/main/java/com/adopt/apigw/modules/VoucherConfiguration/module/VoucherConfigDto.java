package com.adopt.apigw.modules.VoucherConfiguration.module;

import com.adopt.apigw.model.common.FieldType;
import com.adopt.apigw.model.radius.VoucherLinkType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
@Data
@ApiModel(value = "Voucher Config",description = "This is data transfer object for voucher config which is used to create new voucher configuration")
public class VoucherConfigDto {
		
	@NotNull
	@NotBlank
	@ApiModelProperty(notes = "Name of the voucher configuration",required=true)
	private String name;
	
	@NotNull
	@Min(1)
	@ApiModelProperty(notes = "Number of vouchers",required=true)
	private Long noOfVoucher;
		

	@ApiModelProperty(notes = "Name of the plan",required=false)
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

	@ApiModelProperty(notes = "buid" , required = false)
	private Long buId;

	@ApiModelProperty(notes = "createdbystaffid" , required = false)
	private Integer createdByStaffId;

	@NotNull
	@ApiModelProperty(notes = "link_type" , required = true)
	private VoucherLinkType linkType;
	
	@ApiModelProperty(notes = "voucher_amount" , required = true)
	private Double voucherAmount;
	public Long getBuId() {
		return buId;
	}

	public void setBuId(Long buId) {
		this.buId = buId;
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

	public List<FieldType> getVoucherCodeFormat() {
		return voucherCodeFormat;
	}

	public void setVoucherCodeFormat(List<FieldType> voucherCodeFormat) {
		this.voucherCodeFormat = voucherCodeFormat;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
