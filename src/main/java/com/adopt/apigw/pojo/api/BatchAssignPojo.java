package com.adopt.apigw.pojo.api;

import lombok.Data;

@Data
public class BatchAssignPojo {
    private Integer staffId;
    private Integer nextStaffId;
    private Long batchId;
    private String remark;
}
