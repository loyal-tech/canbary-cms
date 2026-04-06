package com.adopt.apigw.modules.tickets.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tbltticketresolutionmapping")
public class TicketResolutionMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mappingId;

    private Long resolutionReasonId;

    private String resolutionDesc;

    @Column(name = "case_id")
    private Long caseId;

}
