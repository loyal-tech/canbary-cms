package com.adopt.apigw.modules.customerDocDetails.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CustomerDocDetailsDTO extends Auditable implements IBaseDto {
    private Long docId;
    private Integer custId;
    private String docType;
    private String docSubType;
    private String remark;
    private String mode;
    private String docStatus;
    private String filename;
    private String uniquename;
    private Boolean isDelete = false;
    private String documentNumber;

    @JsonBackReference
    @ApiModelProperty(hidden = true)
    private CustomersPojo customer;
    
 //   private Integer mvnoId;
    
//    @DateTimeFormat(pattern = "dd-MM-yyyy")
//	@JsonSerialize(using = LocalDateTimeSerializer.class)
//	@JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate startDate;

//    @DateTimeFormat(pattern = "dd-MM-yyyy")
//	@JsonSerialize(using = LocalDateTimeSerializer.class)
//	@JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate endDate;

    private Integer nextTeamHierarchyMappingId;
    private Integer nextStaff;
    private Integer mvnoId;

    private Integer parentStaffId;
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return docId;
    }

	@Override
	public Integer getMvnoId() {
		return mvnoId;
	}

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }


    private Long leadId;
	
	private String startDateAsString;

    private String endDateAsString;
    private Integer staffId;
}
