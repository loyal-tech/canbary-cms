package com.adopt.apigw.modules.BuildingMgmt.Domain;

import com.adopt.apigw.core.data.IBaseData;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmbuildingmapping")
@NoArgsConstructor
public class BuildingMapping implements IBaseData<Long> {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Long id;

    @Column(name = "building_number",nullable = false)
    private String buildingNumber;


    @JsonBackReference
    @ManyToOne()
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "building_mgmt_id")
    private BuildingManagement buildingManagement;


    @Column(name = "is_deleted",nullable = false)
    private Boolean isDeleted;
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


}
