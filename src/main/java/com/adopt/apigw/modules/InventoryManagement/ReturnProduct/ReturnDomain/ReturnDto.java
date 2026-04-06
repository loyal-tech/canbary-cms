package com.adopt.apigw.modules.InventoryManagement.ReturnProduct.ReturnDomain;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReturnDto implements IBaseDto {

    private Long id;
    private String product_name;
    private String mac_name;
    private String serial_no;
    private String item_condition;
    private Integer mvnoId;
    private Long product_id;
    private Long current_inward_id;
    private String current_inward_type;
    private String item_status;
    private Long cust_id;
    private LocalDateTime createdate;
    private LocalDateTime updatedate;
    private String createdByName;
    private String lastModifiedByName;
    private Integer createdById;
    private Integer lastModifiedById;

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
