package com.adopt.apigw.modules.tickets.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.tickets.domain.CaseFeedbackRel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
public class CaseUpdateDTO extends Auditable implements IBaseDto {

    private Long id;
    private Long ticketId;
    private String status;
    private String caseType;
    private Integer assignee;
    private String priority;
    private String attachment;
    private String filename;
    private Integer finalResolutionId;
    private String remarkType;
    private String remark;
    //    private Long reasonId;
    private String commentBy;

    private Boolean isDeleted = false;
    private LocalDate nextFollowupDate;
    private LocalTime nextFollowupTime;
    private String createby;
    private String updateby;
    private LocalDateTime createDateString;
    private String ticketCreateDateString;
    private String updateDateString;
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CaseDTO ticket;
    @JsonManagedReference
    private List<CaseUpdateDetailsDTO> updateDetails = new ArrayList<>();
    private Integer mvnoId;

    private Long ticketReasonCategoryId;
    private Long groupReasonId;
    private Long reasonSubCategoryId;
    private Long tatMappingId;

    private String caseTitle;

    private Long rootCauseReasonId;

    private String subSource;
    private String source;

    private Integer teamHierarchyMappingId;
    private String customerAdditionalMobileNumber;
    private String customerAdditionalEmail;

    private String helperName;
    private Long case_order;

    private Integer caseSlaTime;

    private String caseSlaUnit;
    private Boolean call_status;
    private Boolean is_closed;
    private String deacivate_reason;
    List<CaseFeedbackRel> caseFeedbackRel;
    private String serialNumber;

    public CaseUpdateDTO() {
    }

    public CaseUpdateDTO(CaseUpdateDTO caseUpdateDTO) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("hh:mm:ss");

        this.id = caseUpdateDTO.getId();
        this.ticketId = caseUpdateDTO.getTicketId();
        this.status = caseUpdateDTO.getStatus();
        this.caseType = caseUpdateDTO.getCaseType();
        this.assignee = caseUpdateDTO.getAssignee();
        this.priority = caseUpdateDTO.getPriority();
        this.attachment = caseUpdateDTO.getAttachment();
        this.filename = caseUpdateDTO.getFilename();
        this.finalResolutionId = caseUpdateDTO.getFinalResolutionId();
        this.remarkType = caseUpdateDTO.getRemarkType();
        this.remark = caseUpdateDTO.getRemark();
        this.commentBy = caseUpdateDTO.getCommentBy();
        this.isDeleted = caseUpdateDTO.getIsDeleted();
        if(caseUpdateDTO.getNextFollowupDate() != null) {
            this.nextFollowupDate = LocalDate.parse(caseUpdateDTO.getNextFollowupDate().format(formatter));
        }
        if(caseUpdateDTO.getNextFollowupTime() != null) {
            this.nextFollowupTime = LocalTime.parse(caseUpdateDTO.getNextFollowupTime().format(formatter1));
        }
        this.createby = caseUpdateDTO.getCreatedByName();
        this.updateby = caseUpdateDTO.getLastModifiedByName();
        this.ticketCreateDateString = caseUpdateDTO.getCreateDateString().format(formatter);
        this.updateDateString = caseUpdateDTO.getUpdateDateString();
//        this.ticket = ticket;
//        this.updateDetails = updateDetails;
        this.mvnoId = caseUpdateDTO.getMvnoId();
        this.ticketReasonCategoryId = caseUpdateDTO.getTicketReasonCategoryId();
        this.groupReasonId = caseUpdateDTO.getGroupReasonId();
        this.reasonSubCategoryId = caseUpdateDTO.getReasonSubCategoryId();
        this.tatMappingId = caseUpdateDTO.getTatMappingId();
        this.caseTitle = caseUpdateDTO.getCaseTitle();
        this.rootCauseReasonId = caseUpdateDTO.getRootCauseReasonId();
        this.subSource = caseUpdateDTO.getSubSource();
        this.source = caseUpdateDTO.getSource();
        this.teamHierarchyMappingId = caseUpdateDTO.getTeamHierarchyMappingId();
        this.customerAdditionalMobileNumber = caseUpdateDTO.getCustomerAdditionalMobileNumber();
        this.customerAdditionalEmail = caseUpdateDTO.getCustomerAdditionalEmail();
        this.helperName = caseUpdateDTO.getHelperName();
        this.case_order = caseUpdateDTO.getCase_order();
        this.caseSlaTime = caseUpdateDTO.getCaseSlaTime();
        this.caseSlaUnit = caseUpdateDTO.getCaseSlaUnit();
        this.serialNumber=caseUpdateDTO.getSerialNumber();
    }

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        // TODO Auto-generated method stub
        return mvnoId;
    }
}
