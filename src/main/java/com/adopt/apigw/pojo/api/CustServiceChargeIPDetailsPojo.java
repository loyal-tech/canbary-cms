package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.common.Auditable;
import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class CustServiceChargeIPDetailsPojo extends Auditable {

    private Integer id;

    private Integer custId;

    private Integer custServiceMappingId;

    private String staticIPAdrress;

    private LocalDateTime staticIPStartDate;

    private LocalDateTime staticIPEndDate;

    private  Integer chargeId;
}
