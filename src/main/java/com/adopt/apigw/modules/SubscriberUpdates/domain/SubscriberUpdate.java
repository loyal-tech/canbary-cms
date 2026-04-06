package com.adopt.apigw.modules.SubscriberUpdates.domain;

import lombok.Data;

import javax.persistence.*;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.spring.security.AuditableListener;

@Data
@Entity
@Table(name = "tblsubscriberupdates")
@EntityListeners(AuditableListener.class)
public class SubscriberUpdate extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String operation;
    private String oldval;
    private String newval;
    private String remarks;
    private String entityName;
    @ManyToOne
    @JoinColumn(name = "custid")
    private Customers customers;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    public SubscriberUpdate() {
    }

    public SubscriberUpdate(String operation, String oldval, String newval, String remarks, Customers customers, String textval) {
        this.operation = operation;
        this.oldval = oldval;
        this.newval = newval;
        this.remarks = remarks;
        this.customers = customers;
        this.entityName = textval;
    }

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }
}
