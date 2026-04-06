package com.adopt.apigw.rabbitMq.message;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
public class ServiceTerminationMessage {
    private List<Integer> customerServiceId;
    private String customerStatus;
    private String remarks;
    private Boolean generatecn=true;


    public ServiceTerminationMessage(List<Integer>  customerServiceId, String customerStatus, String remarks,Boolean generatecn) {
        this.customerServiceId = customerServiceId;
        this.customerStatus = customerStatus;
        this.remarks = remarks;
        this.generatecn=generatecn;
    }
}
