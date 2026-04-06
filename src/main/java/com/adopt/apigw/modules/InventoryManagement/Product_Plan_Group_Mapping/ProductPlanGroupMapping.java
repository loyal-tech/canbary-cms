package com.adopt.apigw.modules.InventoryManagement.Product_Plan_Group_Mapping;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Data
@EntityListeners(Auditable.class)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_product_plan_group_mapping")
public class ProductPlanGroupMapping implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Long id;

    @Column(name = "plan_group_id",length = 40)
    private Long planGroupId;

    @Column(name = "product_category_id",length = 40)
    private Long productCategoryId;

    @Column(name = "product_type",length = 40)
    private String product_type;

    @Column(name = "product_id",length = 40)
    private Long productId;

    @Column(name = "revised_charge",length = 40)
    private String revisedCharge;

    @Column(name = "ownership_type",length = 40)
    private String ownershipType;

    @Column(name="name")
    private String name;

    @Transient
    private String productName;
    @Transient
    private String productCategoryName;
    @Transient
    private String planName;
    @Column(name = "plan_id", length = 40)
    private Long planId;

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
