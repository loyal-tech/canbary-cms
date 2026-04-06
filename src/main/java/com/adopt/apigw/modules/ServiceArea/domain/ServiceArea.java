package com.adopt.apigw.modules.ServiceArea.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.postpaid.City;
import com.adopt.apigw.model.postpaid.Location;
import com.adopt.apigw.modules.Area.domain.Area;
import com.adopt.apigw.modules.LocationMaster.domain.LocationMaster;
import com.adopt.apigw.modules.LocationMaster.domain.ServiceAreaLocationMapping;
import com.adopt.apigw.modules.NetworkDevices.domain.NetworkDevices;
import com.adopt.apigw.modules.Pincode.domain.Pincode;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBookSlabDetails;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblservicearea")
//@EntityListeners(AuditableListener.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property  = "id",
        scope     = Long.class)
public class ServiceArea extends Auditable implements IBaseData<Long> {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_area_id")
    @DiffIgnore
    private Long id;

    private String name;

    private String status;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "servicearea")
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    @LazyCollection(LazyCollectionOption.FALSE)
//    @DiffIgnore
//    private List<NetworkDevices> networkDevicesList = new ArrayList<>();

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;


    @DiffIgnore
    @Column(name = "latitude", nullable = false, length = 50)
    private String latitude;
    @DiffIgnore
    @Column(name = "longitude", nullable = false, length = 50)
    private String longitude;

    @DiffIgnore
    @Column(name = "areaid", nullable = true)
    private Long areaId;

    @ManyToMany(fetch = FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.FALSE)
    @DiffIgnore
    @JoinTable(name = "tblserviceareapincoderel", joinColumns = {@JoinColumn(name = "serviceareaid")}, inverseJoinColumns = {@JoinColumn(name = "pincodeid")})
    private List<Pincode> pincodeList = new ArrayList<>();

//    @ManyToMany
//    @JoinTable(
//            name = "tbltservicearealocationmapping",
//            joinColumns = @JoinColumn(name = "service_area_id"),
//            inverseJoinColumns = @JoinColumn(name = "location_id")
//    )
//    private Set<LocationMaster> locations = new HashSet<>();


    @DiffIgnore
    @Column(name = "cityid", length = 40)
    private Long cityid;

    public ServiceArea(Long id) {
        this.id = id;
    }


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

    @Column(name = "site_name")
    private String siteName;
    public ServiceArea(ServiceArea serviceArea) {
        this.id = serviceArea.getId();
        this.name = serviceArea.getName();
        this.status = serviceArea.getStatus();
        this.isDeleted = serviceArea.getIsDeleted();
        //       this.networkDevicesList = networkDevicesList;
        this.mvnoId = serviceArea.getMvnoId();
        this.latitude = serviceArea.getLatitude();
        this.longitude = serviceArea.getLongitude();
        this.areaId = serviceArea.getAreaId();
        this.pincodeList = serviceArea.getPincodeList();
        this.cityid = serviceArea.getCityid();
        this.siteName= serviceArea.getSiteName();
    }

}
