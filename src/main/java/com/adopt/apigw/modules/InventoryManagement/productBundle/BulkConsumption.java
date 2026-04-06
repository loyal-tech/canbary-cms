package com.adopt.apigw.modules.InventoryManagement.productBundle;
import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.adopt.apigw.modules.InventoryManagement.item.Item;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tblmbulkconsumption")
@EntityListeners(AuditableListener.class)

public class BulkConsumption  extends Auditable implements IBaseData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
//
    @Column(name = "name")
    private String bulkConsumptionName;
//
////    @Column(name = "status")
////    private String bulkConsumptionStatus;
//
    @Column(name = "mvno_id", updatable = false)
    private Integer mvnoId;
//
//
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tbltbulkconsumptionmapping", joinColumns = {@JoinColumn(name = "bulkconsumptionid")}
            , inverseJoinColumns = {@JoinColumn(name = "mac_mapping_id")})
    private List<InOutWardMACMapping> itemListLongId = new ArrayList<>();

    @Column(name = "product_id")
    private Long productId;
//
//    @Column(name = "inward_id")
//    private Long inwardId;
//
    @Column(name = "approval_status")
    private String approvalStatus;
//
//    @Column(name = "approval_remark")
//    private String approvalRemark;
//
    @Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;
//
    @Column(name = "qty")
    private Long qty;
//
    @Column(name = "itemtype")
    private String itemType;
//
//    @Column(name = "ownerid")
//    private Long ownerId;
//
//    @Column(name = "ownertype")
//    private String ownerType;
    @Override
    public Serializable getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return  this.isDeleted;
    }
}
