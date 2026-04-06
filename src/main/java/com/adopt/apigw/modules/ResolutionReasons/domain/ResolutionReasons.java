package com.adopt.apigw.modules.ResolutionReasons.domain;

import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.ResolutionReasons.model.RootCauseResolutionMapping;
import com.adopt.apigw.modules.tickets.domain.ResoSubCategoryMapping;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name="tblcaseresolutions")
public class ResolutionReasons extends Auditable implements IBaseData2<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="res_id")
    private Long id;

    @Column(name="res_name")
    private String name;
    @Column(name="res_status")
    private String status;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = ResoSubCategoryMapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "res_id")
    List<ResoSubCategoryMapping> resoSubCategoryMappingList;


    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
    private Integer lcoId;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = RootCauseResolutionMapping.class,orphanRemoval = true,cascade = CascadeType.ALL)
    @JoinColumn(name = "resolution_id")
    private List<RootCauseResolutionMapping> rootCauseResolutionMappingList;

//    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
//    private Integer lcoId;

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted=deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }
}
