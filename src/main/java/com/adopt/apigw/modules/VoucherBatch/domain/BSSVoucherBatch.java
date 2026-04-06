package com.adopt.apigw.modules.VoucherBatch.domain;

import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.modules.Reseller.domain.Reseller;
import com.adopt.apigw.modules.Voucher.domain.Voucher;
import com.adopt.apigw.modules.VoucherBatch.module.UpdateVoucherBatchDto;
import com.adopt.apigw.modules.VoucherBatch.module.VoucherBatchDto;
import com.adopt.apigw.modules.VoucherConfiguration.domain.VoucherConfiguration;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "TBLMVOUCHERBATCH")
public class BSSVoucherBatch {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated voucherBatchId")
	@Column(name = "voucherbatchid", nullable = false)
	private Long voucherBatchId;

	@ApiModelProperty(notes = "This is Voucher Batch name")
	@Column(name = "batchname", nullable = false)
	private String batchName;

	@ManyToOne(optional = false)
		@JoinColumn(name = "voucher_profile_id")
	private VoucherConfiguration voucherConfiguration;

	@ManyToOne(optional = true)
	@JoinColumn(name = "planid", nullable = true)
	private PostpaidPlan plan;

	@ManyToOne(optional = false)
	@JoinColumn(name = "resellerid")
	private Reseller reseller;

	@ApiModelProperty(notes = "This is VoucherBatch create date")
	@Column(name = "createdate", nullable = false)
	private LocalDateTime createDate;

	@ApiModelProperty(notes = "This is VoucherBatch Quantity")
	@Column(name = "voucherquantity", nullable = false)
	private Integer voucherQuantity;

	@ApiModelProperty(notes = "This is Voucher price")
	@Column(name = "price", nullable = false)
	private Double price;

	@ApiModelProperty(notes = "This is mvno id", required = true)
	@Column(name = "mvnoid", nullable = false)
	private Long mvnoId;

	@OneToMany(mappedBy = "voucherBatch", cascade = {CascadeType.ALL})
	@JsonIgnore
	private List<Voucher> vouchers;

	@ApiModelProperty(notes = "add Expiry Date in each voucherBatch")
	@Column(name = "expirydate")
	private LocalDateTime expirydate;

	@ApiModelProperty(notes = "This is buid", required = true)
	@Column(name = "buid")
	private Long buId;

	@Column(name = "createdbystaffid")
	private Integer createdByStaffId;

	public BSSVoucherBatch(VoucherBatchDto voucherBatchDto, PostpaidPlan plan, Reseller reseller, VoucherConfiguration voucherConfiguration, Long mvnoId, Long buId) {
		this.setVoucherConfiguration(voucherConfiguration);
		this.setBatchName(voucherBatchDto.getBatchName());
		this.voucherQuantity = voucherBatchDto.getVoucherQuantity();
		this.createDate = voucherBatchDto.getCreateDate();
		this.price = voucherBatchDto.getPrice();
		this.mvnoId = mvnoId;
		this.buId = buId;
		this.setPlan(plan);
		if(reseller != null)
			this.setReseller(reseller);
	}

	public BSSVoucherBatch(UpdateVoucherBatchDto voucherBatchDto, PostpaidPlan plan, Reseller reseller, VoucherConfiguration voucherConfiguration, Long mvnoId) {
		this.voucherBatchId=voucherBatchDto.getVoucherBatchId();
		this.setBatchName(voucherBatchDto.getBatchName());
		this.setVoucherConfiguration(voucherConfiguration);
		this.voucherQuantity = voucherBatchDto.getVoucherQuantity();
		this.createDate = voucherBatchDto.getCreateDate();
		this.price = voucherBatchDto.getPrice();
		this.mvnoId = mvnoId;
		this.setPlan(plan);
		this.setReseller(reseller);
	}

}
