package com.adopt.apigw.modules.InventoryManagement.ExternalItemMacSerialMapping;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@Table(name = "tbltexternalitemmacmapping")
@EntityListeners(AutoCloseable.class)
public class ExternalItemMacSerialMapping extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mac_mapping_id")
    private Long id;

    @Column(name = "external_item_id", nullable = false)
    private Long externalItemId;

    @Column(name = "mac")
    private String macAddress;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "cust_inventory_mapping_id")
    private Long custInventoryMappingId;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "item_id")
    private Long itemId;

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
        return isDeleted;
    }
}
