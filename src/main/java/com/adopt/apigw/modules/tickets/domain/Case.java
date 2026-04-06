package com.adopt.apigw.modules.tickets.domain;

import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.modules.ResolutionReasons.domain.ResolutionReasons;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tblcases")
@EntityListeners(AuditableListener.class)
public class Case extends Auditable implements IBaseData2<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long caseId;

    private String caseTitle;
    private String caseType;
    private String caseNumber;
    private String caseFor;
    private String caseOrigin;
    private String caseStatus;
    private String priority;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "case_for_id")
    private Customers customers;

    private String caseForPartner;
    private String caseForZone;
    private LocalDate nextFollowupDate;
    private LocalTime nextFollowupTime;
    @CreationTimestamp
    private LocalDateTime caseStartedOn;
    @CreationTimestamp
    private LocalDateTime firstAssignedOn;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    @ManyToOne
    @JoinColumn(name = "current_assignee_id")
    private StaffUser currentAssignee;

    @ManyToOne
    @JoinColumn(name = "final_resolution_id")
    private ResolutionReasons finalResolution;

    @ManyToOne
    @JoinColumn(name = "final_resolved_by_id")
    private StaffUser finalResolvedBy;
    @ManyToOne
    @JoinColumn(name = "final_closed_by_id")
    private StaffUser finalClosedBy;
    @Column(name = "final_resolution_date")
    private LocalDateTime finalResolutionDate;
    @Column(name = "final_closed_date")
    private LocalDateTime finalClosedDate;

    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ticket")
    @OrderBy("id desc")
    private List<CaseUpdate> caseUpdateList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "partnerid")
    private Partner partner;

    @Column(name = "first_remark")
    private String firstRemark;

    @Transient
    private Long mobile;

    @Transient
    private String userName;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Transient
    private String customerName;
    @Transient
    private String assigneeName;

    private Integer rating;

    @Column(name = "customer_feedback")
    private String customerFeedback;

    private Long ticketReasonCategoryId;

    private Long reasonSubCategoryId;

    @Column(name = "group_reason_id")
    private Long groupReasonId;

    private Long tatMappingId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    private Long rootCauseReasonId;

    @Column(name = "source")
    private String source;

    @Column(name = "sub_source")
    private String subSource;

    @Column(name = "team_hir_mapping_id")
    private Long teamHierarchyMappingId;

    @OneToMany(targetEntity = TicketAssignStaffMapping.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_id")
    private List<TicketAssignStaffMapping> ticketAssignStaffMappings;

    private String department;

    @Column(name = "cust_additional_mobile_number")
    private String customerAdditionalMobileNumber;

    @Column(name = "cust_additional_email")
    private String customerAdditionalEmail;

    @Column(name = "parent_ticket_id")
    private Integer parentTicketId;

    @Column(name="helper_name")
    private String helperName;

    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
    private Integer lcoId;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(targetEntity = TicketServicemapping.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "ticket_id")
    List<TicketServicemapping> ticketServicemappingList;


    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return caseId;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDelete;
    }
    @Column(name="case_order", length=40,nullable = true)
    private Long case_order;


    @Column(name= "ticket_sla_time")
    private Integer caseSlaTime;

    @Column(name= "ticket_sla_unit")
    private String caseSlaUnit;

    @Transient
    private Long parentId;

    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "ticketid")
    @OrderBy("id desc")
//    @OneToMany(targetEntity = CaseFeedbackRel.class, cascade = CascadeType.ALL)
//    @JoinColumn(name = "ticketid")
    List<CaseFeedbackRel> caseFeedbackRel;

    @Column(name= "call_status", columnDefinition = "Boolean default false", nullable = false)
    private Boolean call_status= false;

    @Column(name= "is_closed", columnDefinition = "Boolean default false", nullable = false)
    private Boolean is_closed= false;

    @Column(name = "deacivate_reason", length = 200)
    private String deacivate_reason;

    @Transient
    private String reasonSubCategoryName;
    @Transient
    private String ticketReasonCategoryName;
    @Column(name = "serial_number")
    private String serialNumber;

    public Case() {
    }

    public Case(String caseNumber, String priority, String caseStatus, String customerName, String caseType, LocalDate nextFollowupDate, String assigneeName) {
        this.caseNumber = caseNumber;
        this.priority = priority;
        this.customerName = customerName;
        this.caseType = caseType;
        this.caseStatus = caseStatus;
        this.nextFollowupDate = nextFollowupDate;
        this.assigneeName = assigneeName;
    }
}
