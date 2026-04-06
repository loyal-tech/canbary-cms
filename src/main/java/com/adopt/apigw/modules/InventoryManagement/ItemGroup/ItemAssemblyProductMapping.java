package com.adopt.apigw.modules.InventoryManagement.ItemGroup;

import com.adopt.apigw.core.dto.IBaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblt_itemassembly_product_mapping")

public class ItemAssemblyProductMapping implements IBaseDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "itemassemblyid")
    private Long itemAssemblyId;

    @Column(name="mac_mapping_id")
    private Long itemId;

    @Column(name="product_id")
    private Long productId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getIdentityKey() {
        return null;
    }

    @Override
    public Integer getMvnoId() {
        return getMvnoId();
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }
}
