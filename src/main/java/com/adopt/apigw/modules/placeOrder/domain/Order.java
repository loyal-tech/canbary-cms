package com.adopt.apigw.modules.placeOrder.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbl_order_details")
@EntityListeners(AuditableListener.class)
public class Order extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderid")
    private Long id;
    private Long custId;
    private Long partnerId;
    private Long entityid;
    private Long pgid;
    private String ordertype;
    private Double finalamount;
    private Double basicamount;
    private Double taxamount;
    private String orderdesc;
    private Double balanced_used;
    private Boolean is_balance_used;
    private Long ledger_details_id;
    private Boolean is_settled;
    private String purchase_type;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;

    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }
}
