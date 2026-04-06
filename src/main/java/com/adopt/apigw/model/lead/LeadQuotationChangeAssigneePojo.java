package com.adopt.apigw.model.lead;

import lombok.Data;

@Data
public class LeadQuotationChangeAssigneePojo {

    private Long leadQuotationDetailId;

    private String status;

    private String remark;

    private String remarkType;

    private Integer assignee;
}
