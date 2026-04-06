
package com.adopt.apigw.model.postpaid;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerLedger;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerLedgerDetails;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerPayment;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBook;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Getter
@Setter
@Table(name = "tblpartners")
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Partner extends Auditable {

    @Id
   // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PARTNERID", nullable = false, length = 40)
    @DiffIgnore
    private Integer id;

    @Column(name = "PARTNERNAME", nullable = false, length = 40)
    private String name;

    @Column(name = "partner_code")
    private String prcode;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    @Column(name = "COMM_TYPE", nullable = false, length = 40)
    private String commtype;

    @Column(name = "COMM_REL_VALUE", length = 40)
    private Double commrelvalue;

    @Column(name = "balance", length = 40)
    private Double balance;

    @Column(name = "COMM_DUE_DAY", length = 40)
    private Integer commdueday;

    @Column(name = "NEXTBILLDATE", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextbilldate;

    @Column(name = "LASTBILLDATE", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastbilldate;

    @DiffIgnore
    @Column(name = "taxid", nullable = false, length = 40)
    private Integer taxid;

    @Column(name = "addresstype", nullable = false, length = 40)
    private String addresstype;

    @Column(name = "address1", nullable = false, length = 40)
    private String address1;

    @Column(name = "address2", nullable = false, length = 40)
    private String address2;

    @Column(name = "credit", nullable = false, length = 40)
    private Double credit;

    @Column(name = "city", nullable = false, length = 40)
    private Integer city;

    @Column(name = "state", nullable = false, length = 40)
    private Integer state;

    @Column(name = "country", nullable = false, length = 40)
    private Integer country;

    @Column(name = "pincode", nullable = false, length = 40)
    private String pincode;

    @Column(name = "mobile", nullable = false, length = 40)
    private String mobile;


    private String countryCode;

    @Column(name = "email", nullable = false, length = 40)
    private String email;

    @Column(name = "partner_type", nullable = false, length = 40)
    private String partnerType;

    @Column(name = "contact_person_name", nullable = false, length = 40)
    private String cpName;

    @Column(name = "company_name", nullable = false, length = 40)
    private String cname;

    @Column(name = "pan_details", nullable = false, length = 40)
    private String panName;


    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tblpartnerservicearearel", joinColumns = {@JoinColumn(name = "partnerid")}
            , inverseJoinColumns = {@JoinColumn(name = "serviceareaid")})
    private List<ServiceArea> serviceAreaList = new ArrayList<>();

    @ManyToOne
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "parentpartnerid")
    private Partner parentPartner;

    @ManyToOne()
    @JoinColumn(name = "pricebookid")
    @DiffIgnore
    private PriceBook priceBookId;

    @JsonManagedReference
    @OneToOne(mappedBy = "partner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private PartnerLedger partnerLedger;

    @DiffIgnore
    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "partner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PartnerLedgerDetails> partnerLedgerDetails = new ArrayList<>();

    @DiffIgnore
    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "partner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PartnerPayment> partnerPayments = new ArrayList<>();

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "commission_share_type", nullable = false, length = 40)
    private String commissionShareType;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name= "new_customer_count")
    private Long newCustomerCount = 0L;

    @Column(name= "renew_customer_count")
    private Long renewCustomerCount = 0L;

    @Column(name= "total_customer_count")
    private Long totalCustomerCount = 0L;

    @Column(name = "calendartype", nullable = false, length = 100,columnDefinition = "varchar(100) default 'English'")
    private String calendarType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column(name = "reset_date")
    private LocalDate resetDate;

    @Column(name= "credit_consume")
    private Double creditConsume = 0d;

    @Column(name = "region")
    private Long region ;

    @Column(name = "branch")
    private Long branch ;

    @Column(name = "bussiness_vertical")
    private Long bussinessvertical ;

    @Column(name = "dunning_activate_for")
    private String dunningActivateFor;

    @Column(name = "last_dunning_date")
    private LocalDateTime lastDunningDate;

    @Column(name = "is_dunning_enable")
    private Boolean isDunningEnable;

    @Column(name = "dunning_action")
    private String dunningAction;
    @Column(name = "is_visible_to_isp")
    private Boolean isVisibleToIsp;


    public Partner getParentPartner() {
        if (parentPartner == null) {
            return null;
        } else {
            return parentPartner;
        }
    }

	@Override
	public String toString() {
		return "Partner []";
	}

    public Partner(Integer id) {
        this.id = id;
    }

    public Partner(Partner partner) {
        this.id = partner.getId();
        this.mvnoId = partner.getMvnoId();
        this.buId = partner.getBuId();
        this.email = partner.getEmail();
        this.mobile = partner.getMobile();
        this.partnerType = partner.getPartnerType();
        this.isDelete = partner.getIsDelete();
        this.name = partner.getName();
        this.city = partner.getCity();
        this.country = partner.getCountry();
        this.state = partner.getState();
        this.branch = partner.getBranch();
        this.region = partner.getRegion();
        this.status = partner.getStatus();
        this.isVisibleToIsp=partner.isVisibleToIsp;

    }
}
