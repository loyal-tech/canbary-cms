package com.adopt.apigw.modules.VoucherBatch.module;

import com.adopt.apigw.modules.VoucherBatch.domain.BSSVoucherBatch;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ApiModel(value = "VoucherBatch", description = "This is data transfer object for VoucherBatch which is used to create new VoucherBatch")
public class VoucherBatchDto {

	@ApiModelProperty(notes = "This is VoucherBatch id", hidden = true)
	private Long voucherBatchId;

	@ApiModelProperty(notes = "This is VoucherBatch name")
	private String batchName;

	@ApiModelProperty(notes = "This is Voucher Profile id")
	private Long voucherProfileId;

	@ApiModelProperty(notes = "This is VoucherBatch planId")
	private Integer planId;

	@ApiModelProperty(notes = "This is VoucherBatch resellerId")
	private Long resellerId;

	@ApiModelProperty(notes = "This is VoucherBatch create date")
	private LocalDateTime createDate;

	@ApiModelProperty(notes = "This is VoucherBatch Quantity")
	private Integer voucherQuantity;

	@ApiModelProperty(notes = "This is Voucher price")
	private Double price;

	@ApiModelProperty(hidden = true)
    private Long mvnoId;

	@ApiModelProperty(hidden = true)
	private Long buId;

	@ApiModelProperty(hidden = true)
	private Integer createdByStaffId;

	public VoucherBatchDto(BSSVoucherBatch voucherBatch) {
		this.voucherProfileId = voucherBatch.getVoucherConfiguration().getId();
		this.batchName = voucherBatch.getBatchName();
		if(Objects.nonNull(voucherBatch.getPlan())) {
			this.planId = voucherBatch.getPlan().getId();
		}
		if(voucherBatch.getReseller() != null)
			this.resellerId = voucherBatch.getReseller().getResellerId();
		this.createDate = voucherBatch.getCreateDate();
		this.voucherQuantity = voucherBatch.getVoucherQuantity();
		this.price = voucherBatch.getPrice();
		this.voucherBatchId=voucherBatch.getVoucherBatchId();
		this.mvnoId=voucherBatch.getMvnoId();
		//this.buId = voucherBatch.get
	}

}
