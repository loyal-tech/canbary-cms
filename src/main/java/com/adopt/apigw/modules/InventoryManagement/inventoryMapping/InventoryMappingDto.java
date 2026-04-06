package com.adopt.apigw.modules.InventoryManagement.inventoryMapping;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InventoryMappingDto implements IBaseDto {
    private Long id;

    Long qty;

    Long productId;

//    Integer customerId;
    String ownerType;
    Long ownerId;

    Integer staffId;

    Long inwardId;

    LocalDateTime assignedDateTime;

    private Boolean isDeleted = false;

    private Integer mvnoId;

    String approvalStatus;

    LocalDateTime expiryDateTime;

    private String inwardNumber;
    private String productName;
    private String customerName;
    private boolean hasMac;
    private boolean hasSerial;
    private boolean hasTrackable;
    private boolean hasPort;
    private Integer nextApproverId;
    private Integer teamHierarchyMappingId;
    private String assigneeName;
    private List<InOutWardMACMapping> inOutWardMACMapping;
    private Integer previousApproveId;
    private String approvalRemark;
    private String popName;
    private String serviceAreaName;

    @Override
    public Long getIdentityKey() {
        return this.id;
    }

    @Override
    public Integer getMvnoId() {
        return this.mvnoId;
    }
}
