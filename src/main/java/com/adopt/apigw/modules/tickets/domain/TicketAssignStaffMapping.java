package com.adopt.apigw.modules.tickets.domain;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tblmticketassignstaffmapping")
@EntityListeners(AuditableListener.class)
public class TicketAssignStaffMapping extends Auditable<TicketAssignStaffMapping> {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id")
    private Long ticketId;

    private Integer staffId;
}
