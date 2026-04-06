package com.adopt.apigw.model.lead;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TBLMLEADDOCDETAILS")
public class LeadDocDetails {

	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "doc_id")
	private Long docId;
	
	//@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_master_id")
	private LeadMaster leadMaster;
	
	@Column(name = "doc_type")
	private String docType;
	
	@Column(name = "doc_sub_type")
	private String docSubType;
	
	@Column(name = "mode")
	private String mode;
	
	@Column(name = "remark")
	private String remark;
	
	@Column(name = "doc_status")
	private String docStatus;
	
	@Column(name = "filename")
	private String filename;
	
	@Column(name = "uniquename")
	private String uniquename;
	
	@Column(name="is_delete",columnDefinition = "Boolean default false", nullable = false)
	private Boolean isDelete = false;
	
	@Column(name = "STARTDATE")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	
	@Column(name = "ENDDATE")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	
	@Transient
	private String documentNumber;
	
	public LeadDocDetails(LeadDocDetailsDTO dto) {
		this.docId = dto.getDocId();
		this.docType = dto.getDocType();
		this.docSubType = dto.getDocSubType();
		this.mode = dto.getMode();
		this.remark = dto.getRemark();
		this.docStatus = dto.getDocStatus();
		this.filename = dto.getFilename();
		this.uniquename = dto.getUniquename();
		this.isDelete = dto.getIsDelete();
		this.startDate = dto.getStartDate();
		this.endDate = dto.getEndDate();
		if(dto.getLeadMasterId() != null)
			this.leadMaster = new LeadMaster(dto.getLeadMasterId());
	}
}
