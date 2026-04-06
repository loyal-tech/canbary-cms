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


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "VoucherBatch Update",description = "This is data transfer object for VoucherBatch which is used to update VoucherBatch data")
public class UpdateVoucherBatchDto {

	@ApiModelProperty(notes = "The database generated voucherBatchId")
	private Long voucherBatchId;

	@ApiModelProperty(notes = "This is VoucherBatch name")
	private String batchName;

	@ApiModelProperty(notes = "This is Voucher Profile id")
	private Long voucherProfileId;

	@ApiModelProperty(notes = "This is VoucherBatch planId")
	private Long planId;

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

	public UpdateVoucherBatchDto(BSSVoucherBatch voucherBatch, Long mvnoId) {
		this.batchName = voucherBatch.getBatchName();
		this.voucherProfileId = voucherBatch.getVoucherConfiguration().getId();
		Long planId = (voucherBatch.getPlan() != null && voucherBatch.getPlan().getId() != null)
				? Long.valueOf(voucherBatch.getPlan().getId())
				: null;
		this.planId = planId;
		if(voucherBatch.getReseller() != null)
			this.resellerId = voucherBatch.getReseller().getResellerId();
		this.createDate = voucherBatch.getCreateDate();
		this.voucherQuantity = voucherBatch.getVoucherQuantity();
		this.price = voucherBatch.getPrice();
		this.voucherBatchId=voucherBatch.getVoucherBatchId();
		this.mvnoId=mvnoId;
	}

}
