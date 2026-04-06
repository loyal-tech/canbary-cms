package com.adopt.apigw.modules.PartnerLedger.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PartnerLedgerDetailsDTO implements IBaseDto {
    private Long id;
    private String transtype;
    private String transcategory;
    private Double amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createDate;
    private String description;
    private Integer partnerId;
    private Boolean isDeleted = false;
    private Integer custid;
    private Double offerprice;
    private Double agr_amount;
    private Double tds_amount;
    private Double tax;
    private Double commission;
    private String customer_name;
    private String customer_username;
    private Double royalty;
    private String planname;
    private List<String> creditDocNo;
    private String invoiceNo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate END_DATE;
    private Double balAmount;
    private Integer mvnoId;
    Double partnerTax = 0d;
    Double grossOfferPrice = 0d;

    Double royaltyBasePrice = 0d;

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
