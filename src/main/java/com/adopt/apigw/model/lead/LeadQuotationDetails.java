package com.adopt.apigw.model.lead;

import com.adopt.apigw.rabbitMq.message.LeadQuotationWfDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "tbltqotationdetails")
public class LeadQuotationDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "quotation_detail_id")
    private Long quotationDetailId;

    @Column(name = "lead_id")
    private Long leadId;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "version_id")
    private Long versionId;

    @Column(name = "status")
    private String status;

    @Column(name= "BUID")
    private Long buId;

    @Column(name= "MVNOID")
    private Long mvnoId;

    @Column(name ="next_approve_staff_id")
    private Integer nextApproveStaffId;

    @Column(name = "next_team_mapping_id")
    private Integer nextTeamMappingId;

    @Column(name = "final_approved")
    private boolean finalApproved;

    public LeadQuotationDetails(LeadQuotationWfDTO leadQuotationWfDTO){
        setQuotationDetailId(leadQuotationWfDTO.getQuotationId());
        setLeadId(leadQuotationWfDTO.getLeadMasterId());
        setFirstName(leadQuotationWfDTO.getFirstName());
        setStatus(leadQuotationWfDTO.getStatus());
        setBuId(leadQuotationWfDTO.getBuId());
        setMvnoId(leadQuotationWfDTO.getMvnoId());
        setNextApproveStaffId(leadQuotationWfDTO.getNextApproveStaffId());
        setNextTeamMappingId(leadQuotationWfDTO.getNextTeamMappingId());
        setFinalApproved(leadQuotationWfDTO.getFinalApproved());
    }

}
