package com.adopt.apigw.model.postpaid;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = "tblpartnerlocationrel")
public class PartnerLocationMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partnerlocid", nullable = false, length = 40)
    private Integer id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "locationid")
    private Location location;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "partnerid")
    private Partner partner;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @UpdateTimestamp
    @Column(name = "lastmodified_on", nullable = true, updatable = true)
    private LocalDateTime updatedate;

}
