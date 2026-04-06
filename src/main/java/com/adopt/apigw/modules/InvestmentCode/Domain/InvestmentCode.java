package com.adopt.apigw.modules.InvestmentCode.Domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltinvestmentcode")
@EntityListeners(AuditableListener.class)
public class InvestmentCode extends Auditable implements IBaseData<Long> {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investmentcode_id", nullable = false, length = 40)
    private Long id;

    @Column(name = "iccode")
    private String iccode;

    @Column(name = "icname")
    private String icname;


    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

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
        return this.isDeleted;
    }
}
