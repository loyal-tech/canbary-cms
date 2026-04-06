package com.adopt.apigw.modules.cafRejectReason.DTO;

import com.adopt.apigw.core.dto.IBaseDto2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.cafRejectReason.Entity.RejectReason;
import com.adopt.apigw.modules.cafRejectReason.Entity.RejectSubReason;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejectReasonDto extends Auditable<RejectReason> implements IBaseDto2 {

    private Long id;

    private String name;

    private String status;

    private List<RejectSubReasonDto> rejectSubReasonDtoList;

    private List<Long> rejectSubReasonDeletedIds;

    private Boolean isDelete = false;

    private Integer mvnoId;

    private Long buId;

    private LocalDateTime createdate;

    private LocalDateTime updatedate;

    private String createdByName;

    private String lastModifiedByName;

    private Integer createdById;

    private Integer lastModifiedById;



    public RejectReasonDto(RejectReason rejectReason) {
        this.id = rejectReason.getId();
        this.name = rejectReason.getName();
        this.status = rejectReason.getStatus();
        this.isDelete = rejectReason.getIsDelete();
        this.mvnoId = rejectReason.getMvnoId();
        this.buId = rejectReason.getBuId();
        if(rejectReason.getRejectSubReasonList() != null && rejectReason.getRejectSubReasonList().size() > 0) {
            List<RejectSubReasonDto> rejectSubReasonDtoList = new ArrayList<RejectSubReasonDto>();
            for (RejectSubReason rejectSubReason : rejectReason.getRejectSubReasonList()) {
                if(rejectSubReason.getIsDelete() == false)
                    rejectSubReasonDtoList.add(new RejectSubReasonDto(rejectSubReason));
            }
            this.rejectSubReasonDtoList = rejectSubReasonDtoList;
        }
        this.createdate = rejectReason.getCreatedate();
        this.updatedate = rejectReason.getUpdatedate();
        this.createdByName = rejectReason.getCreatedByName();
        this.lastModifiedByName = rejectReason.getLastModifiedByName();
        this.createdById = rejectReason.getCreatedById();
        this.lastModifiedById = rejectReason.getLastModifiedById();
    }
    @Override
    public Long getIdentityKey() {
        return this.id;
    }

}
