package com.adopt.apigw.pojo;

import com.adopt.apigw.model.common.CustMilestoneDetails;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.rabbitMq.message.CustMilestoneDetailsMessage;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class CustMilestoneDetailsPojo {

    private Long id;
    private String milestoneName;

    private Double amount;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    private Long customerId;
    private Long leadId;

    public CustMilestoneDetailsPojo(){}

    public CustMilestoneDetailsPojo(CustMilestoneDetails milestoneDetails){
        if(milestoneDetails.getId() != null)
            this.id = milestoneDetails.getId();
        this.milestoneName = milestoneDetails.getMilestoneName();
        this.amount = milestoneDetails.getAmount();
        this.dueDate = milestoneDetails.getDueDate();
        if(milestoneDetails.getCustomers() != null && milestoneDetails.getCustomers().getId() != null)
            this.customerId = Long.parseLong(String.valueOf(milestoneDetails.getCustomers().getId()));
        if(milestoneDetails.getLeadMaster() != null && milestoneDetails.getLeadMaster().getId() != null)
            this.leadId = milestoneDetails.getLeadMaster().getId();
    }

    public CustMilestoneDetailsPojo(CustMilestoneDetailsMessage message){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatterForDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if(message.getId()!= null)
            this.id = message.getId();
        if(message.getCustomerId()!= null)
            this.customerId = message.getCustomerId();
        if(message.getLeadId()!= null)
            this.leadId = message.getLeadId();
        this.milestoneName = message.getMilestoneName();
        this.dueDate = LocalDate.parse(message.getDueDate(), formatter);
        this.amount = message.getAmount();
    }
}
