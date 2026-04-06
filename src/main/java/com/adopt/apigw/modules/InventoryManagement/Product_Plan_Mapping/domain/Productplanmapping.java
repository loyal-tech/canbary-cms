package com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbl_product_plan_mapping")
@EntityListeners(AuditableListener.class)

public class Productplanmapping extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Long id;

    @Column(name = "plan_id",length = 40)
    private Long planId;

    @Column(name = "product_category_id",length = 40)
    private Long productCategoryId;

    @Column(name = "product_type",length = 40)
    private String product_type;

    @Column(name = "product_id",length = 40)
    private Long productId;

    @Column(name = "revised_charge",length = 40)
    private String revisedCharge;

    @Column(name = "ownershipType",length = 40)
    private String ownershipType;

    @Column(name="name")
    private String name;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "LASTMODIFIEDDATE")
    private LocalDateTime updatedate;

    @Column(name = "createbyname", nullable = false, length = 40, updatable = false)
    private String createdByName;

    @Column(name = "updatebyname", nullable = false, length = 40)
    private String lastModifiedByName;

    @Column(name = "CREATEDBYSTAFFID", nullable = false, length = 40, updatable = false)
    private Integer createdById;

    @Column(name = "LASTMODIFIEDBYSTAFFID", nullable = false, length = 40)
    private Integer lastModifiedById;
    @Transient
    private String productCategoryName;
    @Transient
    private String productName;
    @Transient
    private String planName;
    
    @Column(name = "product_quantity")
    private Integer productQuantity;

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
