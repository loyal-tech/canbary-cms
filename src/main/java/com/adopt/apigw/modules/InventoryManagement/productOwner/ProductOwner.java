package com.adopt.apigw.modules.InventoryManagement.productOwner;


import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.postpaid.Charge;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tbltproductowner")
@EntityListeners(AuditableListener.class)
public class ProductOwner extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "owner_type")
    private String ownerType;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "used_qty")
    private Long usedQty;

    @Column(name = "unused_qty")
    private Long unusedQty;

    @Column(name = "in_transit_qty")
    private Long inTransitQty;

    @Column(name = "bound_qty")
    private Long boundQty;

    @Transient
    private Integer mvnoId;

    @Transient
    private String wareHouseName;

    @Transient
    private String productName;
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}
