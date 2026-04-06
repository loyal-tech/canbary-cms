package com.adopt.apigw.modules.InventoryManagement.inward;


import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.adopt.apigw.modules.InventoryManagement.outward.Outward;
import com.adopt.apigw.modules.InventoryManagement.product.Product;
import com.adopt.apigw.modules.InventoryManagement.product.ProductDto;
import com.adopt.apigw.modules.InventoryManagement.warehouse.WareHouse;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InwardDto implements IBaseDto {

    Long id ;
    String inwardNumber;
    Product productId;
    Long qty;
    Long usedQty;
    Long unusedQty;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime inwardDateTime;
//    WareHouse wareHouseId;
    String type;
    String status;
    Integer mvnoId;
    private Boolean isDeleted = false;
    private String sourceType;
    private Long sourceId;
    private String destinationType;
    private Long destinationId;
    private Long inTransitQty;
    private Long serviceAreaId;
    private Outward outwardId;
    private Long outTransitQty;
    private Long rejectedQty;
    private String approvalStatus;
    private String categoryType;
    private String rmsInwardId;
    private String navInwardId;
    private Long totalMacSerial;
    private String createdBy;
    private String approvalRemark;
    private Long assignNonSerializedItemQty;
    private Long requestInventoryId;

    @Transient
    private String source;

    @Transient
    private String destination;

    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }
}
