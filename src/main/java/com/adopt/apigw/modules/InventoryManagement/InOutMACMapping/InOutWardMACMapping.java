package com.adopt.apigw.modules.InventoryManagement.InOutMACMapping;


import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.InventoryManagement.inward.Inward;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@Table(name = "tblhitemhistory")
@EntityListeners(AuditableListener.class)
public class InOutWardMACMapping extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mac_mapping_id")
    private Long id;

    @Column(name = "inward_id", nullable = false)
    private Long inwardId;

    @Column(name = "outward_id")
    private Long outwardId;

    @Column(name = "status")
    String status;

    @Column(name = "mac")
    String macAddress;
    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "cust_inventory_mapping_id")
    private Long custInventoryMappingId;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;

    private Integer currentApproveId;
    private Integer previousApproveId;
    private Integer teamHierarchyMappingId;

    @Column(name = "used_count")
    private Integer usedCount;

    @Column(name = "inward_id_of_outward")
    private Long inwardIdOfOutward;

    @Column(name = "is_forwarded", columnDefinition = "Boolean default false", nullable = false)
    private Integer isForwarded = 0;

    @Column(name = "is_returned", columnDefinition = "Boolean default false", nullable = false)
    private Integer isReturned = 0;

    @Column(name = "remark")
    private String remark;

    @Column(name = "external_item_id")
    private Long externalItemId;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "inventory_mapping_id")
    private Long inventoryMappingId;

    @Column(name = "bulkconsumption_id")
    private Long bulkConsumptionId;

    @Column(name = "non_serialized_item_id")
    private Long nonSerializedItemId;

    @Transient
    private String itemStatus;
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

    @Override
    public String toString() {
        return "id";
    }
}
