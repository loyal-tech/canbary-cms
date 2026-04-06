package com.adopt.apigw.modules.InventoryManagement.inventoryMapping;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.adopt.apigw.modules.InventoryManagement.inward.Inward;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.Teams.domain.TeamHierarchyMapping;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@Table(name = "tblm_inventory_mapping")
@EntityListeners(AuditableListener.class)
public class InventoryMapping extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_id")
    private Long id;

    @Column(name = "quantity")
    Long qty;

    @ManyToOne(targetEntity = Product.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    Product product;

//    @ManyToOne(targetEntity = Customers.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "customer_id", referencedColumnName = "custid")
//    Customers customer;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "owner_type")
    private String ownerType;

    @ManyToOne(targetEntity = StaffUser.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "staff_id", referencedColumnName = "staffid")
    StaffUser staff;

    @ManyToOne(targetEntity = Inward.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "inward_id", referencedColumnName = "inward_id")
    Inward inward;

    @Column(name = "assigned_date_time")
    LocalDateTime assignedDateTime;

    @Column(name = "mvno_id")
    private Integer mvnoId;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "approval_status")
    String approvalStatus;

    @Column(name = "expiry_date_time")
    LocalDateTime expiryDateTime;

    @ManyToOne(targetEntity = StaffUser.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "next_approver", referencedColumnName = "staffid")
    StaffUser nextApprover;

    @ManyToOne(targetEntity = TeamHierarchyMapping.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "team_hierarchy_mapping_id", referencedColumnName = "id")
    TeamHierarchyMapping teamHierarchyMapping;

    @OneToMany(targetEntity = InOutWardMACMapping.class, fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "inventory_mapping_id")
    List<InOutWardMACMapping> inOutWardMACMapping;

    @Column(name = "previous_approve_id")
    Integer previousApproveId;

    @Column(name = "approval_remark")
    private String approvalRemark;
    @Transient
    private String popName;
    @Transient
    private String serviceAreaName;

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


