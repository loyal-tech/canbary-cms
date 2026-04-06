package com.adopt.apigw.modules.BuildingMgmt.Domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tblmbuildingmanagement")
@EntityListeners(AuditableListener.class)
@NoArgsConstructor
@AllArgsConstructor
public class BuildingManagement extends Auditable implements IBaseData<Long> {

    @Id
    @Column(name = "building_mgmt_id",nullable = false)
    private Long buildingMgmtId;

    @Column(name = "building_name",nullable = false)
    private String buildingName;

    @Column(name = "pincode_id")
    private Integer pincodeId;

    @Column(name = "area_id")
    private Integer areaId;

    @Column(name = "sub_area_id")
    private Integer subAreaId;


    @Column(name = "mvnoid",nullable = false)
    private Integer mvnoId;

    @Column(name = "buid")
    private Integer buid;

    @Column(name = "is_deleted",nullable = false)
    private Boolean isDeleted;

    @Column(name = "building_type")
    private String buildingType;


    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "buildingManagement")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    private List<BuildingMapping> buildingMappings = new ArrayList<>();


    @Override
    public Long getPrimaryKey() {
        return this.buildingMgmtId;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }
    public BuildingManagement(Long buildingMgmtId){
            this.buildingMgmtId=buildingMgmtId;
    }

}
