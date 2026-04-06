package com.adopt.apigw.modules.InventoryManagement.inward;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.adopt.apigw.modules.InventoryManagement.outward.Outward;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.InventoryManagement.warehouse.WareHouse;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.jdo.annotations.Join;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tbltinward")
@EntityListeners(AuditableListener.class)
public class Inward extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inward_id")
    private Long id;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "inward_number")
    private String inwardNumber;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "product_id")
    Product productId;

    @Column(name = "quantity")
    Long qty;

    @Column(name = "used_qty")
    Long usedQty;

    @Column(name = "unused_qty")
    Long unusedQty;

    @Column(name = "inward_date_time")
    LocalDateTime inwardDateTime;

//    @ToString.Exclude
//    @ManyToOne
//    @JoinColumn(name = "warehouse_id")
//    WareHouse wareHouseId;

    @Column(name = "type")
    String type;

    @Column(name = "status")
    String status;


    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;


    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "source_type")
    private String sourceType;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "destination_type")
    private String destinationType;

    @Column(name = "destination_id")
    private Long destinationId;

    @Column(name = "in_transit_qty")
    private Long inTransitQty;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "service_area_id")
    private ServiceArea serviceArea;

    @ManyToOne(targetEntity = Outward.class)
    @JoinColumn(name = "outward_id", referencedColumnName = "outward_id")
    Outward outwardId;

    @Column(name = "out_transit_qty")
    private Long outTransitQty;

    @Column(name = "rejected_qty")
    private Long rejectedQty;

    @Column(name = "approval_status")
    private String approvalStatus;

    @Column(name = "category_type")
    private String categoryType;

    @Column(name = "rms_inward_id")
    private String rmsInwardId;

    @Column(name = "nav_inward_id")
    private String navInwardId;

    @Column(name = "total_mac_serial")
    private Long totalMacSerial;

    @Column(name = "approval_remark")
    private String approvalRemark;

    @Column(name = "assign_non_serialized_item_qty")
    private Long assignNonSerializedItemQty;
//
//    @OneToMany(targetEntity = InOutWardMACMapping.class,fetch = FetchType.LAZY)
//    @JoinColumn(name = "inward_id")
//    List<InOutWardMACMapping> inOutWardMACMappingList;

    @Column(name = "request_inventory_id")
    private Long requestInventoryId;

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

    public Inward(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Inward   toString Override :" + inwardNumber;
    }
}
