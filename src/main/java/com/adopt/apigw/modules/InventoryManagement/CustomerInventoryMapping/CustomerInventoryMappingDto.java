package com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.modules.InventoryManagement.ExternalItemMacSerialMapping.ExternalItemMacSerialMapping;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.adopt.apigw.modules.InventoryManagement.ItemGroup.ItemAssembly;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class    CustomerInventoryMappingDto implements IBaseDto,Cloneable {
    private Long id;

    Long qty;

    Long productId;

    Integer customerId;

    Integer staffId;

    Long inwardId;

    LocalDateTime assignedDateTime;

    private Boolean isDeleted = false;

    private Integer mvnoId;

    String status;

    LocalDateTime expiryDateTime;

    private String inwardNumber;
    private String externalItemNumber;
    private Long externalItemId;
    private String productName;
    private String customerName;
    private boolean hasMac;
    private boolean hasSerial;
    private boolean hasTrackable;
    private boolean hasPort;
    private boolean hasCas;
    private Integer nextApproverId;
    private Integer teamHierarchyMappingId;
    private String assigneeName;
    private List<InOutWardMACMapping> inOutWardMACMapping;
    private List<ExternalItemMacSerialMapping> externalItemMacSerialMappings;
    private Integer previousApproveId;

    private Long serviceId;

    private Long custPackId;

    private Long itemId;

    private String serviceName;

    private String currentPlan;

    private String itemType;

    private String warranty;

    private boolean itemAssemblyflag;

    private String itemAssemblyStatus;

    private String itemAssemblyName;

    private Long itemAssemblyId;

    private Long custInventoryListId;

    private ItemAssembly itemAssembly;

    private String connectionNo;

    private Boolean isInvoiceCreated = false;
    private Long planId;

    private Long mapping_ref_id;

    private String approvalRemark;

     private String customerFirstName;
     private String customerLastName;

     private String itemwarranty;
     private LocalDateTime expDate;
     private String serviceAreaName;
     private String dtvCategory;
    private String flag;
    private Double discount;
    private String billTo = "CUSTOMER";
    private Double newAmount = 0d;
    private Double offerPrice;
    private Boolean isInvoiceToOrg = false;
    private Boolean isRequiredApproval = false;
    private Long chargeId;
    private Long planGroupId;
    private Boolean isFree = false;
    private Long paymentOwnerId;
    private Long ezyBillStockId;
    private Long billabecustId;

    private String replacementReason;

    private Long revisedCharge;

    private  boolean isGenerateRemoveRequest;
    private String removeRequestStatus;
    private String pairStatus;

    private String productType;
    private String productCategoryName;

    private Long productPlanMappingId;
    private String nonSerializedItemRemark;

    @Override
    public Long getIdentityKey() {
        return this.id;
    }

    @Override
    public Integer getMvnoId() {
        return this.mvnoId;
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
