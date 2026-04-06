package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.postpaid.CustSpecialPlanMappping;
import com.adopt.apigw.model.postpaid.CustSpecialPlanRelMappping;
import com.adopt.apigw.model.postpaid.PlanGroup;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustSpecialPlanMapppingPojo {

 	private Integer id;
	
    private Integer specialPlanId;
    
    private String specialPlanName;
    
    private Integer normalPlanId;
    
    private String normalPlanName;

	private Integer specialPlanGroupId;

	private String specialPlanGroupName;

	private Integer normalPlanGroupId;

	private String normalPlanGroupName;

    private Integer customerId;

	private String customerName;

    private String customerPlanName;
	
    private String service;

	private Integer mvnoId;

	private Long buId;

	private Integer leadCustId;

	private String leadCustPlanName;

	private String leadCustName;
	private PostpaidPlan specialPlan;

	private PostpaidPlan normalPlan;

	private PlanGroup specialPlanGroup;
	private PlanGroup normalPlanGroup;



	public CustSpecialPlanMapppingPojo(CustSpecialPlanMappping custSpecialPlanMappping) {
		super();
		this.id = custSpecialPlanMappping.getId();
		if(custSpecialPlanMappping.getSpecialPlan() != null) {
			this.specialPlanId = custSpecialPlanMappping.getSpecialPlan().getId();
			this.specialPlanName = custSpecialPlanMappping.getSpecialPlan().getName();
		}
		if(custSpecialPlanMappping.getNormalPlan() != null) {
			this.normalPlanId = custSpecialPlanMappping.getNormalPlan().getId();
			this.normalPlanName = custSpecialPlanMappping.getNormalPlan().getName();
		}
		if(custSpecialPlanMappping.getSpecialPlanGroup() != null) {
			this.specialPlanGroupId = custSpecialPlanMappping.getSpecialPlanGroup().getPlanGroupId();
			this.specialPlanGroupName = custSpecialPlanMappping.getSpecialPlanGroup().getPlanGroupName();
		}
		if(custSpecialPlanMappping.getNormalPlanGroup() != null) {
			this.normalPlanGroupId = custSpecialPlanMappping.getNormalPlanGroup().getPlanGroupId();
			this.normalPlanGroupName = custSpecialPlanMappping.getNormalPlanGroup().getPlanGroupName();
		}
		if(custSpecialPlanMappping.getCustomer() != null) {
			this.customerId = custSpecialPlanMappping.getCustomer().getId();
			this.customerName = custSpecialPlanMappping.getCustomer().getFirstname() + " " +custSpecialPlanMappping.getCustomer().getLastname();
			this.customerPlanName = custSpecialPlanMappping.getCustomer().getUsername();
		}
		if(custSpecialPlanMappping.getService() != null) 
			this.service = custSpecialPlanMappping.getService();
		if(custSpecialPlanMappping.getLeadMaster()!=null){
			this.leadCustId = Math.toIntExact(custSpecialPlanMappping.getLeadMaster().getId());
			this.leadCustPlanName = custSpecialPlanMappping.getLeadMaster().getServiceareaName();
			this.leadCustName = custSpecialPlanMappping.getLeadMaster().getFirstname() + " " +custSpecialPlanMappping.getLeadMaster().getLastname();
		}
		this.mvnoId = custSpecialPlanMappping.getMvnoId();

	}
    
    
     
}
