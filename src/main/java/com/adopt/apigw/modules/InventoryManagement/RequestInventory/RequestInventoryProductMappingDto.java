package com.adopt.apigw.modules.InventoryManagement.RequestInventory;
import com.adopt.apigw.core.dto.IBaseDto;
import lombok.Data;
@Data
public class RequestInventoryProductMappingDto  implements IBaseDto {


    private Long id;
//
    private Long inventoryRequestId;
    private Long productCategoryId;
//
//    private String productCategoryName;
    private Long productId;
//    private String productName;
    private Long quantity;
    private String itemType;
//    private String requestStatus;
//
//    private boolean isOutWardCreated=false;
//
//
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }
}
