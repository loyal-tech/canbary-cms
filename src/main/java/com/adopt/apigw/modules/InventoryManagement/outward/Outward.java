package com.adopt.apigw.modules.InventoryManagement.outward;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.InventoryManagement.inward.Inward;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.InventoryManagement.warehouse.WareHouse;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tbltoutward")
@EntityListeners(AuditableListener.class)
public class Outward extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outward_id")
    private Long id;

    @Column(name = "outward_number")
    private String outwardNumber;

    @Column(name = "quantity")
    Long qty;

//    @Column(name = "user_type")
//    String userType;

    @Column(name = "status")
    String status;


    @ManyToOne(targetEntity = Product.class)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    Product productId;

//    @ManyToOne(targetEntity = WareHouse.class)
//    @JoinColumn(name = "warehouse_id", referencedColumnName = "warehouse_id")
//    WareHouse wareHouseId;



//    @ManyToOne(targetEntity = StaffUser.class)
//    @Column(name = "staff_id")
//    Long staffId;


    @ManyToOne(targetEntity = Inward.class)
    @JoinColumn(name = "inward_id", referencedColumnName = "inward_id")
    Inward inwardId;

    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;

    @Column(name = "outward_date_time")
    LocalDateTime outwardDateTime;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "used_qty")
    Long usedQty;

    @Column(name = "unused_qty")
    Long unusedQty;


    private transient String productName;
    private transient String wareHouseName;
    private transient String inwardNumber;
    private transient String unit;


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

    @Column(name = "out_transit_qty")
    private Long outTransitQty;

    @Column(name = "rejected_qty")
    private Long rejectedQty;

    @Column(name = "approval_status")
    private String approvalStatus;

    @Column(name = "category_type")
    private String categoryType;

    @Column(name = "rms_outward_id")
    private String rmsOutwardId;

    @Column(name = "nav_outward_id")
    private String navOutwardId;

    @Column(name = "approval_remark")
    private String approvalRemark;

    @Column(name = "type")
    private String type;

    @Column(name = "request_inventory_id")
    private Long requestInventoryId;

    @Column(name="request_inventory_product_id")
    private Long requestInventoryProductId;

    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return this.id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }

    public Outward(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Outward   toString Override :" + id;
    }

}
