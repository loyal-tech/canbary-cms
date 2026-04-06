package com.adopt.apigw.modules.subscriber.model;


import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.modules.PurchaseOrder.Domain.PurchaseOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CustServiceMappingDTO {

    private Integer id;

    private Integer custId;

    private Long serviceId;

    private String serviceName;

    private String connectionNo;

    private String nickName;

    private String discountType;

    private Double discount;

    private LocalDate discountExpiryDate;

    private Double newDiscount=0.0;

    private String newDiscountType=null;

    private LocalDate newDiscountExpiryDate=null;

    private String remarks;

    private Integer nextTeamHierarchyMappingId;

    private Integer nextStaff;

    private String invoiceType;

    private String status;

    private String discountFlowInProcess;



    public CustServiceMappingDTO(CustomerServiceMapping mapping)
    {
        this.id=mapping.getId();
        this.custId= mapping.getCustId();
        this.serviceId=mapping.getServiceId();
        this.connectionNo=mapping.getConnectionNo();
        this.nickName=mapping.getNickName();

        this.discount=mapping.getDiscount();
        this.newDiscount=mapping.getNewDiscount();

        this.discountType=mapping.getDiscountType();
        this.newDiscountType=mapping.getNewDiscountType();

        this.discountExpiryDate=mapping.getDiscountExpiryDate();
        this.newDiscountExpiryDate=mapping.getNewDiscountExpiryDate();

        this.remarks=mapping.getRemarks();
        this.nextStaff=mapping.getNextStaff();
        this.nextTeamHierarchyMappingId=mapping.getNextTeamHierarchyMappingId();
        this.status=mapping.getStatus();
        this.invoiceType=mapping.getInvoiceType();

        if(mapping.getDiscountFlowInProcess()!=null){
            this.discountFlowInProcess = mapping.getDiscountFlowInProcess();
        }
    }
}
