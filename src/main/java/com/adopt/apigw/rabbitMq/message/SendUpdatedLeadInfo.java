package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.pojo.api.LeadMgmtWfDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendUpdatedLeadInfo {
    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private LeadMgmtWfDTO leadFlowApproverUpdatedData;

    public SendUpdatedLeadInfo(LeadMgmtWfDTO leadMgmtWfDTO) {
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Getting suitable staff for lead management approval";
        this.leadFlowApproverUpdatedData=leadMgmtWfDTO;

    }
}
