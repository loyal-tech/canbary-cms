package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = "tblcustservicechargipedtls")
@EntityListeners(AuditableListener.class)
public class CustServiceChargeIPDetails extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Integer id;

    @Column(name = "custid",  nullable = false, length = 40)
    private Integer custId;

    @Column(name = "custservicemappingid", nullable = false,  length = 40)
    private Integer custServiceMappingId;

    @Column(name = "static_ip_address")
    private String staticIPAdrress;

    @Column(name = "static_ip_start_date")
    private LocalDateTime staticIPStartDate;

    @Column(name = "static_ip_end_date")
    private LocalDateTime staticIPEndDate;

    @Column(name = "charge_id", nullable = false, length = 40)
    private  Integer chargeId;

}
