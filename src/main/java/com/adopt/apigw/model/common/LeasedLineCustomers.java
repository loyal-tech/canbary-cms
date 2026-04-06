package com.adopt.apigw.model.common;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

@Data
@Entity
@Table(name = "TBLLEASEDLINECUSTOMERS")
@EntityListeners(AuditableListener.class)
public class LeasedLineCustomers extends Auditable{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "llcustid", nullable = false, length = 40)
    private Integer id;
	
	@Column(name = "name", nullable = false, length = 100)
    private String name;
	
	@Column(name = "email", nullable = false, length = 100)
    private String email;
	
	@Column(name = "businessname", nullable = false, length = 100)
    private String businessName;
	
	@Column(name = "billingaddress", nullable = false, length = 255)
    private String billingAddress;
	
	@Column(name = "technicalpersonname", nullable = false, length = 100)
    private String technicalPersonName;
	
	@Column(name = "technicalpersoncontactno", nullable = false, length = 100)
    private String technicalPersonContactNo;
	
	@Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @DiffIgnore
	@Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @DiffIgnore
	@JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "leasedLineCustomers", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    @ToString.Exclude
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<LeasedLineCircuitDetails> llcDetailsList = new ArrayList<>();

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

	public LeasedLineCustomers() {
    }

    public LeasedLineCustomers(Integer id) {
        this.id = id;
    }

}
