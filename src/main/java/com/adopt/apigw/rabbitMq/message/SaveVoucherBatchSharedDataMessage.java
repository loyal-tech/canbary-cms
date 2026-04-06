package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.VoucherBatch.module.VoucherBatchDto;
import lombok.Data;

@Data
public class SaveVoucherBatchSharedDataMessage {

    private Long voucherBatchId;

    private String batchName;

    private Long voucherProfileId;

    private Integer planId;

    private Long resellerId;

    private String createDate;

    private Integer voucherQuantity;

    private Double price;

    private Long mvnoId;

    private Long buId;

    private Integer partnerId;

    public SaveVoucherBatchSharedDataMessage(VoucherBatchDto voucherBatch,Integer partnerId) {
        this.voucherProfileId = voucherBatch.getVoucherProfileId();
        this.batchName = voucherBatch.getBatchName();
        this.planId = voucherBatch.getPlanId();
        this.resellerId = voucherBatch.getResellerId();
        //this.createDate = voucherBatch.getCreateDate().toString();
        this.voucherQuantity = voucherBatch.getVoucherQuantity();
        this.price = voucherBatch.getPrice();
        this.voucherBatchId=voucherBatch.getVoucherBatchId();
        this.mvnoId=voucherBatch.getMvnoId();
        this.buId=voucherBatch.getBuId();
        this.partnerId=partnerId;
    }
}
