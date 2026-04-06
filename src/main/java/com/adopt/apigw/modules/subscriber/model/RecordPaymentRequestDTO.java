package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.time.LocalDate;

import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateAbstarctDTO;

@Data
public class RecordPaymentRequestDTO extends UpdateAbstarctDTO {
    String paymentMode;
    LocalDate paymentDate;
    Double paymentAmount;
    String referenceNo;
    String chequeNo;
    String bankName;
    LocalDate chequeDate;
    String branch;
    Boolean isTdsDeducted;
    Double tdsAmount;
    String remarks;
    Integer custId;
    Integer credit_doc_id;
    
    private String type;

    private Integer mvnoId;

    public RecordPaymentRequestDTO() {
    }

    public RecordPaymentRequestDTO(String paymentMode, LocalDate paymentDate, Double paymentAmount, Boolean isTdsDeducted, String remarks, Integer custId) {
        this.paymentMode = paymentMode;
        this.paymentDate = paymentDate;
        this.paymentAmount = paymentAmount;
        this.isTdsDeducted = isTdsDeducted;
        this.remarks = remarks;
        this.custId = custId;
    }
}
