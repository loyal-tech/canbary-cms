package com.adopt.apigw.rabbitMq.message;

import lombok.Data;

@Data
public class AppproveOrgInvoiceMessage {
    private Integer debitdocId;
    private Boolean isApproveRequest;
}
