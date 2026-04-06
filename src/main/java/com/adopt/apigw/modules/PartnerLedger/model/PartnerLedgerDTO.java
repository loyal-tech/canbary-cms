package com.adopt.apigw.modules.PartnerLedger.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.postpaid.Partner;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class PartnerLedgerDTO implements IBaseDto {
    private Long id;
    private Double totaldue = 0.0;
    private Double totalpaid = 0.0;
    public Integer partnerId;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate createdate;
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate updatedate;
    private Boolean isDeleted = false;
    private Integer mvnoId;

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoId;
	}
}
