package com.adopt.apigw.modules.cafRejectReason.Entity;

import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.cafRejectReason.DTO.RejectReasonDto;
import com.adopt.apigw.modules.cafRejectReason.DTO.RejectSubReasonDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLMCAFREJECTREASON")
public class RejectReason extends Auditable implements IBaseData2 {

    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reject_reason_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "status")
    private String status;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "rejectReason")
    private List<RejectSubReason> rejectSubReasonList = new ArrayList<>();

    @Column(name = "is_delete",columnDefinition = "Boolean default false")
    private Boolean isDelete;

    @DiffIgnore
    @Column(name = "mvno_id")
    private Integer mvnoId;

    @DiffIgnore
    @Column(name = "bu_id")
    private Long buId;

    /*@DiffIgnore
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @DiffIgnore
    @Column(name = "LASTMODIFIEDDATE")
    private LocalDateTime updatedate;

    @DiffIgnore
    @Column(name = "createbyname", nullable = false, length = 40, updatable = false)
    private String createdByName;

    @DiffIgnore
    @Column(name = "updatebyname", nullable = false, length = 40)
    private String lastModifiedByName;

    @DiffIgnore
    @Column(name = "CREATEDBYSTAFFID", nullable = false, length = 40, updatable = false)
    private Integer createdById;

    @DiffIgnore
    @Column(name = "LASTMODIFIEDBYSTAFFID", nullable = false, length = 40)
    private Integer lastModifiedById;*/



    public RejectReason(RejectReasonDto rejectReasonDto) {
        this.id = rejectReasonDto.getId();
        this.name = rejectReasonDto.getName();
        this.status = rejectReasonDto.getStatus();
        this.mvnoId = rejectReasonDto.getMvnoId();
        this.buId = rejectReasonDto.getBuId();
        this.isDelete = rejectReasonDto.getIsDelete();
        if(rejectReasonDto.getRejectSubReasonDtoList() != null && rejectReasonDto.getRejectSubReasonDtoList().size() > 0) {
            List<RejectSubReason> rejectSubReasonList = new ArrayList<RejectSubReason>();
            for (RejectSubReasonDto rejectSubReasonDto : rejectReasonDto.getRejectSubReasonDtoList()) {
                rejectSubReasonList.add(new RejectSubReason(rejectSubReasonDto));
            }
            this.rejectSubReasonList = rejectSubReasonList;
        }
        this.setCreatedate(rejectReasonDto.getCreatedate());
        this.setUpdatedate(rejectReasonDto.getUpdatedate());
        this.createdByName = rejectReasonDto.getCreatedByName();
        this.lastModifiedByName = rejectReasonDto.getLastModifiedByName();
        this.createdById = rejectReasonDto.getCreatedById();
        this.lastModifiedById = rejectReasonDto.getLastModifiedById();
    }

    public RejectReason(Long id) {
        this.id = id;
    }


    @Override
    @JsonIgnore
    public Serializable getPrimaryKey() {
        return this.id;
    }

    @Override
    @JsonIgnore
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;

    }

    @Override
    @JsonIgnore
    public boolean getDeleteFlag() {
        return isDelete;
    }
}