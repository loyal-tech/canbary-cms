package com.adopt.apigw.modules.cafRejectReason.DTO;

import com.adopt.apigw.core.dto.IBaseDto2;
import com.adopt.apigw.modules.cafRejectReason.Entity.RejectSubReason;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectSubReasonDto implements IBaseDto2 {

    private Long id;

    private String name;

    private Long rejectReasonId;

    private Boolean isDelete = false;

    public RejectSubReasonDto(RejectSubReason rejectSubReason) {
        this.id = rejectSubReason.getId();
        this.name = rejectSubReason.getName();
        this.isDelete = rejectSubReason.getIsDelete();
        if(rejectSubReason.getRejectReason() != null)
            this.rejectReasonId = rejectSubReason.getRejectReason().getId();
    }

    @Override
    public Long getIdentityKey() {
        return null;
    }

    @Override
    public Integer getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {

    }

    @Override
    public Long getBuId() {
        return null;
    }
}