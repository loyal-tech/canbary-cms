package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.pojo.api.PlanGroupDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlanGroupMsg {

    private Integer planGroupId;
    private String planGroupName;
    private String status;
   // private ServiceArea servicearea;
   // private Boolean isDelete;
    private String plantype;
    private String planMode;
    private Integer mvnoId;
    private Long buId;
    //private Double dbr;
    private String planGroupType;
    private String category;
    private String accessibility;
    private Boolean allowdiscount;
    private Double offerprice;
    private Boolean invoiceToOrg;
    private Boolean requiredApproval;

    public PlanGroupMsg(PlanGroupDTO planGroupDTO){
        this.planGroupId = planGroupDTO.getPlanGroupId();
        this.planGroupName = planGroupDTO.getPlanGroupName();
        this.status = planGroupDTO.getStatus();
        this.plantype = planGroupDTO.getPlanType();
        this.planMode = planGroupDTO.getPlanMode();
        this.mvnoId = planGroupDTO.getMvnoId();
        this.buId = planGroupDTO.getBuId();
        this.planGroupType = planGroupDTO.getPlanGroupType();
        this.category = planGroupDTO.getCategory();
        this.accessibility = planGroupDTO.getAccessibility();
        this.allowdiscount = planGroupDTO.getAllowdiscount();
        this.offerprice = planGroupDTO.getOfferprice();
        this.invoiceToOrg = planGroupDTO.getInvoiceToOrg();
        this.requiredApproval = planGroupDTO.getRequiredApproval();


    }



}
