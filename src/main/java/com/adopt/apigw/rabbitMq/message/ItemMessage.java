package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMapping;
import com.adopt.apigw.modules.InventoryManagement.item.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;

    private Map<String,Object> customerInventoryData = new HashMap<>();
    public ItemMessage(Item item, String message) {
        this.setMessage(message);
        this.setMessageDate(new Date());
        this.messageId = UUID.randomUUID().toString();
        customerInventoryData.put("id",item.getId());
        customerInventoryData.put("name",item.getName());
        customerInventoryData.put("macAddress",item.getMacAddress());
        customerInventoryData.put("serialNumber",item.getSerialNumber());
        customerInventoryData.put("mvnoId",item.getMvnoId());
        customerInventoryData.put("condition",item.getCondition());
        customerInventoryData.put("productId",item.getProductId());
        customerInventoryData.put("currentInwardId",item.getCurrentInwardId());
        customerInventoryData.put("ownerId",item.getOwnerId());
        customerInventoryData.put("ownerType",item.getOwnerType());
        customerInventoryData.put("warrantyPeriod",item.getWarrantyPeriod());
        customerInventoryData.put("warranty",item.getWarranty());
        customerInventoryData.put("currentInwardType",item.getCurrentInwardType());
        customerInventoryData.put("itemStatus",item.getItemStatus());
        customerInventoryData.put("remainingDays",item.getRemainingDays());
        customerInventoryData.put("isDeleted",item.getIsDeleted());
        customerInventoryData.put("ownershipType",item.getOwnershipType());
        customerInventoryData.put("externalItemId",item.getExternalItemId());
        customerInventoryData.put("intransiantWarrenty",item.getIntransiantWarrenty());
        customerInventoryData.put("intransiantWarrentyStatus",item.getIntransiantWarrentyStatus());
        customerInventoryData.put("intransiantOwnership",item.getIntransiantOwnership());
        customerInventoryData.put("removeFrom",item.getRemoveFrom());
        if(item.getExpireDate() != null) {
            customerInventoryData.put("expireDate", item.getExpireDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        customerInventoryData.put("intransiantexpireDate",item.getIntransiantexpireDate());
        customerInventoryData.put("remarks",item.getRemarks());
    }
}
