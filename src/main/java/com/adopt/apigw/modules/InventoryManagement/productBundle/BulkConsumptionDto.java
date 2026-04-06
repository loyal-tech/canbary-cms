package com.adopt.apigw.modules.InventoryManagement.productBundle;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.adopt.apigw.modules.InventoryManagement.item.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;
 import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class BulkConsumptionDto implements IBaseDto {

    private Long id;
    private String bulkConsumptionName;
    private Integer mvnoId;
    private Boolean isDeleted;
    private Long productId;
    private String productName;
    private Long inwardId;
    private String inwardNumber;
    private String approvalStatus;
    private String approvalRemark;
    private Long qty;
    private String itemType;
    private Long ownerId;
    private String ownerType;
    private List<Long> itemListLongId=new ArrayList<>();
    private List<InOutWardMACMapping> inOutWardMACMappings = new ArrayList<>();

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

}
