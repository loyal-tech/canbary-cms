package com.adopt.apigw.model.lead;

import lombok.Data;

@Data
public class LeadChangeAssigneePojo {

    private Long leadMasterId;

    private String status;

    private String remark;

    private String remarkType;

    private Integer assignee;
}
