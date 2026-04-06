package com.adopt.apigw.modules.mvnoDocDetails.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MvnoDocDetailsDTO extends Auditable implements IBaseDto {
    private Long docId;
    private Long mvnoId;
    private String docType;
    private String docSubType;
    private String mode;
    private String remark;
    private String docStatus;
    private String filename;
    private String uniquename;
    private Boolean isDelete = false;
    private String documentNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer nextTeamHierarchyMappingId;
    private Integer nextStaff;
    private String startDateAsString;
    private String endDateAsString;
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return docId;
    }

    @Override
    public Integer getMvnoId() {
        return this.mvnoId.intValue() ;
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId=mvnoId.longValue();
    }

    public MvnoDocDetailsDTO(Long docId, Long mvnoId, String docType, String docSubType, String mode, String remark, String docStatus, String filename, String uniquename, Boolean isDelete, LocalDate startDate, LocalDate endDate, Integer nextTeamHierarchyMappingId, Integer nextStaff, String documentNumber) {
        this.docId = docId;
        this.mvnoId = mvnoId;
        this.docType = docType;
        this.docSubType = docSubType;
        this.mode = mode;
        this.remark = remark;
        this.docStatus = docStatus;
        this.filename = filename;
        this.uniquename = uniquename;
        this.isDelete = isDelete;
        this.documentNumber = documentNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.nextTeamHierarchyMappingId = nextTeamHierarchyMappingId;
        this.nextStaff = nextStaff;
    }
}
