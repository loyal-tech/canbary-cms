package com.adopt.apigw.pojo.api;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.postpaid.PlanService;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlanServiceInventoryMappingPojo implements IBaseDto {

    private Long id;
    private Long pcategoryId;
    private Long planserviceId;
    private Integer mvnoId;

    @Override
    public Long getIdentityKey() {return id;}

    @Override
    public Integer getMvnoId() { return mvnoId; }

    @Override
    public void setMvnoId(Integer mvnoId) { this.mvnoId = mvnoId;  }


}
