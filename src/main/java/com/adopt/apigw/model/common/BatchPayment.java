package com.adopt.apigw.model.common;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "tblmbatchpayment")
public class BatchPayment {
    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Long id;

    @Column(name = "batchname", nullable = false, length = 50)
    private String batchname;
    @DiffIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "batchPayment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<BatchPaymentMapping> batchPaymentMappingList = new ArrayList<>();
    @DiffIgnore
    @Column(name = "createdby", nullable = false, length = 50)
    private String createBy;

    @Column(name = "is_deleted", nullable = false, length = 50)
    private Boolean isDeleted;

    @Column(name = "status", nullable = false, length = 50)
    private String status;
    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;
}