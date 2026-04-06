package com.adopt.apigw.pojo.customer.service;

import lombok.Data;

import java.util.List;

@Data
public class TerminateServiceInBulkPojo {

   private List<TerminateServicePojo> terminateService;
   private Integer customerId;
   private String reason;
}
