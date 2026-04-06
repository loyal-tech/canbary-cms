package com.adopt.apigw.modules.PurchaseOrder.DTO;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import lombok.Data;

@Data
public class PurchaseOrderDTO  extends Auditable implements IBaseDto {

    private Long id;
    private String ponumber;
    private Customers custid;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private Long buid;
    private String filename;
    private String uniquename;



    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return mvnoId;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }
}
