package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.common.BatchPayment;
import com.adopt.apigw.model.common.StaffUser;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BatchPaymentAssignmentPojo
{
    private Long batchId;

    private Integer staffUserId;

    private Integer nextStaffUserId;

    private String remark;

    private String status;

    private String assignedStatus;
}
