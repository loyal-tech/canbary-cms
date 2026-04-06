package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.modules.Teams.domain.TeamHierarchyMapping;
import lombok.Data;

import java.util.List;

@Data
public class UpdateTeamHierarchyMappingMessage {
    List<TeamHierarchyMapping> teamHierarchyMappingList;
    private Long hierarchyId;
    private Integer operationId;
    private Boolean isDeleted;
}
