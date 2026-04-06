package com.adopt.apigw.modules.InventoryManagement.productBundle;
import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
//@AllArgsConstructor
@Table(name="tbltbulkconsumptionmapping")
@EntityListeners(AuditableListener.class)
public class BulkConsumptionMapping extends Auditable implements IBaseData<Long> {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bulkconsumptionid", nullable = false)
    private Long bulkConsumptionId;

    @Column(name = "mac_mapping_id")
    private  Long macMappingId;

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
