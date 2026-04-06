package com.adopt.apigw.model.common;

import com.adopt.apigw.model.postpaid.City;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tblshorter")
@Data
public class Shorter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Long id;

    @Column
    private String hash;

    @Column(name = "original_url")
    private String originalUrl;

    @Column(name = "custid")
    private Integer custId;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "customer_username", length = 500)
    private String customerUsername;

    @Column(name = "planname", length = 500)
    private String planName;

    @Column(name = "mvnoid")
    private Integer mvnoId;

    @Column(name = "plan_duedate")
    private LocalDateTime planDueDate;

    @Column(name = "invoiceid")
    private Integer invoiceId;

    @Column(name = "token")
    private String token;

    @Column(name = "planid")
    private Integer planId;

    @Column(name = "ishashused")
    private Boolean ishashused = false;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "linktype")
    private String linkType;
}