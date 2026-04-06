package com.adopt.apigw.modules.cafRejectReason.Entity;

import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.modules.cafRejectReason.DTO.RejectSubReasonDto;
import com.adopt.apigw.modules.cafRejectReason.Entity.RejectReason;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLTCAFREJECTSUBREASON")
public class RejectSubReason implements IBaseData2 {

    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reject_sub_reason_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reject_reason_id")
    private RejectReason rejectReason;

    @Column(name = "is_delete",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;

    public RejectSubReason(RejectSubReasonDto rejectSubReasonDto) {
        this.id = rejectSubReasonDto.getId();
        this.name = rejectSubReasonDto.getName();
        this.isDelete = rejectSubReasonDto.getIsDelete();
        if(rejectSubReasonDto.getRejectReasonId() != null)
            this.rejectReason = new RejectReason(rejectSubReasonDto.getRejectReasonId());
    }
    public RejectSubReason(Long id) {
        this.id = id;
    }

    @Override
    @JsonIgnore
    public Serializable getPrimaryKey() {
        return this.id = id;
    }

    @Override
    @JsonIgnore
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    @JsonIgnore
    public boolean getDeleteFlag() {
        return false;
    }

    @Override
    @JsonIgnore
    public void setBuId(Long buId) {

    }
}