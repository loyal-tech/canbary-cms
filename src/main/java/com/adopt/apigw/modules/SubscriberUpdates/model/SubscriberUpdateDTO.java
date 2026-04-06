package com.adopt.apigw.modules.SubscriberUpdates.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class SubscriberUpdateDTO extends Auditable implements IBaseDto {

    private Long id;
    private String operation;
    private String oldval;
    private String newval;
    private String remarks;
    private String entityName;
    @JsonBackReference
    private Customers customers;
    private Boolean isDeleted = false;
    private Integer mvnoId;

    public SubscriberUpdateDTO() {
    }

    public SubscriberUpdateDTO(String oldValue, String newValue, String operation, String remarks, Customers customers, String entityName) {
        this.oldval = oldValue;
        this.newval = newValue;
        this.operation = operation;
        this.remarks = remarks;
        this.customers = customers;
        this.entityName = entityName;
    }


    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoId;
	}
}
