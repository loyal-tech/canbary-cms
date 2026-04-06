package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.common.Auditable2;
import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategory;
import com.adopt.apigw.modules.ServiceParameterMapping.model.ServiceParamMappingDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PlanPojo extends Auditable2 {

    private Integer id;

    @NotNull
    private String name;
    private String planTYpe;
    private Integer validity;


    private String quota;
    private String stml;
    private String icname;
    private String iccode;
    private Long businessunitid;
    private BusinessUnit businessUnit;
    private Boolean isQoSV = true;
    private String expiry;
    private String ledgerId;
    private List<Long> pcategoryId;
    private List<ServiceParamMappingDTO> serviceParamMappingList;
    private boolean is_dtv;
    //    private ProductCategory productCategory;
    private Long investmentid;
    private List<ProductCategory> productCategory;

    public boolean getis_dtv() {
        return is_dtv;
    }

    public void setis_dtv(boolean is_dtv) {
        this.is_dtv = is_dtv;
    }

    private Integer displayId;
    private String displayName;

    private Boolean feasibility;
    private Boolean poc;
    private Boolean installation;
    private Boolean provisioning;
    private Boolean isPriceEditable;
    private Long feasibilityTeamId;
    private Long pocTeamId;
    private Long installationTeamId;
    private Long provisioningTeamId;
    private Boolean isServiceThroughLead;
    private  String serviceParamName;
    private String mvnoName;
}
