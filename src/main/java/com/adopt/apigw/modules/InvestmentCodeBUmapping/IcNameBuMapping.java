package com.adopt.apigw.modules.InvestmentCodeBUmapping;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import com.adopt.apigw.modules.InvestmentCode.Domain.InvestmentCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmicnamebumapping")
public class IcNameBuMapping implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "icname_bu_mappingid")
    private Long id;
//    @Column(name = "businessunitid")
//    private Long businessunitid;
//    @Column(name = "investmentcode_id")
//    private Long investmentcode_id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(targetEntity = BusinessUnit.class)
    @JoinColumn(name = "businessunitid", referencedColumnName = "businessunitid", updatable = true, insertable = true)
    private BusinessUnit businessUnitid;

    @ManyToOne(targetEntity = InvestmentCode.class)
    @JoinColumn(name = "investmentcode_id", referencedColumnName = "investmentcode_id",updatable = true, insertable = true)
    private InvestmentCode investmentCodeid;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

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
