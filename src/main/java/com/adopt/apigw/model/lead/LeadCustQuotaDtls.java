package com.adopt.apigw.model.lead;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLTLEADCUSTQUOTADTLS")
public class LeadCustQuotaDtls {

	@Id
	@Column(name = "cust_quota_dtls_id", nullable = false)
	private Integer id;

	@Column(name = "plan_id")
    private Integer planId;
	
	@Column(name = "quota_type")
    private String quotaType;

	@Column(name = "total_quota")
    private Double totalQuota;

	@Column(name = "used_quota")
    private Double usedQuota;

	@Column(name = "quota_unit")
    private String quotaUnit;

	@Column(name = "time_total_quota")
    private Double timeTotalQuota;

	@Column(name = "time_quota_used")
    private Double timeQuotaUsed;
	
	@Column(name = "time_quota_unit")
    private String timeQuotaUnit;

	@Column(name = "is_delete")
    private Boolean isDelete;

	@Column(name = "total_quota_kb")
    private Double totalQuotaKB;

	@Column(name = "used_quota_kb")
    private Double usedQuotaKB;

	@Column(name = "time_used_quota_sec")
    private Double timeUsedQuotaSec;

	@Column(name = "time_total_quota_sec")
    private Double timeTotalQuotaSec;

//	@Column(name = "did_total_unit")
//    private Double didtotalquota;

    private Double didusedquota;

    private Double intercomtotalquota;

    private Double intercomusedquota;

	@Column(name = "did_quota_unit")
    private String didQuotaUnit;

	@Column(name = "intercom_quota_unit")
    private String intercomQuotaUnit;
    
	@Column(name = "plan_name")
    private String planName;

    @JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lead_master_id")
	private LeadMaster leadMaster;
    
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cust_plan_mapping_id")
    private LeadCustPlanMappping custPlanMappping;
    
    public LeadCustQuotaDtls(LeadCustQuotaDtlsPojo leadCustQuotaDtlsPojo) {
    	this.id = leadCustQuotaDtlsPojo.getId();
    	this.planId = leadCustQuotaDtlsPojo.getPlanId();
    	this.quotaType = leadCustQuotaDtlsPojo.getQuotaType();
    	//this.totalQuota = leadCustQuotaDtlsPojo.getDidtotalquota();
    	this.usedQuota = leadCustQuotaDtlsPojo.getDidusedquota();
    	this.quotaUnit = leadCustQuotaDtlsPojo.getDidQuotaUnit();
    	this.timeTotalQuota = leadCustQuotaDtlsPojo.getTimeTotalQuota();
    	this.timeQuotaUsed = leadCustQuotaDtlsPojo.getTimeQuotaUsed();
    	this.timeQuotaUnit = leadCustQuotaDtlsPojo.getTimeQuotaUnit();
    	this.isDelete = leadCustQuotaDtlsPojo.getIsDelete();
    	this.totalQuotaKB = leadCustQuotaDtlsPojo.getTotalQuotaKB();
    	this.usedQuotaKB = leadCustQuotaDtlsPojo.getUsedQuotaKB();
    	this.timeUsedQuotaSec = leadCustQuotaDtlsPojo.getTimeUsedQuotaSec();
    	this.timeTotalQuotaSec = leadCustQuotaDtlsPojo.getTimeTotalQuotaSec();
    //	this.didtotalquota = leadCustQuotaDtlsPojo.getDidtotalquota();
    	this.didusedquota = leadCustQuotaDtlsPojo.getDidusedquota();
    	this.intercomtotalquota = leadCustQuotaDtlsPojo.getIntercomtotalquota();
    	this.intercomusedquota = leadCustQuotaDtlsPojo.getIntercomusedquota();
    	this.didQuotaUnit = leadCustQuotaDtlsPojo.getDidQuotaUnit();
    	this.intercomQuotaUnit = leadCustQuotaDtlsPojo.getIntercomQuotaUnit();
    	this.planName = leadCustQuotaDtlsPojo.getPlanName();
    	if(leadCustQuotaDtlsPojo.getLeadMasterId() != null)
    		this.leadMaster = new LeadMaster(leadCustQuotaDtlsPojo.getLeadMasterId());
    	if(leadCustQuotaDtlsPojo.getCustPlanMapppingId() != null)
    		this.custPlanMappping = new LeadCustPlanMappping(leadCustQuotaDtlsPojo.getCustPlanMapppingId());    	
    }
}
