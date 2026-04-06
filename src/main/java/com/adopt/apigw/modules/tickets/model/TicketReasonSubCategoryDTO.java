package com.adopt.apigw.modules.tickets.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.core.dto.IBaseDto2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.tickets.domain.TicketReasonCategory;
import com.adopt.apigw.modules.tickets.domain.TicketSubCategoryGroupReasonMapping;
import com.adopt.apigw.modules.tickets.domain.TicketSubCategoryReasonCategoryMapping;
import com.adopt.apigw.modules.tickets.domain.TicketSubCategoryTatMapping;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TicketReasonSubCategoryDTO extends Auditable implements IBaseDto2 {


    Long id;

    String subCategoryName;

    //TicketReasonCategory parentCategory;

  //  List<TicketSubCategoryGroupReasonMapping> ticketSubCategoryGroupReasonMappingList;

    List<TicketSubCategoryTatMapping>ticketSubCategoryTatMappingList;

    Integer mvnoId;

    Boolean isDeleted = false;
    String status;

    List<TicketSubCategoryReasonCategoryMapping> ticketSubCategoryReasonCategoryMappingList;

    private Long buId;

    private Integer lcoId;

    public Long getBuId() {
        return buId;
    }

    public void setBuId(Long buId) {
        this.buId = buId;
    }


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
