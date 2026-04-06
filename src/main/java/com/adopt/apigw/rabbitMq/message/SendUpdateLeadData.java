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
public class SendUpdateLeadData {

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private LeadMgmtWfDTO leadFlowApproverUpdatedData;

//    public SendUpdateLeadData(LeadMgmtWfDTO leadMgmtWfDTO) {
//        this.messageDate = new Date();
//        this.messageId = UUID.randomUUID().toString();
//        this.message = "Getting suitable staff for lead management approval";
//        leadFlowApproverUpdatedData.put("id",leadMgmtWfDTO.getId());
//        leadFlowApproverUpdatedData.put("staffid",leadMgmtWfDTO.getStaffId().longValue());
//        leadFlowApproverUpdatedData.put("buid",leadMgmtWfDTO.getBuId());
//        leadFlowApproverUpdatedData.put("mvnoid",leadMgmtWfDTO.getMvnoId());
//        leadFlowApproverUpdatedData.put("status",leadMgmtWfDTO.getStatus());
//        leadFlowApproverUpdatedData.put("serviceareaid",leadMgmtWfDTO.getServiceareaid());
//        leadFlowApproverUpdatedData.put("nextapprover",leadMgmtWfDTO.getNextLeadApprover());
//    }

}
