package com.adopt.apigw.modules.qosPolicy.domain;

import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "tbl_qos_policy")
@NoArgsConstructor
public class QOSPolicy extends Auditable implements IBaseData2<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String basepolicyname;
    private String thpolicyname;
    private String baseparam1;
    private String baseparam2;
    private String baseparam3;
    private String thparam1;
    private String thparam2;
    private String thparam3;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @DiffIgnore
    @Column(name = "upstreamprofileuid")
    private String upstreamprofileuid;

    @DiffIgnore
    @Column(name = "downstreamprofileuid")
    private String downstreamprofileuid;
    @Column(name = "upstreamprofilename")
    private String upstreamprofileName;
    @Column(name = "downstreamprofilename")
    private String downstreamprofileName;

    private String type;

    private String qosspeed;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = QOSPolicyGatewayMapping.class, cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "qos_policy_id")
    List<QOSPolicyGatewayMapping> qosPolicyGatewayMappingList;

//    private String type;
    @Column(name = "mvnoName")
    private String mvnoName;
    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }

    public QOSPolicy(QOSPolicy qosPolicy) {
        this.id = qosPolicy.getId();
        this.name = qosPolicy.getName();
        this.description = qosPolicy.getDescription();
        this.basepolicyname = qosPolicy.getBasepolicyname();
        this.thpolicyname = qosPolicy.getThpolicyname();
        this.baseparam1 = qosPolicy.getBaseparam1();
        this.baseparam2 = qosPolicy.getBaseparam2();
        this.baseparam3 = qosPolicy.baseparam3;
        this.thparam1 = qosPolicy.getThparam1();
        this.thparam2 = qosPolicy.getThparam2();
        this.thparam3 = qosPolicy.getThparam3();
        this.isDeleted = qosPolicy.getIsDeleted();
        this.mvnoId = qosPolicy.getMvnoId();
        this.buId = qosPolicy.getBuId();
        this.type = qosPolicy.getType();
        this.qosspeed = qosPolicy.getQosspeed();
    }
}
