package com.adopt.apigw.model.postpaid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.adopt.apigw.pojo.api.CustSpecialPlanMapppingPojo;
import com.adopt.apigw.pojo.api.CustSpecialPlanRelMapppingPojo;
import org.apache.commons.collections4.IterableUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "TBLMCUSTSPECIALPLANRELMAPPING")
@EntityListeners(AuditableListener.class)
@NoArgsConstructor
@AllArgsConstructor
public class CustSpecialPlanRelMappping extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CUSTSPPLANID", nullable = false, length = 40)
	private Long id;

	@ApiModelProperty(notes = "Name of the mapping")
	@Column(name = "mapping_name")
	private String mappingName;

	@Column(name = "status", nullable = false, length = 100)
	private String status;

	@JsonManagedReference
	@OneToMany(mappedBy = "custSpecialPlanRelMappping", cascade = CascadeType.ALL, orphanRemoval=true)
	@LazyCollection(LazyCollectionOption.FALSE)
	Set<CustSpecialPlanMappping> custSpecialPlanMapppingList;
	
	@Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
	private Integer mvnoId;

	@Column(name = "BUID", nullable = false, length = 40, updatable = false)
	private Long buId;

	@Column(name = "next_team_hir_mapping")
	private Integer nextTeamHierarchyMapping;

	@Column(name = "next_staff")
	private Integer nextStaff;

	@Column(name="remarks")
	private String remarks;
	@Transient
	private String flag;
	@Transient
	private String name;
	@Transient
	Set<CustSpecialPlanMapppingPojo> custMapping;
	@Transient

	Set<CustSpecialPlanMapppingPojo> planMapping;
	@Transient

	Set<CustSpecialPlanMapppingPojo> planGroupMapping;
	@Transient

	Set<CustSpecialPlanMapppingPojo> leadCustMapping;

	@Column(name = "mvno_name")
	private String mvnoName;


	public CustSpecialPlanRelMappping(CustSpecialPlanRelMappping custSpecialPlanRelMappping){
		this.id = custSpecialPlanRelMappping.getId();
		this.buId = custSpecialPlanRelMappping.getBuId();
		this.flag = custSpecialPlanRelMappping.getFlag();
		this.remarks = custSpecialPlanRelMappping.getRemarks();
		this.nextStaff = custSpecialPlanRelMappping.getNextStaff();
		this.nextTeamHierarchyMapping = custSpecialPlanRelMappping.getNextTeamHierarchyMapping();
		this.mvnoId = custSpecialPlanRelMappping.getMvnoId();
		this.status = custSpecialPlanRelMappping.getStatus();
		this.mappingName = custSpecialPlanRelMappping.getMappingName();
		this.custSpecialPlanMapppingList=custSpecialPlanRelMappping.getCustSpecialPlanMapppingList();

	}
	public CustSpecialPlanRelMappping(CustSpecialPlanRelMapppingPojo custSpecialPlanRelMapppingPojo){
		this.setId(custSpecialPlanRelMapppingPojo.getId());
		this.setFlag(custSpecialPlanRelMapppingPojo.getFlag());
		this.setStatus(custSpecialPlanRelMapppingPojo.getStatus());
		this.setNextStaff( custSpecialPlanRelMapppingPojo.getNextStaff());
		this.setNextTeamHierarchyMapping(custSpecialPlanRelMapppingPojo.getNextTeamHierarchyMapping());
		this.setMvnoId(custSpecialPlanRelMapppingPojo.getMvnoId());
		this.setMappingName(custSpecialPlanRelMapppingPojo.getName());
		Set<CustSpecialPlanMappping> custSpecialPlanMapppings=new HashSet<>();
		for(CustSpecialPlanMapppingPojo custSpecialPlanRelMapppingPojo1 :IterableUtils.toList(custSpecialPlanRelMapppingPojo.getPlanMapping())) {
			custSpecialPlanMapppings.add(new CustSpecialPlanMappping(custSpecialPlanRelMapppingPojo1));
		}
		this.setName(custSpecialPlanRelMapppingPojo.getName());
		this.setCustSpecialPlanMapppingList(custSpecialPlanMapppings);
		this.setCustMapping(custSpecialPlanRelMapppingPojo.getCustMapping());
		this.setLeadCustMapping(custSpecialPlanRelMapppingPojo.getLeadCustMapping());
		this.setPlanMapping(custSpecialPlanRelMapppingPojo.getPlanMapping());
		this.setPlanGroupMapping(custSpecialPlanRelMapppingPojo.getPlanGroupMapping());




	}

}
