package com.adopt.apigw.modules.TimeBasePolicy.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.core.dto.IBaseDto2;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBook;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltimebasepolicydetails")
public class TimeBasePolicyDetails implements IBaseData2<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "details_id", nullable = false, length = 40)
    private Long detailsid;

//    @Column(name = "policy_id", nullable = false, length = 40)
//    private Long policyid;

    @Column(name = "from_day", nullable = false)
    private String fromDay;

    @Column(name = "to_day", nullable = false)
    private String toDay;

    @Column(name = "from_time", nullable = false)
    private String fromTime;

    @Column(name = "to_time", nullable = false)
    private String toTime;

    @DiffIgnore
    @Column(name = "qqsid", nullable = false)
    private Long qqsid;

    @Column(name = "access")
    private Boolean access;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "policy_id")
    private TimeBasePolicy timeBasePolicy;

    @Column(name = "is_free_quota", columnDefinition = "Boolean default false")
    private Boolean isFreeQuota;

    @Override
    public Long getPrimaryKey() {
        return detailsid;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }

    @Override
    public void setBuId(Long buId) {
    }
}
