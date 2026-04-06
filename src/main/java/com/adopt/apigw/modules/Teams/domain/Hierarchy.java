package com.adopt.apigw.modules.Teams.domain;

import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@EntityListeners(AuditableListener.class)
@Entity
@Table(name = "tblmhierarchy")
public class Hierarchy extends Auditable implements IBaseData2<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "hierarchy_id")
    private Long id;

    @Column(name = "mvno_id", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name ="is_deleted",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted;

    //@Column(name = "flowname", nullable = false)
    //private String  flowName;

    //@Column(name = "status", nullable = false)
    //private String status;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "hierarchyname", nullable = false)
    private String hierarchyName;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @OneToMany(targetEntity = TeamHierarchyMapping.class, cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "hierarchy_id")
    private List<TeamHierarchyMapping> teamHierarchyMappingList;

    @Column(name = "lcoid")
    private Integer lcoId;

    @JsonIgnore
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

    @Override
    public String toString() {
        return "Hierarchy{" +
                "id=" + id +
                ", mvnoId=" + mvnoId +
                ", isDeleted=" + isDeleted +
                ", hierarchyName='" + hierarchyName + '\'' +
                ", eventName='" + eventName + '\'' +
                ", teamHierarchyMappingList=" + teamHierarchyMappingList +
                '}';
    }
}
