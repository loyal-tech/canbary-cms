package com.adopt.apigw.modules.WorkFlowInProgressEntity.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkFlowInProgressData {

    private Long entityId;
    private String entityName;
    private String tableName;
    private String assignToTeamOrStaff;
    private String staffName;
    private String workflow;
}
