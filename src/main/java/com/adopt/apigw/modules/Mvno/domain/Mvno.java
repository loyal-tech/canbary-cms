package com.adopt.apigw.modules.Mvno.domain;
import javax.persistence.*;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.modules.custAccountProfile.CustAccountProfile;
import com.adopt.apigw.modules.mvnoDocDetails.domain.MvnoDocDetails;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@ToString
@Table(name = "tblmmvno")
public class Mvno implements IBaseData {
	
	@Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Long id;
	
	@Column(name = "NAME", nullable = false, length = 64)
    private String name;

	@Column(name = "full_name",nullable = false, length = 255)
	private String fullName;

	@Column(name = "USERNAME", nullable = false, length = 200)
	private String username;

	@Column(name = "PASSWORD", nullable = false, length = 200)
	private String password;
	
	@Column(name = "SUFFIX", nullable = false, length = 16)
    private String suffix;

	@Column(name = "DESCRIPTION", nullable = false, length = 255)
    private String description;
	
	@Column(name = "EMAIL", nullable = false, length = 255)
    private String email;
	
	@Column(name = "PHONE", nullable = false, length = 255)
    private String phone;
    
	@Column(name = "STATUS", nullable = false, length = 40)
	private String status;
	
	@Column(name = "LOGOFILE", nullable = false, length = 255)
	private String logfile;
	
	@Column(name = "MVNOHEADER", nullable = false, length = 255)
	private String mvnoHeader;
	
	@Column(name = "MVNOFOOTER", nullable = false, length = 255)
	private String mvnoFooter;
	
	@Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

	@CreationTimestamp
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
	@Column(name = "CREATEDATE", nullable = false, updatable = false)
	@DiffIgnore
	private LocalDateTime createdate;

	@UpdateTimestamp
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
	@Column(name = "LASTMODIFIEDDATE")
	@DiffIgnore
	private LocalDateTime updatedate;

	@Column(name = "CREATEDBYSTAFFID", nullable = false, length = 40, updatable = false)
	@DiffIgnore
	private Integer createdById;

	@Column(name = "LASTMODIFIEDBYSTAFFID", nullable = false, length = 40)
	@DiffIgnore
	private Integer lastModifiedById;


	@DiffIgnore
	@JsonManagedReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mvno")
	@OrderBy("docId desc")
	private List<MvnoDocDetails> mvnoDocList = new ArrayList<>();

	@Column(name = "cust_invoice_ref_id")
	private Integer custInvoiceRefId;

	@Column(name = "mvno_deactivation_flag")
	private Boolean mvnoDeactivationFlag;
	@Lob
	@Column(name = "logo_image", columnDefinition = "BLOB", length = 30000)
	private byte[] profileImage;

	@Column(name = "logo_file_name", nullable = false, length = 200)
	private String logo_file_name;
	@Column(name = "mvno_payment_due_days")
	private Integer mvnoPaymentDueDays;

	@Column(name = "isp_bill_day")
	private Integer ispBillDay;

	@Column(name = "bill_type")
	private String billType;

	@Column(name = "isp_commission_percentage")
	private Double ispCommissionPercentage;

	@OneToOne(targetEntity = CustAccountProfile.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name = "profile_id", referencedColumnName = "profile_id", nullable = false)
	private CustAccountProfile custAccountProfile;
	@Column(name = "threshold")
//	@Transient
	private Long threshold;
	@JsonIgnore
	@Override
	public Long getPrimaryKey() {
        return id;
	}

	@Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDelete;
    }

}
