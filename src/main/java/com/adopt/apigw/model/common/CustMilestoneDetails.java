package com.adopt.apigw.model.common;

import com.adopt.apigw.model.lead.LeadMaster;
import com.adopt.apigw.pojo.CustMilestoneDetailsPojo;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "tblcust_milestone_details")
public class CustMilestoneDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "id")
    private Long id;

    @Column(name = "milestone_name")
    private String milestoneName;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "due_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customers customers;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lead_id")
    private LeadMaster leadMaster;

    public CustMilestoneDetails(){}

    public CustMilestoneDetails(CustMilestoneDetailsPojo pojo){
        if(pojo.getId()!= null)
            this.id = pojo.getId();
        this.amount = pojo.getAmount();
        this.milestoneName = pojo.getMilestoneName();
        this.dueDate = pojo.getDueDate();
    }
}
