package com.adopt.apigw.rabbitMq.message;


import com.adopt.apigw.pojo.api.LeadMgmtWfDTO;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class StaffForLeadMsg {

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private Map<String, Integer> leadMgmtData;

    private static final String ADOPT_API_GATEWAY = "Adopt Api Gateway";

    public StaffForLeadMsg(Integer staffId, Integer leadId) {
        Map<String, Integer> map = new HashMap<>();
        map.put("staffId",staffId);
        map.put("leadId",leadId);

        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "New staff created from Api Gateway";
        this.leadMgmtData = map;
        this.sourceName = ADOPT_API_GATEWAY;

    }


}
