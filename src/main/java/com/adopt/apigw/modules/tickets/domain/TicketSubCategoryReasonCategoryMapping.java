package com.adopt.apigw.modules.tickets.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tbltticketsubcategoryreasoncategorymapping")
public class TicketSubCategoryReasonCategoryMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_reason_category_id", nullable = false)
    private Long ticketReasonCategoryId;

    @Column(name = "ticket_reason_sub_category_id", nullable = false)
    private Long ticketReasonSubCategoryId;
}
