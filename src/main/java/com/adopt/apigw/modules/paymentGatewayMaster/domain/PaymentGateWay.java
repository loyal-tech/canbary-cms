package com.adopt.apigw.modules.paymentGatewayMaster.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbl_payment_gateway")
@EntityListeners(AuditableListener.class)
public class PaymentGateWay extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pgid")
    private Long id;
    private String returnurl;
    private String pgurl;
    private String name;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean partnerenableflag = false;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean userenableflag = false;
    private String prefix;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    private String status;
    
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
