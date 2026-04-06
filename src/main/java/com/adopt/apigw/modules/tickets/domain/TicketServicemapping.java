package com.adopt.apigw.modules.tickets.domain;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltticketservicemapping")
public class TicketServicemapping
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ticket_id")
    private Long ticketid;

    @Column(name = "service_id")
    private Long serviceid;
}
