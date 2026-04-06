package com.adopt.apigw.modules.SectorMaster.Domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblsectormaster")
@EntityListeners(AuditableListener.class)

public class SectorMaster extends Auditable implements IBaseData<Long> {

    @Id
    @Column(name = "sector_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(name = "sector_name")
    private String sname;

    @Column(name = "status")
    private String status;

    @DiffIgnore
    @Column(name = "count")
    Integer count;

    @DiffIgnore
    @Column(name = "ezy_bill_sector_id", nullable = false)
    Integer ezyBillSectorId = 1;

    @Column(columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

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
