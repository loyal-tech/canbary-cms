package com.adopt.apigw.modules.VoucherConfiguration.domain;

import com.adopt.apigw.converter.FieldTypeConverter;
import com.adopt.apigw.model.common.FieldType;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.model.radius.VoucherLinkType;
import com.adopt.apigw.modules.Voucher.module.Auditable;
import com.adopt.apigw.modules.Voucher.module.ValidateCrudTransactionData;
import com.adopt.apigw.modules.VoucherConfiguration.module.VoucherConfigDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "TBLMVOUCHERPROFILES")
@Data
public class VoucherConfiguration extends Auditable<Long>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column (name="NAME", nullable = false, length=250)
	private String name;
	
	@Column (name="NO_OF_VOUCHERS")
	private Long noOfVoucher;
		
	@ManyToOne(optional = true)
	@JoinColumn(name="planid", nullable = true)
	private PostpaidPlan plan;
	
	@Column (name="vouchercodelength")
	private Integer voucherCodeLength;
	
	@Column (name="prefix", length=250)
	private String prefix;
	
	@Column (name="STATUS", length=10)
	private String status;
	
	@Column (name="vouchercodeformat")
	@Convert(converter = FieldTypeConverter.class)
	private List<FieldType> voucherCodeFormat;

	@Column (name="suffix", length=250)
	private String suffix;

	@DiffIgnore
	@ApiModelProperty(notes = "This is mvnoid",required=true)
	@Column (name="mvnoid", nullable = false)
	private Long mvnoId;

	@ApiModelProperty(notes = "This is validity",required=true)
	@Column (name="validity", nullable = false)
	private Long validity;

	@DiffIgnore
	@ApiModelProperty(notes = "This is buid")
	@Column (name="buid")
	private Long buId;

	@DiffIgnore
	@Column(name = "createdbystaffid")
	private Integer createdByStaffId;

	@Column(name="mvnoName")
	private String mvnoName;
	@Column(name = "link_type",nullable = false )
	@Enumerated(EnumType.STRING)
	private VoucherLinkType linkType;
	@Column(name = "voucher_amount")
	private Double voucherAmount;


	public Long getValidity() {
		return validity;
	}

	public void setValidity(Long validity) {
		this.validity = validity;
	}

	public Long getMvnoId() {
		return mvnoId;
	}

	public void setMvnoId(Long mvnoId) {
		this.mvnoId = mvnoId;
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

	public PostpaidPlan getPlan() {
		return plan;
	}

	public void setPlan(PostpaidPlan plan) {
		this.plan = plan;
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

	@Override
	public String toString() {
		return "VoucherConfiguration [id=" + id + ", name=" + name + ", noOfVoucher=" + noOfVoucher + ", plan=" + plan
				+ ", voucherCodeLength=" + voucherCodeLength + ", prefix=" + prefix + ", status=" + status + ", voucherCodeFormat="
				+ voucherCodeFormat + ", suffix=" + suffix +"]";
	}

	public VoucherConfiguration(Long id, String name, Long noOfVoucher, PostpaidPlan plan, Integer voucherCodeLength, String prefix,
                                String suffix, List<FieldType> voucherCodeFormat, String status) {
		super();
		this.id = id;
		this.name = name;
		this.noOfVoucher = noOfVoucher;
		this.plan = plan;
		this.voucherCodeLength = voucherCodeLength;
		this.prefix = prefix;
		this.suffix = suffix;
		this.voucherCodeFormat = voucherCodeFormat;
		this.status = status;
	}

	
	public VoucherConfiguration() {
		super();
	}

	public VoucherConfiguration(VoucherConfigDto configDto, PostpaidPlan plan, Long mvnoId)
	{
		this.name = configDto.getName();
		if(ValidateCrudTransactionData.validateStringTypeFieldValue(configDto.getSuffix()))
		{
			this.suffix = configDto.getSuffix();
		}
		if(ValidateCrudTransactionData.validateStringTypeFieldValue(configDto.getPrefix()))
		{
			this.prefix = configDto.getPrefix();
		}
		this.linkType=configDto.getLinkType();
		this.voucherAmount=configDto.getVoucherAmount();
		this.voucherCodeLength = configDto.getVoucherCodeLength();
		this.noOfVoucher = configDto.getNoOfVoucher();
		this.voucherCodeFormat = configDto.getVoucherCodeFormat();
		this.status = configDto.getStatus();
		this.plan = plan;
		this.mvnoId = mvnoId;
		this.validity = configDto.getValidity();
		this.buId = configDto.getBuId();
	}
	
	public VoucherConfiguration(UpdateVoucherConfigDto configDto, PostpaidPlan plan, Long mvnoId)
	{
		this.id=configDto.getId();
		this.name = configDto.getName();
		if(ValidateCrudTransactionData.validateStringTypeFieldValue(configDto.getSuffix()))
		{
			this.suffix = configDto.getSuffix();
		}
		if(ValidateCrudTransactionData.validateStringTypeFieldValue(configDto.getPrefix()))
		{
			this.prefix = configDto.getPrefix();
		}
		this.voucherCodeLength = configDto.getVoucherCodeLength();
		this.noOfVoucher = configDto.getNoOfVoucher();
		this.voucherCodeFormat = configDto.getVoucherCodeFormat();
		this.status = configDto.getStatus();
		this.plan = plan;
		this.mvnoId = mvnoId;
		this.linkType=configDto.getLinkType();
		this.voucherAmount= configDto.getVoucherAmount();
		if(configDto.getValidity() != null) {
			this.validity = configDto.getValidity();
		}
	}

	public VoucherConfiguration(VoucherConfiguration voucherConfiguration) {
		this.id = voucherConfiguration.id;
		this.name = voucherConfiguration.name;
		this.noOfVoucher = voucherConfiguration.noOfVoucher;
		this.plan = voucherConfiguration.plan;
		this.voucherCodeLength = voucherConfiguration.voucherCodeLength;
		this.prefix = voucherConfiguration.prefix;
		this.status = voucherConfiguration.status;
		this.voucherCodeFormat = voucherConfiguration.voucherCodeFormat;
		this.suffix = voucherConfiguration.suffix;
		this.mvnoId = voucherConfiguration.mvnoId;
		this.validity = voucherConfiguration.validity;
		this.buId = voucherConfiguration.buId;
	}
}
