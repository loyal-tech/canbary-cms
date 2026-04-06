package com.adopt.apigw.modules.InventoryManagement.item;


import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tblmserializeditem")
@EntityListeners(AuditableListener.class)
public class Item extends Auditable implements IBaseData<Long> {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "mac")
    private String macAddress;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;

//    @Column(name = "status")
//    private String status;

    @Column(name = "item_condition")
    private String condition;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "current_inward_id")
    private Long currentInwardId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "owner_type")
    private String ownerType;

    @Column(name = "warranty_period", nullable = false)
    private Integer warrantyPeriod;

    @Column(name = "warranty")
    private String warranty;

    @Column(name = "current_inward_type")
    private String currentInwardType;

    @Column(name = "item_status")
    private String itemStatus;

    @Column(name = "remaining_days")
    private  String remainingDays;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "ownership_type")
    private String ownershipType;

    @Column(name = "external_item_id")
    private Long externalItemId;

    @Column(name = "intransiant_warrenty")
    private String intransiantWarrenty;


    @Column(name = "intransiant_ownership")
    private String intransiantOwnership;

    @Column(name = "intransiant_warrenty_status")
    private String intransiantWarrentyStatus;

    @Column(name="expiry_date")
    private LocalDateTime expireDate;

    @Column(name="intransiant_expiry_date")
    private LocalDateTime intransiantexpireDate;

    @Transient
    private Double productRefundAmount;
    @Transient
    private boolean refundFlag;

    private String remarks;

    @Transient
    private String removeFrom;

    public Item(Long id) {
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
