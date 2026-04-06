package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;

import io.swagger.models.auth.In;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = " tblcustmacmapping")
@EntityListeners(AuditableListener.class)
public class CustMacMappping extends Auditable implements IBaseData<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custmacmapid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "macaddress", length = 100)
    private String macAddress;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "custid")
    private Customers customer;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name="custsermappingid")
    private Integer custsermappingid;

    @Column(name="service")
    private String service;
    @Column (name="macretentiondate")
    private Timestamp macRetentionDate;


    @Override
    public Integer getPrimaryKey() {
        return this.id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }

    public CustMacMappping() {
    }

    public CustMacMappping(CustMacMappping custMacMappping) {
        this.id = custMacMappping.getId();
        this.macAddress = custMacMappping.getMacAddress();
        this.customer = custMacMappping.getCustomer();
        this.isDeleted = custMacMappping.getIsDeleted();
        this.macRetentionDate = custMacMappping.getMacRetentionDate();
    }
}
