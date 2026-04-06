package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Auditable;
import lombok.Data;

@Data
public class BranchServiceAreaMappingPojo extends Auditable {
    private Long id;

    private Integer branchId;

    private Integer serviceareaId;

}
