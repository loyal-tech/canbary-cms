package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.model.postpaid.PlanGroupMapping;
import com.adopt.apigw.model.postpaid.PlanGroupMappingChargeRel;
import com.adopt.apigw.model.postpaid.ServiceAreaPlanGroupMapping;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Group_Mapping.ProductPlanGroupMapping;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class SavePlanGroupSharedDataMessage {
    private Integer planGroupId;
    private String planGroupName;
    private String status;
    private Integer mvnoId;
    private String plantype;
    private String planMode;
    private Boolean isDelete;
    private List<PlanGroupMapping> planMappingList;
    private Double dbr;
    private Long buId;
    private String planGroupType;
    private String category;
    private Integer nextTeamHierarchyMappingId;
    private Integer nextStaff;
    private String accessibility;
    private Boolean allowDiscount;
    private Double offerprice;
    private List<ServiceArea> servicearea;
    private List<PlanGroupMappingChargeRel> planGroupMappingChargeRelsList;
    private List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMappingList;
    private List<ProductPlanGroupMapping> productPlanGroupMappingList;
    private Long templateId;
    private Boolean invoiceToOrg;
    private Boolean requiredApproval;
    private Integer createdById;
    private Integer lastModifiedById;
}
