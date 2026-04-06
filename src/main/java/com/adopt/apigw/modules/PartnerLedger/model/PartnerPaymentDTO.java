package com.adopt.apigw.modules.PartnerLedger.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerPayment;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PartnerPaymentDTO implements IBaseDto {

    private Long id;
    private String transcategory;
    private String paymentmode;
    private String refno;
    private Integer partnerId;
    private Double amount = 0.0;
    private String chequenumber;
    private LocalDate chequedate;
    private String remarks;
    private LocalDate paymentdate;
    private String bank_name;
    private String branch_name;
    private Boolean isDeleted = false;
    private Integer mvnoId;
    private Integer nextTeamHierarchyMappingId;
    private Integer nextStaff;
    private String status;
    private Integer credit;

    private String onlinesource;
    private Long sourceBank;
    private Long destinationBank;

    private  String chequedateString;

    private  String paymentdateString;
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

    public PartnerPaymentDTO(PartnerPaymentDTO partnerPaymentDTO) {
        this.id = partnerPaymentDTO.getId();
        this.transcategory = partnerPaymentDTO.getTranscategory();
        this.paymentmode = partnerPaymentDTO.getPaymentmode();
        this.refno = partnerPaymentDTO.getRefno();

        this.partnerId = partnerPaymentDTO.getPartnerId();

        this.amount = partnerPaymentDTO.getAmount();
        this.credit = partnerPaymentDTO.getCredit();
        this.chequenumber = partnerPaymentDTO.getChequenumber();
        this.remarks = partnerPaymentDTO.getRemarks();
        this.bank_name = partnerPaymentDTO.getBank_name();
        this.branch_name = partnerPaymentDTO.getBranch_name();
        if(partnerPaymentDTO.getChequedate()!=null) {
            this.chequedateString = partnerPaymentDTO.getChequedate().toString();
        }
        if(partnerPaymentDTO.getPaymentdate()!=null) {
            this.paymentdateString = partnerPaymentDTO.getPaymentdate().toString();
        }
        this.nextTeamHierarchyMappingId = partnerPaymentDTO.getNextTeamHierarchyMappingId();
        this.nextStaff = partnerPaymentDTO.getNextStaff();
        this.status = partnerPaymentDTO.getStatus();
        this.isDeleted = partnerPaymentDTO.isDeleted;
        this.onlinesource = partnerPaymentDTO.getOnlinesource();
        this.sourceBank = partnerPaymentDTO.getSourceBank();
        this.destinationBank = partnerPaymentDTO.getDestinationBank();
    }
}
