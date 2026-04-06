package com.adopt.apigw.modules.BankManagement.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmbankmanagement")
@EntityListeners(AuditableListener.class)
public class BankManagement extends Auditable implements IBaseData<Long> {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bankid")
    private Long id;

    private String bankname;

    private String accountnum;

    private String ifsccode;

    private String bankholdername;

    private String status;

    @Column(name = "bank_code")
    private String bankcode;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    private String banktype;


    @Override
    public Long getPrimaryKey() {
        return null;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}
