package com.adopt.apigw.modules.InventoryManagement.ItemGroup;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditableListener.class)
@Table(name = "tblmitemassembly")

@SQLDelete(sql = "UPDATE tblmitemassembly SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
public class ItemAssembly extends Auditable implements IBaseData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "itemassembly_name")
    private String  itemAssemblyName;

    @Column(name = "itemassembly_status")
    private String status;

    @Column(name = "mvno_id")
    private Long mvnoId;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tblt_itemassembly_product_mapping", joinColumns = {@JoinColumn(name = "itemassemblyid")}
            , inverseJoinColumns = {@JoinColumn(name = "mac_mapping_id")})
    @SQLDeleteAll(sql = "UPDATE tblt_itemassembly_product_mapping SET is_deleted = true WHERE itemassemblyid=?")
    @Where(clause = "is_deleted=false")
    private List<InOutWardMACMapping> itemListLongId = new ArrayList<>();

    @Transient
    private List<String> itemNameList=new ArrayList<>();


    @Override
    public Serializable getPrimaryKey() {
        return null;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}
