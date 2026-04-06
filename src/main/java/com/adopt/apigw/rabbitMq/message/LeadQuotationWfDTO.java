package com.adopt.apigw.rabbitMq.message;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LeadQuotationWfDTO {

    private Long quotationId;
    private Long leadMasterId;
    private String firstName;
    private String status;
    private Long mvnoId;
    private Long buId;
    private Integer nextApproveStaffId;
    private Integer nextTeamMappingId;
    private String flag;
    private String remark;
    private Integer currentLoggedInStaffId;
    private String teamName;
    private Boolean finalApproved = false;
    private Boolean approveRequest = false;
    private Long rejectedReasonMasterId;
    private String remarkType;

}
