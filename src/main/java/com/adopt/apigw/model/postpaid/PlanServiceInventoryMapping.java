package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategory;
import com.adopt.apigw.modules.Pincode.domain.Pincode;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tbltserviceinventorymapping")
public class PlanServiceInventoryMapping  implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

//    @Column(name = "serviceid", nullable = false, length = 40)
//    private Integer serviceid;
//
//    @Column(name = "product_id", nullable = false, length = 40)
//    private  Integer productid;

    public PlanServiceInventoryMapping(PlanService planService, ProductCategory productCategory, Boolean isDeleted) {
        this.planService = planService;
        this.productCategory = productCategory;
        this.isDeleted = isDeleted;
    }

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(targetEntity = PlanService.class)
    @JoinColumn(name = "serviceid", referencedColumnName = "serviceid", updatable = true, insertable = true)
    private PlanService planService;

    @ManyToOne(targetEntity = ProductCategory.class)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id", updatable = true, insertable = true)
    private ProductCategory productCategory;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @Override
    public Long getPrimaryKey() { return id; }

    @Override
    public void setDeleteFlag(boolean deleteFlag) { this.isDeleted = deleteFlag; }

    @Override
    public boolean getDeleteFlag()  {
        return isDeleted;
    }



}
