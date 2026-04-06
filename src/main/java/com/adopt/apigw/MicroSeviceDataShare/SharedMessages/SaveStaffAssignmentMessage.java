package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.model.common.StaffUserServiceAreaMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveStaffAssignmentMessage {
    private List<StaffUserServiceAreaMapping> mappingList;

    private Integer createdById;

    private Integer updatedById;

    private String createdByName;

    private String lastModifiedByName;

    private Long areaId;

    private Long mvnoId;

    private Boolean staffSAMap = true;
}
