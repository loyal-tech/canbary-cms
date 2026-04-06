package com.adopt.apigw.modules.tickets.domain;


import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.postpaid.PlanService;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@EntityListeners(AuditableListener.class)
@Entity
@Table(name = "tblmticketreasoncategory")
public class TicketReasonCategory extends Auditable implements IBaseData2<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String categoryName;

    @ManyToOne(targetEntity = PlanService.class)
    @JoinColumn(name = "service_id",nullable = false,referencedColumnName = "serviceid")
    private PlanService service;

    @Column(name = "mvno_id", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(targetEntity = TicketReasonCategoryTATMapping.class, cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_reason_category_id")
    List<TicketReasonCategoryTATMapping> ticketReasonCategoryTATMappingList;

    String status;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "sla_time_P3")
    Long slaTimeP3;

    @Column(name = "sunitP3")
    String slaUnitP3;

    @Column(name = "sla_time_P2")
    Long slaTimeP2;

    @Column(name = "sunitP2")
    String slaUnitP2;

    @Column(name = "sla_time_P1")
    Long slaTimeP1;

    @Column(name = "sunitP1")
    String slaUnitP1;

    @Column(name = "department")
    String department;


    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
    Integer lcoId;

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
