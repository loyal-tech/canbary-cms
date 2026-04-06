package com.adopt.apigw.model.lead;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.adopt.apigw.rabbitMq.message.LeadDocDetailsDTOMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadDocDetailsDTO {

	private Long docId;
    
    private String docType;
    
    private String docSubType;
    
    private String remark;
    
    private String mode;
    
    private String docStatus;
    
    private String filename;
    
    private String uniquename;
    
    private Boolean isDelete = false;
    
    private String documentNumber;

	private Long leadMasterId;
        
    private LocalDate startDate;

    private LocalDate endDate;
	private Integer staffId;
    
    public LeadDocDetailsDTO(LeadDocDetails leadDocDetails) {
    	this.docId = leadDocDetails.getDocId();
		this.docType = leadDocDetails.getDocType();
		this.docSubType = leadDocDetails.getDocSubType();
		this.mode = leadDocDetails.getMode();
		this.remark = leadDocDetails.getRemark();
		this.docStatus = leadDocDetails.getDocStatus();
		this.filename = leadDocDetails.getFilename();
		this.uniquename = leadDocDetails.getUniquename();
		this.isDelete = leadDocDetails.getIsDelete();
		this.startDate = leadDocDetails.getStartDate();
		this.endDate = leadDocDetails.getEndDate();
		if(leadDocDetails.getLeadMaster() != null)
			this.leadMasterId = leadDocDetails.getLeadMaster().getId();
    }
    
    public LeadDocDetailsDTO(LeadDocDetailsDTOMessage leadDocDetails) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    	this.docId = leadDocDetails.getDocId();
		this.docType = leadDocDetails.getDocType();
		this.docSubType = leadDocDetails.getDocSubType();
		this.mode = leadDocDetails.getMode();
		this.remark = leadDocDetails.getRemark();
		this.docStatus = leadDocDetails.getDocStatus();
		this.filename = leadDocDetails.getFilename();
		this.uniquename = leadDocDetails.getUniquename();
		this.isDelete = leadDocDetails.getIsDelete();
		if (leadDocDetails.getStartDate() != null) {
			this.startDate = LocalDate.parse(leadDocDetails.getStartDate(),formatter);
		}
		if (leadDocDetails.getEndDate() != null) {
			this.endDate = LocalDate.parse(leadDocDetails.getEndDate(),formatter);
		}
		this.leadMasterId = leadDocDetails.getLeadMasterId();

    }
}
