package com.adopt.apigw.modules.NetworkDevices.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.InventoryManagement.inward.Inward;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Table(name = "tblnetworkdevices")
@DynamicInsert
@EntityListeners(AuditableListener.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property  = "id",
        scope     = Long.class)
public class NetworkDevices extends Auditable implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deviceid")
    private Long id;

    private String name;
    private String devicetype;
    private String status;
    private String latitude;
    private String longitude;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "servicearea_id")
    private ServiceArea servicearea;

    @ToString.Exclude
    @LazyCollection(LazyCollectionOption.FALSE)
    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "networkDevices")
    private List<Oltslots> oltslotsList = new ArrayList<>();

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

//    @Column(name = "parent_network_device_id")
//    private Long parentNetworkDeviceId;
    @Column(name = "total_in_ports")
    private Integer totalInPorts;
    @Column(name = "available_in_ports")
    private Integer availableInPorts;
    @Column(name = "total_out_ports")
    private Integer totalOutPorts;
    @Column(name = "available_out_ports")
    private Integer availableOutPorts;
    @Column(name = "total_ports")
    private Integer totalPorts;

    @Column(name = "available_ports")
    private Integer availablePorts;
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

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(targetEntity = ServiceArea.class,orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tblnetworkdevicesservicearearel", joinColumns = {@JoinColumn(name = "deviceid")}
            ,inverseJoinColumns = {@JoinColumn(name = "serviceareaid")})
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();


    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "inward_id")
    private Long inwardId;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "customer_inventory_id")
    private Long custInventoryId;


    @Column(name = "inventory_mapping_id")
    private Long inventorymappingId;

    @Transient
    private String  productName;
}
