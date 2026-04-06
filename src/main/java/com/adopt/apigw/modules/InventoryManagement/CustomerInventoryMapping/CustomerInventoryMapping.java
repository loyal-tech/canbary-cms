package com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemMacSerialMapping.ExternalItemMacSerialMapping;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemManagement.domain.ExternalItemManagement;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.adopt.apigw.modules.InventoryManagement.inward.Inward;
import com.adopt.apigw.modules.InventoryManagement.outward.Outward;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.Teams.domain.TeamHierarchyMapping;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@Table(name = "tblmcustomer_inventory_mapping")
@EntityListeners(AuditableListener.class)
public class
CustomerInventoryMapping extends Auditable implements IBaseData<Long> {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_id")
    private Long id;

    @Column(name = "quantity")
    Long qty;

    @ManyToOne(targetEntity = Product.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id",nullable = true)
    Product product;

    @ManyToOne(targetEntity = Customers.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", referencedColumnName = "custid")
    Customers customer;

    @ManyToOne(targetEntity = StaffUser.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "staff_id", referencedColumnName = "staffid")
    StaffUser staff;

    @Column(name = "inward_id")
    private Long inwardId;

    @Column(name = "assigned_date_time")
    LocalDateTime assignedDateTime;

    @Column(name = "mvno_id")
    private Integer mvnoId;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "status")
    String status;

    @Column(name = "expiry_date_time")
    LocalDateTime expiryDateTime;

    @ManyToOne(targetEntity = StaffUser.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "next_approver", referencedColumnName = "staffid")
    StaffUser nextApprover;

//    @ManyToOne(targetEntity = TeamHierarchyMapping.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "team_hierarchy_mapping_id", referencedColumnName = "id")
    @Column(name = "team_hierarchy_mapping_id")
    Integer teamHierarchyMappingId;

    @OneToMany(targetEntity = InOutWardMACMapping.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "cust_inventory_mapping_id")
    List<InOutWardMACMapping> inOutWardMACMapping;

    @OneToMany(targetEntity = ExternalItemMacSerialMapping.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "cust_inventory_mapping_id")
    List<ExternalItemMacSerialMapping> externalItemMacSerialMappings;

    @Column(name = "previous_approve_id")
    Integer previousApproveId;

    @Column(name = "external_item_id")
    private Long externalItemId;

    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "custpack_id")
    private Long custPackId;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "itemassemblyid")
    private Long itemAssemblyId;

    @Column(name = "connection_no")
    private String connectionNo;

    @Column(name = "is_invoice_created")
    private Boolean isInvoiceCreated = false;
    @Column(name = "plan_id")
    private Long planId;

    @Column(name = "replacement_reason")
    private String replacementReason;

    @Column(name = "mapping_ref_id")
    private Long mapping_ref_id;

    @Column(name = "remark")
    private String approvalRemark;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "bill_to")
    private String billTo;

    @Column(name = "new_amount")
    private Double newAmount;

    @Column(name = "offer_price")
    private Double offerPrice;

    @Column(name = "is_invoice_to_org")
    private Boolean isInvoiceToOrg;

    @Column(name = "charge_id")
    private Long chargeId;

    @Column(name = "plangroup_id")
    private Long planGroupId;

    @Column(name = "is_required_approval")
    private Boolean isRequiredApproval;

    @Column(name = "is_free")
    private Boolean isFree;

    @Column(name = "payment_owner_id")
    private Long paymentOwnerId;

    @Column(name = "non_seri_remark")
    private String nonSerializedItemRemark;

    @Transient
    private String customerFirstName;
    @Transient
    private String customerLastName;

    @Transient
    private String itemwarranty;
    @Transient
    private LocalDateTime expDate;

    @Transient
    private String serviceAreaName;

    @Column(name = "ezybill_stock_id")
    private String ezyBillStockId;

    @Column(name = "billable_cust_id")
    private Long billabecustId;

    @Column(name = "pairstatus")
    private String pairStatus;

    @Column(name = "vendorid")
    private Long vendorId;

    @Transient
    private String flag;


    @Override
    public Long getPrimaryKey() {
        return this.id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;

    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}


