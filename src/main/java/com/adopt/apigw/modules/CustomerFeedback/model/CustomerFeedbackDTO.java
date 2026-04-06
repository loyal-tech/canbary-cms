package com.adopt.apigw.modules.CustomerFeedback.model;

import lombok.Data;

import javax.persistence.Column;

@Data
public class CustomerFeedbackDTO {

    private Long Id;

    private Long custId;

    private String rating;

    private String feedback;

    private String event;

    private Boolean isDelete;

    private Integer mvnoId;

    private Long buId;
}
