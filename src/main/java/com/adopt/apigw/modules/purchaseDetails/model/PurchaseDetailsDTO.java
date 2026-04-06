package com.adopt.apigw.modules.purchaseDetails.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
public class PurchaseDetailsDTO extends Auditable implements IBaseDto {

    private Long id;
    private Long orderid;
    private Integer custid;
    private Integer partnerid;
    private Long pgid;
    private Double amount = 0.0;
    private String paymentstatus;
    private String transid;
    private String pgtransid;
    private String pgResStatus;
    private String purchaseStatus;
    private LocalDateTime purchasedate;
    private LocalDateTime transResDate;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    private String custName;
    private String pgName;
    private String partnerName;
    private String purchaseDateString;
    private String transResDateString;
    private Integer mvnoId;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return null;
	}
}
