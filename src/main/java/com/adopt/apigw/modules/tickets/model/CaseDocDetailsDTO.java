package com.adopt.apigw.modules.tickets.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;


@Data
public class CaseDocDetailsDTO extends Auditable implements IBaseDto {
    private Long docId;
    private Integer ticketId;

    private String remark;
    private String docStatus;
    private String filename;
    private String uniquename;
    private Boolean isDelete = false;
    private Integer mvnoId;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return docId;
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
