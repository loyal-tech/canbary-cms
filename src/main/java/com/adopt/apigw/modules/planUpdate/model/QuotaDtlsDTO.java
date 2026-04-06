package com.adopt.apigw.modules.planUpdate.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuotaDtlsDTO extends Auditable implements IBaseDto {
    private Long quotaDtlsId;
    private Long customersId;
    private Integer postpaidPlanId;// = new ArrayList<>();
    private String quotaType;
    private Double totalQuota;
    private Double usedQuota;
    private LocalDateTime createdOn;
    private LocalDateTime lastModifiedOn;
    private String quotaUnit;
    private String timeTotalQuota;
    private String timeQuotaUsed;
    private String timeQuotaUnit;
    private Boolean isDelete;
    private Integer mvnoId;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return quotaDtlsId;
    }

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoId;
	}
}
