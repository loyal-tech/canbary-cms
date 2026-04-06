package com.adopt.apigw.modules.InventoryManagement.NonSerializedItemHierarchy;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tbltnonserializeditemhierarchy")
@EntityListeners(AuditableListener.class)
public class NonSerializedItemHierarchy extends Auditable implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "parent_item_id")
    private Long parentItemId;
    @Column(name = "child_item_id")
    private Long childItemId;
    @Column(name = "qty")
    private Long qty;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;
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
