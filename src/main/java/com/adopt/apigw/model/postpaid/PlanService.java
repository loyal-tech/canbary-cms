package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.modules.InventoryManagement.productCategory.ProductCategory;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceParameterMapping.domain.ServiceParamMapping;
import com.adopt.apigw.modules.ServiceParameters.domain.ServiceParameter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.TypeName;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "TBLMSERVICES")
@EntityListeners(AuditableListener.class)
public class PlanService extends Auditable<Integer> {
	
	
	/*
CREATE TABLE TBLMSERVICES
(
	serviceid SERIAL PRIMARY KEY,
	servicename varchar(255),
	CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);  
	 */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serviceid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "servicename", nullable = false, length = 40)
    private String name;

    @Column(name = "displayname", nullable = false, length = 40)
    private String displayName;

    @DiffIgnore
    @Column(name = "icname", nullable = false, length = 40)
    private String icname;

    @DiffIgnore
    @Column(name = "iccode", nullable = false, length = 40)
    private String iccode;
    
    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @DiffIgnore
    @Column(name = "is_qosv", nullable = false, columnDefinition = "Boolean default true")
    private Boolean isQoSV;

    @DiffIgnore
    @Column(name = "expiry",nullable = false,length = 100)
    private String expiry;

    @DiffIgnore
    private String ledgerId;

    @Column(name = "is_dtv")
    private Boolean is_dtv;

    @DiffIgnore
    @Column(name = "investmentcode_id")
    private Long investmentid;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @DiffIgnore
    @JoinTable(name = "tbltserviceinventorymapping",
            joinColumns = @JoinColumn(name = "serviceid", referencedColumnName = "serviceid"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "product_id"))
    private List<ProductCategory> productCategories = new ArrayList<>();


    @OneToMany(cascade = CascadeType.ALL,targetEntity = ServiceParamMapping.class,fetch = FetchType.LAZY)
    @JoinColumn(name="serviceid")
    @DiffIgnore
    List<ServiceParamMapping> serviceParamMappingList;

    @Column(name = "feasibility")
    @DiffIgnore
    private Boolean feasibility;

    @Column(name = "poc")
    @DiffIgnore
    private Boolean poc;

    @Column(name = "installation")
    @DiffIgnore
    private Boolean installation;

    @Column(name = "provisioning")
    @DiffIgnore
    private Boolean provisioning;

    @Column(name = "is_price_editable")
    @DiffIgnore
    private Boolean isPriceEditable;

    @DiffIgnore
    @Column(name = "feasibility_team_id")
    private Long feasibilityTeamId;

    @DiffIgnore
    @Column(name = "poc_team_id")
    private Long pocTeamId;

    @DiffIgnore
    @Column(name = "installation_team_id")
    private Long installationTeamId;

    @DiffIgnore
    @Column(name = "provisioning_team_id")
    private Long provisioningTeamId;

    @DiffIgnore
    @Column(name="is_service_through_lead")
    private Boolean isServiceThroughLead;

    @Transient
    @DiffIgnore
    private String createdByName;

    @Transient
    @DiffIgnore
    private String lastModifiedByName;

    @Transient
    @DiffIgnore
    private Integer createdById;

    @Column(name="is_deleted")
    private Boolean isDeleted = false;

    @Column(name="mvnoName")
    private String mvnoName;

}
