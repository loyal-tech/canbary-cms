package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.pojo.api.LeadMgmtWfDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendSaveLeadData {

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private LeadMgmtWfDTO leadMgmtWfDTO;
}
