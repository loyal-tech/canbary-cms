package com.adopt.apigw.modules.purchaseDetails.model;

import lombok.Data;

import java.sql.Date;

import com.adopt.apigw.core.dto.PaginationRequestDTO;

@Data
public class PurchaseHistoryReqDTO extends PaginationRequestDTO {

    private Boolean custFlag = false;
    private Boolean partnerFlag = false;
    private Long partnerId;
    private Long custId;
    private Date startDate;
    private Date endDate;
    private String orderType;
    private Long pgId;
    private String paymentStatus;
    private String purchaseStatus;
}
