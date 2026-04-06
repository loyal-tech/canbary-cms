package com.adopt.apigw.modules.InventoryManagement.productCategory;


import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tblmproductcategory")
@EntityListeners(AuditableListener.class)
public class ProductCategory extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "unit")
    private String unit;

    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;

    @Column(name = "has_mac")
    private boolean hasMac;

    @Column(name = "type")
    private String type;

    @Column(name = "status")
    private String status;

    @Column(name = "rms_product_id")
    private String productId;

    public ProductCategory(Long id) {
        this.id = id;
    }

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "has_serial")
    private boolean hasSerial;

    @Column(name = "has_trackable")
    private boolean hasTrackable;
    @Column(name = "has_port")
    private boolean hasPort;

    @Column(name="has_cas")
    private boolean hasCas;

    @Column(name="dtvcategory")
    private String dtvCategory;

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
