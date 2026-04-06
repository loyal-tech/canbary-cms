package com.adopt.apigw.modules.planUpdate.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerPackageDTO extends Auditable<Integer> implements IBaseDto {
    private Long custPackageId;
    private Long customersId;
    private Long planId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate expiryDate;
    private String status;
    private Long qospolicyId;
    private String uploadqos;
    private String downloadqos;
    private String uploadts;
    private String downloadts;
    private Boolean isDelete;

    private Boolean isinvoicestop = false;

    private Boolean istrialplan = false;

    private Integer mvnoId;
    private Integer discount;
    private boolean isInvoiceToOrg=false;
    private String billTo="CUSTOMER";
    private Integer nextApprover;
    private Integer nextStaff;
    private String staffapproverstatus;
    private String promise_to_pay_remarks;
    private Long promisetopay_renew_count;
    private Double isTrialValidityDays;
    private Integer trialPlanValidityCount;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return custPackageId;
    }

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoId;
	}
}
