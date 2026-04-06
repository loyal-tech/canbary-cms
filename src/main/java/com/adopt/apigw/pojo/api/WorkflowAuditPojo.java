package com.adopt.apigw.pojo.api;

import lombok.Data;

import javax.persistence.Column;

@Data
public class WorkflowAuditPojo {
    private Long id;
    private Integer eventid;
    private Integer customersCAFId;
    private String customersCAFName;
    private Integer planId;
    private String planName;
    private Long custPackageId;
    private Integer creditDocId;
    private Integer staffUser;
    private String staffUserName;
    private Integer nextStaffUser;
    private String nextStaffUserName;
    private String remark;
    private String status;
    private String assignedDate;
    private String nextStaffUserStatus;
    private String nextStaffAssignedDate;

    private Integer custId;
    private String approvalStatus;
}
