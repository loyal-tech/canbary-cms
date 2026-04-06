package com.adopt.apigw.modules.InventoryManagement.NonSerializedItem;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tblmnonserializeditem")
@EntityListeners(AuditableListener.class)
public class NonSerializedItem extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;

    @Column(name = "non_serialized_item_condition")
    private String nonSerializedItemcondition;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "current_inward_id")
    private Long currentInwardId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "owner_type")
    private String ownerType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "warranty_period", nullable = false, updatable = false)
    private Integer warrantyPeriod;

    @Column(name = "warranty")
    private String warranty;

    @Column(name = "current_inward_type")
    private String currentInwardType;

    @Column(name = "non_serialized_item_status")
    private String itemStatus;

    @Column(name = "remaining_days")
    private  String remainingDays;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "ownership_type")
    private String ownershipType;

    @Column(name = "external_item_id")
    private Long externalItemId;

    @Column(name = "intransient_warranty")
    private String intransientWarranty;

    @Column(name = "intransient_ownership")
    private String intransientOwnership;

    @Column(name = "intransient_warranty_status")
    private String intransientWarrantyStatus;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "qty")
    private Long qty;

    public NonSerializedItem(Long id) {
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
