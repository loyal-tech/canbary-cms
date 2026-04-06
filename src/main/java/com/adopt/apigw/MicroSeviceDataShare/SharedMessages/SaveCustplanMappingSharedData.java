package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;


import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.pojo.api.CustPlanMapppingPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class SaveCustplanMappingSharedData {



    private Integer id;


    private Integer planId;



    private String service;


    private LocalDateTime startDate;


    private LocalDateTime endDate;


    private LocalDateTime expiryDate;


    private String status;


    private Integer custid;

    @Transient
    private PostpaidPlan postpaidPlan;

    private Long debitdocid;


    private Boolean isDelete;


    private Boolean isInvoiceToOrg;


    private String billTo;

    private List<CustPlanMapppingPojo> custPlanMapppingList = new ArrayList<>();
    CustPlanMapppingPojo custPlanMappping = new CustPlanMapppingPojo();

    public SaveCustplanMappingSharedData(List<CustPlanMappping> custPlanMapppingList){

        for (int i =0; i<custPlanMapppingList.size();i++){

            this.custPlanMappping.setId(custPlanMapppingList.get(i).getId());
            this.custPlanMappping.setCustid(custPlanMapppingList.get(i).getCustomer().getId());
            this.custPlanMappping.setPlanId(custPlanMapppingList.get(i).getPlanId());
            this.custPlanMappping.setBillTo(custPlanMapppingList.get(i).getBillTo());
            this.custPlanMappping.setIsInvoiceToOrg(custPlanMapppingList.get(i).getIsInvoiceToOrg());
            this.custPlanMappping.setService(custPlanMapppingList.get(i).getService());
//            if(custPlanMapppingList.get(i).getPlanGroup()!=null){
//                custPlanMappping.setPlangroupid(custPlanMapppingList.get(i).getPlanGroup().getPlanGroupId());
//            }
//            custPlanMappping.setPlanName(custPlanMapppingList.get(i).getPostpaidPlan().getName());
            this.custPlanMappping.setIsDelete(custPlanMapppingList.get(i).getIsDelete());
            this.custPlanMapppingList.add(this.custPlanMappping);
        }
    }
}
