package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMapping;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInventoryMappingMessage {

    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private List<Long> ids;
    private String status;
    private Map<String,Object> customerInventoryData = new HashMap<>();

    public CustomerInventoryMappingMessage(String status, List<Long> custInvId) {
        this.setMessage(message);
        this.setMessageDate(new Date());
        this.messageId = UUID.randomUUID().toString();
        this.ids = custInvId;
        this.status = status;
        customerInventoryData.put("ids",custInvId);
        customerInventoryData.put("status",status);
        this.setCustomerInventoryData(customerInventoryData);
    }
    public CustomerInventoryMappingMessage(CustomerInventoryMapping customerInventoryMapping, String message, boolean isUpdate) {
        this.setMessage(message);
        this.setMessageDate(new Date());
        this.messageId = UUID.randomUUID().toString();
        customerInventoryData.put("id",customerInventoryMapping.getId());
        customerInventoryData.put("qty",customerInventoryMapping.getQty());
        customerInventoryData.put("productId",customerInventoryMapping.getProduct().getId());
        customerInventoryData.put("custId",customerInventoryMapping.getCustomer().getId());
        customerInventoryData.put("staffId",customerInventoryMapping.getStaff().getId());
        customerInventoryData.put("inwardId",customerInventoryMapping.getInwardId());
        customerInventoryData.put("mvnoId",customerInventoryMapping.getMvnoId());
        customerInventoryData.put("isDeleted",customerInventoryMapping.getIsDeleted());
        customerInventoryData.put("status",customerInventoryMapping.getStatus());
        customerInventoryData.put("nextApprover",customerInventoryMapping.getNextApprover());
        customerInventoryData.put("teamHierarchyMappingId",customerInventoryMapping.getTeamHierarchyMappingId());
        customerInventoryData.put("previousApproveId",customerInventoryMapping.getPreviousApproveId());
        customerInventoryData.put("externalItemId",customerInventoryMapping.getExternalItemId());
        customerInventoryData.put("serviceId",customerInventoryMapping.getServiceId());
        customerInventoryData.put("custPackId",customerInventoryMapping.getCustPackId());
        customerInventoryData.put("itemId",customerInventoryMapping.getItemId());
        customerInventoryData.put("itemAssemblyId",customerInventoryMapping.getItemAssemblyId());
        customerInventoryData.put("connectionNo",customerInventoryMapping.getConnectionNo());
        customerInventoryData.put("isInvoiceCreated",customerInventoryMapping.getIsInvoiceCreated());
        customerInventoryData.put("planId",customerInventoryMapping.getPlanId());
        customerInventoryData.put("replacementReason",customerInventoryMapping.getReplacementReason());
        customerInventoryData.put("mapping_ref_id",customerInventoryMapping.getMapping_ref_id());
        customerInventoryData.put("approvalRemark",customerInventoryMapping.getApprovalRemark());
        customerInventoryData.put("discount",customerInventoryMapping.getDiscount());
        customerInventoryData.put("billTo",customerInventoryMapping.getBillTo());
        customerInventoryData.put("newAmount",customerInventoryMapping.getNewAmount());
        customerInventoryData.put("offerPrice",customerInventoryMapping.getOfferPrice());
        customerInventoryData.put("isInvoiceToOrg",customerInventoryMapping.getIsInvoiceToOrg());
        customerInventoryData.put("chargeId",customerInventoryMapping.getChargeId());
        customerInventoryData.put("planGroupId",customerInventoryMapping.getPlanGroupId());
        customerInventoryData.put("isRequiredApproval",customerInventoryMapping.getIsRequiredApproval());
        customerInventoryData.put("isFree",customerInventoryMapping.getIsFree());
        customerInventoryData.put("paymentOwnerId",customerInventoryMapping.getPaymentOwnerId());
        customerInventoryData.put("ezyBillStockId",customerInventoryMapping.getEzyBillStockId());
        customerInventoryData.put("billabecustId",customerInventoryMapping.getBillabecustId());
        customerInventoryData.put("pairStatus",customerInventoryMapping.getPairStatus());
        if(customerInventoryMapping.getAssignedDateTime()!=null) {
            customerInventoryData.put("assignedDateTime", customerInventoryMapping.getAssignedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if(customerInventoryMapping.getExpiryDateTime()!=null) {
            customerInventoryData.put("expiryDateTime", customerInventoryMapping.getExpiryDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if(customerInventoryMapping.getExpDate()!=null) {
            customerInventoryData.put("expDate", customerInventoryMapping.getExpDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        customerInventoryData.put("isUpdate", isUpdate);
    }
}
