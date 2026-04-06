package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.pojo.api.TeamHierarchyDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendTeamHierarchyDTO {

    private String messageId;
    private String message;
    private String sourceName;
    private Date messageDate;
    private List<TeamHierarchyDTO> teamHierarchyDTO;

    public SendTeamHierarchyDTO(List<TeamHierarchyDTO> teamHierarchyDTO) {
        this.messageDate = new Date();
        this.messageId = UUID.randomUUID().toString();
        this.message = "Getting suitable staff for lead management approval";
        this.teamHierarchyDTO = teamHierarchyDTO;
    }
}
