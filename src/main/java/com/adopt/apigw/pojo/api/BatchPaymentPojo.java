package com.adopt.apigw.pojo.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

@Data
public class BatchPaymentPojo
{
    private Long id;

    private String batchname;

    private List<BatchPaymentMappingPojo> batchPaymentMappingList=new ArrayList<>();

    @ApiModelProperty(notes = "Batch Payment CreatedBy",hidden = true)
    private String createBy;

    @ApiModelProperty(notes = "Batch Payment Assigned or not",allowableValues = "Not Assigned, Assigned")
    private String assignedStatus;
}
