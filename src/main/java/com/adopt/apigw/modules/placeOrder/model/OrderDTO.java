package com.adopt.apigw.modules.placeOrder.model;

import lombok.Data;

import javax.persistence.Column;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;

@Data
public class OrderDTO extends Auditable implements IBaseDto {
    private Long id;
    private Long entityid;
    private Long custId;
    private Long partnerId;
    private Long pgid;
    private String ordertype;
    private Double finalamount;
    private Double basicamount;
    private Double taxamount;
    private String orderdesc;
    private Boolean is_balance_used;
    private Double balanced_used;
    private Long ledger_details_id;
    private Boolean is_settled;
    private String purchase_type;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    private Integer mvnoId;

    @Override
    public Long getIdentityKey() {
        return null;
    }

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return null;
	}
}
