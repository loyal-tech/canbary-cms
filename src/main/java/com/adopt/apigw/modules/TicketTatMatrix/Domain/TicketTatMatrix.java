package com.adopt.apigw.modules.TicketTatMatrix.Domain;

import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.Matrix.domain.MatrixDetails;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "tblttickettatmatrix")
@EntityListeners(AuditableListener.class)

public class TicketTatMatrix extends Auditable implements IBaseData2<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    Long id;
    @Column(name = "name")
    String name;
    @Column(name = "status")
    String status;

    @Column(name = "slatime_p1")
    Long slaTimep1;

    @Column(name = "slatime_p2")
    Long slaTimep2;

    @Column(name = "stime_p3")
    Long slaTime3;

    @Column(name = "sunit_p1")
    String sunitp1;

    @Column(name = "sunit_p2")
    String sunitp2;

    @Column(name = "sunit_p3")
    String sunitp3;

    @OneToMany(targetEntity = TicketTatMatrixMapping.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "tat_mapping_id")
    List<TicketTatMatrixMapping> tatMatrixMappings;


    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "response_time")
    Long rtime;

    @Column(name = "response_unit")
    String runit;

    @Column(columnDefinition = "Boolean default false", nullable = false)
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
