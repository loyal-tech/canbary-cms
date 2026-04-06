package com.adopt.apigw.modules.TumilIdValidation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tblmhhidvalidation")
public class IdValidationResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "house_hold_id")
    private String householdId;

    @Column(name = "household_type")
    private String householdType;

    @Column(name = "fsr_id")
    private String fsrId;

    @Column(name = "fsr_name")
    private String fsrName;

    @Column(name = "brand")
    private String brand;

    @Column(name = "account")
    private String account;

    @Column(name = "plan")
    private String plan;

    @Column(name = "activation_status")
    private Boolean activationStatus;

    @Column(name = "activation_date")
    private LocalDate activationDate;


    @Column(name = "township_name")
    private String townshipName;

    @Column(name = "ward_name")
    private String wardName;

    @Column(name = "street_name")
    private String streetName;

    @Column(name = "house_no")
    private String houseNo;

    @Column(name = "building_name")
    private String buildingName;

    @Column(name="email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "mvno_id")
    private Integer mvnoId;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "speed")
    private String speed;

    @Column(name = "customer_id")
    private Integer customerId;
}
