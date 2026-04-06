package com.adopt.apigw.model.lead;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadCustQuotaDtlsPojo {

	private Integer id;

    private Integer planId;

    private String quotaType;

    private Double totalQuota;

    private Double usedQuota;

    private String quotaUnit;

    private Double timeTotalQuota;

    private Double timeQuotaUsed;

    private String timeQuotaUnit;

    private Boolean isDelete;

    private Double totalQuotaKB;

    private Double usedQuotaKB;

    private Double timeUsedQuotaSec;

    private Double timeTotalQuotaSec;

    private Double didtotalquota;

    private Double didusedquota;

    private Double intercomtotalquota;

    private Double intercomusedquota;

    private String didQuotaUnit;

    private String intercomQuotaUnit;
    
    private String planName;

	private Long leadMasterId;
    
    private Integer custPlanMapppingId;
    
    public LeadCustQuotaDtlsPojo(Integer id) {
		this.id = id;
	}
    
    public LeadCustQuotaDtlsPojo(LeadCustQuotaDtls leadCustQuotaDtls) {
    	this.id = leadCustQuotaDtls.getId();
    	this.planId = leadCustQuotaDtls.getPlanId();
    	this.quotaType = leadCustQuotaDtls.getQuotaType();
//    	this.totalQuota = leadCustQuotaDtls.getDidtotalquota();
    	this.usedQuota = leadCustQuotaDtls.getDidusedquota();
    	this.quotaUnit = leadCustQuotaDtls.getDidQuotaUnit();
    	this.timeTotalQuota = leadCustQuotaDtls.getTimeTotalQuota();
    	this.timeQuotaUsed = leadCustQuotaDtls.getTimeQuotaUsed();
    	this.timeQuotaUnit = leadCustQuotaDtls.getTimeQuotaUnit();
    	this.isDelete = leadCustQuotaDtls.getIsDelete();
    	this.totalQuotaKB = leadCustQuotaDtls.getTotalQuotaKB();
    	this.usedQuotaKB = leadCustQuotaDtls.getUsedQuotaKB();
    	this.timeUsedQuotaSec = leadCustQuotaDtls.getTimeUsedQuotaSec();
    	this.timeTotalQuotaSec = leadCustQuotaDtls.getTimeTotalQuotaSec();
    	//this.didtotalquota = leadCustQuotaDtls.getDidtotalquota();
    	this.didusedquota = leadCustQuotaDtls.getDidusedquota();
    	this.intercomtotalquota = leadCustQuotaDtls.getIntercomtotalquota();
    	this.intercomusedquota = leadCustQuotaDtls.getIntercomusedquota();
    	this.didQuotaUnit = leadCustQuotaDtls.getDidQuotaUnit();
    	this.intercomQuotaUnit = leadCustQuotaDtls.getIntercomQuotaUnit();
    	this.planName = leadCustQuotaDtls.getPlanName();
    	if(leadCustQuotaDtls.getLeadMaster() != null)
    		this.leadMasterId = leadCustQuotaDtls.getLeadMaster().getId();
    	if(leadCustQuotaDtls.getCustPlanMappping() != null)
    		this.custPlanMapppingId = leadCustQuotaDtls.getCustPlanMappping().getId();
    }

}
