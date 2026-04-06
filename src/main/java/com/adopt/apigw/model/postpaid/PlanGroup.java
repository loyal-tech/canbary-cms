package com.adopt.apigw.model.postpaid;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Group_Mapping.ProductPlanGroupMapping;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;

import com.adopt.apigw.modules.servicePlan.domain.Services;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;


@Data
@Entity
@Table(name = "tblmplangroup")
public class PlanGroup  extends Auditable{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plangroupid")
    private Integer planGroupId;
	
	@Column(name = "plangroupname", nullable = false, length = 40)
	private String planGroupName;
	
	@Column(name = "status", nullable = false, length = 40)
	private String status;
	
	@DiffIgnore
	@Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
	private Integer mvnoId;
	
//	@ManyToOne
//    @JoinColumn(name = "servicearea_id")
//    private ServiceArea servicearea;

	@Column(name = "plantype", nullable = false, length = 40)
    private String plantype;

	@Column(name = "planmode", nullable = false, length = 50)
	private String planMode;
	
	@Column(name = "is_deleted",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;;
	

	@DiffIgnore
	@OneToMany(targetEntity = PlanGroupMapping.class, cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "plangroupid", referencedColumnName = "plangroupid")
	@ToString.Exclude
	@JsonManagedReference
	private List<PlanGroupMapping> planMappingList = new ArrayList<>();

	@DiffIgnore
	@Column(name = "dbr", nullable = true)
	private Double dbr;

	@DiffIgnore
	@Column(name = "BUID", nullable = false, length = 40, updatable = false)
	private Long buId;
	
	@Column(name = "plangrouptype", nullable = false, length = 100)
    private String planGroupType;

	@Column(name = "PLANCATEGORY", nullable = false, length = 40)
	private String category;

	@DiffIgnore
	@Column(name = "next_team_hir_mapping")
	private Integer nextTeamHierarchyMappingId;

	@DiffIgnore
	@Column(name = "next_staff")
	private Integer nextStaff;

	@DiffIgnore
	@Column(name = "accessibility")
	private String accessibility;

	@Column(name = "allowdiscount")
	private boolean allowDiscount;

	@Column(name ="offerprice")
	private Double offerprice;


	@ManyToMany
	@LazyCollection(LazyCollectionOption.TRUE)
	@ToString.Exclude
	@DiffIgnore
	@JoinTable(name = "tblmserviceareaplangroupmapping", joinColumns = {@JoinColumn(name = "plangroupid")}, inverseJoinColumns = {@JoinColumn(name = "service_area_id")})
	private List<ServiceArea> servicearea = new ArrayList<>();


	@OneToMany(cascade = CascadeType.ALL,orphanRemoval = true,targetEntity = ProductPlanGroupMapping.class)
	@JoinColumn(name = "plan_group_id")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@DiffIgnore
	List<ProductPlanGroupMapping> productPlanGroupMappingList;

	@DiffIgnore
	@Column(name = "template_id", length = 40)
	private Long templateId;

	@Column(name="invoicetoorg")
	private Boolean invoiceToOrg;

	@Column(name="requiredapproval")
	private Boolean requiredApproval;

	@Column(name="mvnoName")
	private String mvnoName;


	public PlanGroup() {
	}


	public PlanGroup(PlanGroup planGroup) {
		this.planGroupId = planGroup.planGroupId;
		this.planGroupName = planGroup.getPlanGroupName();
		this.status = planGroup.getStatus();
		this.mvnoId = planGroup.getMvnoId();
		this.plantype = planGroup.getPlantype();
		this.planMode = planGroup.getPlanMode();
		this.isDelete = planGroup.getIsDelete();
		this.planMappingList = planGroup.getPlanMappingList();
		this.dbr = planGroup.getDbr();
		this.buId = planGroup.getBuId();
		this.planGroupType = planGroup.getPlanGroupType();
		this.category = planGroup.getCategory();
		this.nextTeamHierarchyMappingId = planGroup.getNextTeamHierarchyMappingId();
		this.nextStaff = planGroup.getNextStaff();
		this.accessibility = planGroup.getAccessibility();
		this.allowDiscount = planGroup.getInvoiceToOrg();
		this.offerprice = planGroup.getOfferprice();
	//	this.servicearea = servicearea;
		this.productPlanGroupMappingList = planGroup.getProductPlanGroupMappingList();
		//this.templateId = planGroup.getTemplateId();
		this.invoiceToOrg = planGroup.getInvoiceToOrg();
		this.requiredApproval = planGroup.getRequiredApproval();
	}
}
