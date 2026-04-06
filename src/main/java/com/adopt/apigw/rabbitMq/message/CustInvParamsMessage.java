package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustInvParamsDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustInvParamsMessage {

    private String messageId;
    private String message;
    private Date messageDate;
    private Boolean isUpdate;
    private Long custSerMapId;

    private List<CustInvParamsDto> custInvParams;

    public CustInvParamsMessage() {
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Customer Inventory Params Data";
    }
}
