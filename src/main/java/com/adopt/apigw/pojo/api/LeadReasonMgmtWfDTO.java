package com.adopt.apigw.pojo.api;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LeadReasonMgmtWfDTO {

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

    private Long rejectedReasonMasterId;


//    private List<StaffUserPojo> staffUserList;
}
