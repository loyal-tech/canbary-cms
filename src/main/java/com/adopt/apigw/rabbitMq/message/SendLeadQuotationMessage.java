package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendLeadQuotationMessage {
    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    LeadQuotationWfDTO leadQuotationWfDTO;

    public SendLeadQuotationMessage(LeadQuotationWfDTO leadQuotationWfDTO) {
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Getting suitable staff for lead quotation approval";
        this.leadQuotationWfDTO = leadQuotationWfDTO;
    }
}
