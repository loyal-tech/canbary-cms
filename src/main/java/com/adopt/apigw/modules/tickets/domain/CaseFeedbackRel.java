package com.adopt.apigw.modules.tickets.domain;

import com.adopt.apigw.model.common.Auditable;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbltticketfeedbackmapping")
public class CaseFeedbackRel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ticketid")
    private Long ticketid;
    @Column(name = "support_type",length = 50)
    private String support_type;
    @Column(name = "staff_behavior",length = 50)
    private String staff_behavior;
    @Column(name = "payment_mode",length = 50)
    private String payment_mode;
    @Column(name = "current_bandwidth_feedback",length = 150)
    private String current_bandwidth_feedback;
    @Column(name = "current_price_feedback",length = 150)
    private String current_price_feedback;
    @Column(name = "referal_information",length = 100)
    private String referal_information;
    @Column(name = "technicial_support_feedback",length = 100)
    private String technicial_support_feedback;
    @Column(name = "overall_rating",length = 50)
    private String overall_rating;
    @Column(name = "service_experience",length = 50)
    private String service_experience;
    @Column(name = "problem_type",length = 50)
    private String problem_type;
    @Column(name = "behaviour_professionalism",length = 100)
    private String behaviour_professionalism;
    @Column(name = "reason",length = 50)
    private String reason;
    @Column(name = "rating")
    private Integer rating;
    @Column(name = "general_remarks",length = 250)
    private String general_remarks;
    @Column(name = "info_of_payment_mode",length = 50)
    private String infoOfPaymentMode;
    @Column(name = "CREATEDATE")
    private LocalDateTime created_date;
}
