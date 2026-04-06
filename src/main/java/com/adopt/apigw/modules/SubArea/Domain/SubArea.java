package com.adopt.apigw.modules.SubArea.Domain;


import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.Area.domain.Area;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmsubarea")
@EntityListeners(AuditableListener.class)
@NoArgsConstructor
public class SubArea extends Auditable implements IBaseData<Long> {

    @Id
    @Column(name = "subareaid")
    private Long id;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "is_deleted" ,nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "countryid", nullable = false, length = 40)
    private Integer countryId;

    @Column(name = "cityid", nullable = false, length = 40)
    private Integer cityId;

    @Column(name = "stateid", nullable = false, length = 40)
    private Integer stateId;

    @Column(name = "mvnoid", nullable = false, length = 40)
    private Integer mvnoId;

    @Column(name = "buid")
    private Long buId;

    @JsonBackReference
    @ManyToOne()
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "areaid")
    private Area area;
    public SubArea(Long id){
        this.id=id;

    }

    @Override
    public Long getPrimaryKey() {
        return this.id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }

//    @Override
    public void setBuId(Long buId) {
        this.buId = buId;
    }
}

