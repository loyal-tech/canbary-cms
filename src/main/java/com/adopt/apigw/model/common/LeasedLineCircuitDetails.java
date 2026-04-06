package com.adopt.apigw.model.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.pojo.api.LeasedLineCircuitDetailsPojo;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

@Data
@Entity
@Table(name = "TBLLEASEDLINECIRCUITDETAILS")
@EntityListeners(AuditableListener.class)
public class LeasedLineCircuitDetails extends Auditable{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "llcdetailsid", nullable = false, length = 40)
    private Integer id;
	
	@Column(name = "llcidentifier", nullable = false, length = 100)
    private String llcIdentifier;
	
	@Column(name = "llclabel", nullable = false, length = 100)
    private String llcLabel;
	
	@Column(name = "llctype", nullable = false, length = 100)
    private String llcType;
	
	@Column(name = "llcstaticip", nullable = false, length = 100)
    private String llcStaticIP;
	
	@Column(name = "llcdevicetype", nullable = false, length = 100)
    private String llcDeviceType;

	@JsonBackReference
    @ManyToOne
    @JoinColumn(name = "llcustid")
    @ToString.Exclude
    private LeasedLineCustomers leasedLineCustomers;

    @DiffIgnore
    @Column(name = "package_id")
    private Integer packageId;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    
    public LeasedLineCircuitDetails() {}
    
    public LeasedLineCircuitDetails(LeasedLineCircuitDetailsPojo pojo,LeasedLineCustomers leasedLineCustomers) {
		this.id = pojo.getId();
		this.llcIdentifier = pojo.getLlcIdentifier();
		this.llcLabel = pojo.getLlcLabel();
		this.llcType = pojo.getLlcType();
		this.llcStaticIP = pojo.getLlcStaticIP();
		this.llcDeviceType = pojo.getLlcDeviceType();
		this.leasedLineCustomers = leasedLineCustomers;
		this.packageId = pojo.getPackageId();
	}

}
