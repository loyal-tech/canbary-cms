package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.tickets.domain.Case;
import com.adopt.apigw.modules.tickets.domain.TicketServicemapping;
import com.adopt.apigw.modules.tickets.model.CaseUpdateDTO;
import lombok.Data;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Data
public class TicketMessageIntegration {

    private Long caseId;
    private String caseTitle;
    private String caseType;
    private String caseNumber;
    private String caseFor;
    private String caseOrigin;
    private String caseStatus;
    private String priority;
    private Integer customers;
    private String caseForPartner;
    private String caseForZone;
    private String nextFollowupDate;
    private String nextFollowupTime;
    private String caseStartedOn;
    private String firstAssignedOn;
    private Boolean isDelete = false;
    private Integer currentAssignee;
    private Long finalResolution;
    private Integer finalResolvedBy;
    private Integer finalClosedBy;
    private String finalResolutionDate;
    private String finalClosedDate;
    private List<CaseUpdateDTO> caseUpdateList = new ArrayList<>();
    private String partner;
    private String firstRemark;
    private Long mobile;
    private String userName;
    private Integer mvnoId;
    private String customerName;
    private String assigneeName;
    private Integer rating;
    private String customerFeedback;
    private Long ticketReasonCategoryId;
    private String ticketReasonCategoryName;
    private Long reasonSubCategoryId;
    private String reasonSubCategoryName;
    private Long groupReasonId;
    private Long tatMappingId;
    private Long buId;
    private Long rootCauseReasonId;
    private String source;
    private String subSource;
    private Long teamHierarchyMappingId;
    //private List<TicketAssignStaffMapping> ticketAssignStaffMappings;
    private String department;
    private String customerAdditionalMobileNumber;
    private String customerAdditionalEmail;
    private Integer parentTicketId;
    private String helperName;
    private Integer lcoId;
    private List<TicketServicemapping> ticketServicemappingList;
    private Long case_order;
    private Integer caseSlaTime;
    private String caseSlaUnit;
    private String createDate;
    private String modifyDate;
    public TicketMessageIntegration() {
    }

    public TicketMessageIntegration(Case casedata , List<CaseUpdateDTO> caseUpdateDTOS, List<TicketServicemapping> ticketServicemappingList) {  //ticket_assign_staff_mappings

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss");

        this.caseId   = casedata.getCaseId();
        this.caseTitle = casedata.getCaseTitle();
        this.caseType   =  casedata.getCaseType();
        this.caseNumber   =  casedata.getCaseNumber();
        this.caseFor   =  casedata.getCaseFor();
        this.caseOrigin   =  casedata.getCaseOrigin();
        this.caseStatus   =  casedata.getCaseStatus();
        this.priority   =  casedata.getPriority();
        this.customers   =  casedata.getCustomers().getId();
        this.caseForPartner   =  casedata.getCaseForPartner();
        this.caseForZone   =  casedata.getCaseForZone();
        if(casedata.getNextFollowupDate() != null) {
            this.nextFollowupDate = casedata.getNextFollowupDate().format(dateformatter);
        }
        if(casedata.getNextFollowupTime() != null) {
            this.nextFollowupTime = casedata.getNextFollowupTime().format(timeFormatter);
        }
        if(casedata.getCaseStartedOn() != null) {
            this.caseStartedOn = casedata.getCaseStartedOn().format(dateTimeFormatter);
        }
        if (casedata.getFirstAssignedOn() != null) {
            this.firstAssignedOn = casedata.getFirstAssignedOn().format(dateTimeFormatter);
        }
        this.isDelete   =  casedata.getIsDelete();

        if(casedata.getCurrentAssignee() != null) {
            this.currentAssignee   =  casedata.getCurrentAssignee().getId();
        }
        if(casedata.getFinalResolution() != null) {
            this.finalResolution   =  casedata.getFinalResolution().getId();
        }
        if(casedata.getFinalResolvedBy() != null) {
            this.finalResolvedBy = casedata.getFinalResolvedBy().getId();
        }
        if(casedata.getFinalClosedBy() != null) {
            this.finalClosedBy = casedata.getFinalClosedBy().getId();
        }

        if(casedata.getFinalResolutionDate() != null) {
            this.finalResolutionDate = casedata.getFinalResolutionDate().format(dateTimeFormatter);
        }
        if(casedata.getFinalClosedDate() != null) {
            this.finalClosedDate = casedata.getFinalClosedDate().format(dateTimeFormatter);
        }

        if(casedata.getCaseUpdateList() != null) {
            this.caseUpdateList = caseUpdateDTOS;
        }

        if(casedata.getPartner().getName() != null) {
            this.partner = casedata.getPartner().getName();
        }

        this.firstRemark   =  casedata.getFirstRemark();
        this.mobile   =  casedata.getMobile();
        this.userName   =  casedata.getCustomers().getUsername();
        this.mvnoId   =  casedata.getMvnoId();
        this.customerName   =  casedata.getCustomerName();
        this.assigneeName   =  casedata.getAssigneeName();
        this.rating   =  casedata.getRating();
        this.customerFeedback   =  casedata.getCustomerFeedback();
        this.ticketReasonCategoryId   =  casedata.getTicketReasonCategoryId();
        this.ticketReasonCategoryName = casedata.getTicketReasonCategoryName();
        this.reasonSubCategoryId   =  casedata.getReasonSubCategoryId();
        this.reasonSubCategoryName = casedata.getReasonSubCategoryName();
        this.groupReasonId   =  casedata.getGroupReasonId();
        this.tatMappingId   =  casedata.getTatMappingId();
        this.buId   =  casedata.getBuId();
        this.rootCauseReasonId   =  casedata.getRootCauseReasonId();
        this.source   =  casedata.getSource();
        this.subSource   =  casedata.getSubSource();
        this.teamHierarchyMappingId   =  casedata.getTeamHierarchyMappingId();
      //  this.ticketAssignStaffMappings   =  ticketAssignStaffMappings;
        this.department   =  casedata.getDepartment();
        this.customerAdditionalMobileNumber   =  casedata.getCustomerAdditionalMobileNumber();
        this.customerAdditionalEmail   =  casedata.getCustomerAdditionalEmail();
        this.parentTicketId   =  casedata.getParentTicketId();
        this.helperName   =  casedata.getHelperName();
        this.lcoId   =  casedata.getLcoId();
        this.ticketServicemappingList   = ticketServicemappingList;
        this.case_order   =  casedata.getCase_order();
        this.caseSlaTime   =  casedata.getCaseSlaTime();
        this.caseSlaUnit   =  casedata.getCaseSlaUnit();
        this.createDate  = casedata.getCreatedate().format(dateTimeFormatter);
        this.modifyDate = casedata.getUpdatedate().format(dateTimeFormatter);
     }
}
