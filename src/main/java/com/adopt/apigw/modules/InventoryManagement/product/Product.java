package com.adopt.apigw.modules.InventoryManagement.product;


import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.postpaid.Charge;
import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategory;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tbltproduct")
@EntityListeners(AuditableListener.class)
public class Product extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "name")
    private String name;

//    @Column(name = "unit")
//    private String unit;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;


    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;

    @Column(name = "total_in_ports")
    private Integer totalInPorts;
    @Column(name = "available_in_ports")
    private Integer availableInPorts;
    @Column(name = "total_out_ports")
    private Integer totalOutPorts;
    @Column(name = "available_out_ports")
    private Integer availableOutPorts;

    @Column(name = "rms_product_id")
    private String productId;

    @Column(name = "nav_ledger_id")
    private String navLedgerId;

//    @Column(name = "has_mac")
//    private boolean hasMac;

    public Product(Long id) {
        this.id = id;
    }

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

//    @Column(name = "has_serial")
//    private boolean hasSerial;

//    @Column(name = "charge_id",nullable = false)
//    @OneToOne(targetEntity = Charge.class,fetch = FetchType.EAGER,cascade = CascadeType.ALL)
//    @JoinColumn(referencedColumnName = "CHARGEID",name ="old_prod_charge_id" )

    @Column(name = "refurb_prod_charge_id")
    private Integer refurburshiedProductCharge;

    @OneToOne(targetEntity = ProductCategory.class)
    @JoinColumn(referencedColumnName = "product_id",name ="pc_id" )
    private ProductCategory productCategory;

    private Integer expiryTime;

    private String expiryTimeUnit;

    @Column(name = "new_prod_charge_id")
    private Integer newProductCharge;

    @Column(name = "refurb_pra_in_wrty")
    private Double refurburshiedProductRefAmountInWarranty;

    @Column(name = "refurb_pra_post_wrty")
    private Double refurburshiedProductRefAmountPostWarranty;

    @Column(name = "new_pra_in_wrty")
    private Double newProductRefAmountInWarranty;

    @Column(name = "new_pra_post_wrty")
    private Double newProductRefAmountPostWarranty;


    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "vendorid")
    private Long vendorId;

    @Column(name="actualpricenewproduct")
    private Long actualpricenewProduct;
    @Column(name="actualpricerefurbishedproduct")
    private Long actualpricerefurbishedProduct;

    @Transient
    private Double newProductAmount;

    @Transient
    private Double refurburshiedProductAmount;

    @Transient
    private Long newPrice;

    @Transient
    private Long refurburshiedPrice;

    @Transient
    private Long refurburshiedProductTax;

    @Transient
    private Long newProductTax;

    @Transient
    private String refurburshiedProductTaxName;

    @Transient
    private String newProductTaxName;





//    @OneToOne(targetEntity = Charge.class,fetch = FetchType.EAGER,cascade = CascadeType.ALL)
//    @JoinColumn(referencedColumnName = "CHARGEID",name ="new_prod_charge_id" )



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
