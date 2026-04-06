package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.model.lead.LeadMasterPojo;
import com.adopt.apigw.pojo.CustMilestoneDetailsPojo;
import com.adopt.apigw.pojo.QuickInvoiceCreationPojo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuickInvoicePojoMessage {

    public List<CustMilestoneDetailsMessage> custMileStoneDetailsList = new ArrayList<>();

    public QuickInvoicePojoMessage(){}

    public QuickInvoicePojoMessage(QuickInvoiceCreationPojo pojo){

        if(pojo.getCustMileStoneDetailsList() != null && pojo.getCustMileStoneDetailsList().size()>0) {
            pojo.getCustMileStoneDetailsList().forEach(item ->{
                custMileStoneDetailsList.add(new CustMilestoneDetailsMessage(item));
            });
        }

    }
}
