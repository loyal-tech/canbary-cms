package com.adopt.apigw.modules.StaffLedgerTransaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tbltstaffledgertransactionmapping")
@Data
public class StaffLedgerTransactionMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "paymentid")
    private Integer paymentid;

    @Column(name = "transfferedid")
    private Integer transfferedid;

    @Column(name = "transfferedamount")
    private Double transfferedamount;

    @Column(name = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;


}
