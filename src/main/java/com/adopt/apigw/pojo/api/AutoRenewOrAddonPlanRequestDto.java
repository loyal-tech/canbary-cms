package com.adopt.apigw.pojo.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutoRenewOrAddonPlanRequestDto {
     private String purchaseType;
     private String remarks;
     private LocalDateTime addonStartDate;
     private LocalDateTime addonEndDate;
     private Integer customerId;
     private Integer planId;
     private Boolean isAutoRefund;
     private  Integer paymentOwnerId;
     private Double discount;
     private  Boolean isParent;
     private Integer customerServiceMappingId;
     private Boolean isAutoPaymentRequired;
     private List<CreditDocumentPaymentPojo> creditDocumentPaymentPojoList;
}
