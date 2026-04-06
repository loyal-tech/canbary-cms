package com.adopt.apigw.modules.InventoryManagement.warehouse;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.Teams.domain.Teams;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tbltwarehousemanagement")
@SQLDelete(sql = "UPDATE tbltwarehousemanagement SET is_deleted = true WHERE warehouse_id=?")
@Where(clause = "is_deleted=false")
@EntityListeners(AuditableListener.class)
public class WareHouse extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_id")
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "address1", nullable = false)
    private String address1;
    @Column(name = "address2", nullable = false)
    private String address2;
    @Column(name = "pincode", nullable = false)
    private String pincode;
    @Column(name = "state", nullable = false)
    private String state;
    @Column(name = "city", nullable = false)
    private String city;
    @Column(name = "country", nullable = false)
    private String country;
    @Column(name = "latitude", nullable = false)
    private String latitude;
    @Column(name = "longitude", nullable = false)
    private String longitude;
    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "rms_warehouse_id")
    private String rmsWarehouseId;

    @Column(name = "nav_warehouse_id")
    private String navWarehouseId;
    @Column(name = "warehouse_code")
    private String warehouseCode;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tblwarehousemanagmentservicearearel", joinColumns = {@JoinColumn(name = "warehouse_id")}
            , inverseJoinColumns = {@JoinColumn(name = "serviceareaid")})
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();

    @Column (name = "warehousetype")
    private String warehouseType;

    @Column(name = "branch_id")
    private Long branchId;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tblwarehousemanagmentteamsmapping", joinColumns = {@JoinColumn(name = "warehouse_id")}
            , inverseJoinColumns = {@JoinColumn(name = "team_id")})
    private List<Teams> teamsIdsList = new ArrayList<>();

    @Transient
    private List<Teams> teamsList = new ArrayList<>();

    public WareHouse(Long id) {
        this.id = id;
    }

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
}
