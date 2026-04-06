package com.adopt.apigw.schedulers;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Customer Update Quota", description = "This is data transfer object for customer which is used to update customer data")
public class UpdateCustomerQuotaDto {

	@ApiModelProperty(notes = "Name of the user", required = true)
    private String userName;
    private Double usedQuota;
    private Double usedQuotaKB;
    private Integer custId;
    private Integer quotaDetailId;
    @ApiModelProperty(hidden = true)
    private Long mvnoId;
    private Double usedTimeQuota;
    private Double usedTimeQuotaSec;
    private Boolean skipQuotaUpdate;

}
