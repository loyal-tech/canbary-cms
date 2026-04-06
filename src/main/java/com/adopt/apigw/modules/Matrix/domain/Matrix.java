package com.adopt.apigw.modules.Matrix.domain;

import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
@Getter
@Setter
@EntityListeners(AuditableListener.class)
@Entity
@Table(name = "tblmmatrix")
public class Matrix  extends Auditable implements IBaseData2<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    Long id;
    @Column(name = "name")
    String name;
    @Column(name = "status")
    String status;

    @Column(name = "sla_time")
    Long slaTime;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "sunit")
    String slaUnit;

    @OneToMany(targetEntity = MatrixDetails.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "tat_management_id")
    List<MatrixDetails> matrixDetailsList;



    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "lcoid")
    private Integer lcoId;

    @Column(name = "response_time")
    Long rtime;

    @Column(name = "response_unit")
    String runit;

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
