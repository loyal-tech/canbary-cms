package com.adopt.apigw.modules.subscriber.model;

import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.time.LocalDate;

@Data
public class PaymentHistoryDTO extends Auditable<Integer> {
    private Integer id;
    private String paymode;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate paymentdate;
    private String paydetails1;
    private String paydetails2;
    private String paydetails3;
    private String paydetails4;
    private Double amount;
    private String status;
    private String paymentBy;
    private String remarks;
    private String receiptNo;
    private String xmldocument;
    private Integer custId;
    private Boolean isDelete = false;
    private String type;
    private Integer nextApprover;
    private String filename;
    private Integer nextTeamHierarchyMappingId;
    private String creditdocumentno;
    private String referenceno;
    private Double tdsamount;
    private Double abbsAmount;
    private Double adjustedAmount;
    private Double unsettledAmount;
    private String invoiceNumber;
    private Long bankManagement;
    private String bankName;
    private String onlinesource;
}
