package com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltexternalitemmanagement")
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
public class ExternalItemManagement extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "external_item_id")
    private Long id;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "external_item_number")
    private String externalItemGroupNumber;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product productId;

    @Column(name = "quantity")
    private Long qty;

    @Column(name = "used_qty")
    private Long usedQty;

    @Column(name = "unused_qty")
    private Long unusedQty;

    @Column(name = "ownership_type")
    private String ownershipType;

    @Column(name = "status")
    private String status;

    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "servicearea_id")
    private ServiceArea serviceAreaId;

    @Column(name = "in_transit_qty")
    private Long inTransitQty;

    @Column(name = "rejected_qty")
    private Long rejectedQty;

    @Column(name = "approval_status")
    private String approvalStatus;

    @Column(name = "total_mac_serial")
    private Long totalMacSerial;

    @Column(name = "approval_remark")
    private String approvalRemark;


    @Column(name = "owner_id")
    private Long ownerId;


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
