package com.adopt.apigw.model.postpaid;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "tblmextendvaliditymapping")
public class CustPlanExtendValidityMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer validityId;

    @DiffIgnore
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "custpackageid", referencedColumnName = "custpackageid")
    @ToString.Exclude
    private CustPlanMappping custPlanMappping;

    @Column(name = "downtime_start_date")
    private LocalDate downTimeStartDate;

    @Column(name = "downtime_expiry_date")
    private LocalDate downTimeExpiryDate;

    @Column(name = "extend_validity_remarks")
    private String extendValidityremarks;

    @DiffIgnore
    @Column(name = "custservicemappingid", nullable = false)
    private Integer custServiceMappingId;
}
