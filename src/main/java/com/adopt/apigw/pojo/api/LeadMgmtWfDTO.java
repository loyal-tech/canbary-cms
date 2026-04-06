package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.lead.LeadMaster;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.util.List;

@Data
@Getter
@Setter
public class LeadMgmtWfDTO {

    private Long id;

    private String username;

    private String firstname;

    private String status;

    private Long mvnoId;

    private Long buId;

    private Integer nextApproveStaffId;

    private Integer nextTeamMappingId;

    private Long serviceareaid;

    private String flag;

    private String remark;

    private Integer currentLoggedInStaffId;

    private String  teamName;

    private boolean finalApproved;

    private boolean isApproveRequest;

    private String remarkType;

    private String oldValue;

    private String newValue;


    private String EntityType;


    private String operation;


    private String createDateString;


    private String updateDateString;


    private String createdBy;


    private String lastUpdatedBy;

    private Boolean isForLeadAssign;

    private Long rejectedReasonMasterId;

    private String leadStatus;
    private String nextfollowupdate;
    private String nextfollowuptime;
    private Boolean isLeadFromCWSC;
    public LeadMgmtWfDTO() {

    }

    public LeadMgmtWfDTO(LeadMaster leadMaster) {
        this.id = leadMaster.getId();
        this.username = leadMaster.getUsername();
        this.firstname = leadMaster.getFirstname();
        this.status = leadMaster.getStatus();
        this.mvnoId = leadMaster.getMvnoId();
        this.buId = leadMaster.getBuId();
        this.nextApproveStaffId = leadMaster.getNextApproveStaffId();
        this.nextTeamMappingId = leadMaster.getNextTeamMappingId();
        this.serviceareaid = leadMaster.getServiceareaid();
        this.flag = "approved";
        this.remark = leadMaster.getRemarks();
        this.currentLoggedInStaffId = getCurrentLoggedInStaffId();
       // this.teamName = leadMaster;
        this.finalApproved = false;
        this.isApproveRequest = true;
        this.remarkType = null;
        this.oldValue = null;
        this.newValue = null;
        this.EntityType = leadMaster.getLeadType();
       // this.operation = operation;
        this.createDateString = leadMaster.getCreateDateString();
        this.updateDateString = leadMaster.getUpdateDateString();
        this.createdBy = leadMaster.getCreatedBy();
        this.lastUpdatedBy = leadMaster.getLastModifiedBy();
        this.isForLeadAssign = false;
        if(leadMaster.getNextfollowupdate()!=null) {
            this.nextfollowupdate = leadMaster.getNextfollowupdate().toString();
        }
        if(leadMaster.getNextfollowuptime()!=null) {
            this.nextfollowuptime = leadMaster.getNextfollowuptime().toString();
        }
        //this.rejectedReasonMasterId = leadMaster.getRejectReasonId().longValue();
    }
//    private List<StaffUserPojo> staffUserList;
}
