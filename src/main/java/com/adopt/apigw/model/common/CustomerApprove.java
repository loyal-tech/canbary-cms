package com.adopt.apigw.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tblcustapprove")
public class CustomerApprove {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Integer id;

    @Column(name = "customer_name", length = 40)
    private String custName;

    @Column(name = "current_status", length = 40)
    private String currentStatus;
    @DiffIgnore
    @Column(name = "new_status", length = 40)
    private String activeStatus;

    @Column(name = "current_staff", length = 40)
    private String currentStaff;

    @Column(name = "parent_staff", length = 40)
    private String parentStaff;

    @Column(name = "status", length = 40)
    private String status;

    @DiffIgnore
    @Column(name = "custid")
    private Integer customerID;
    @DiffIgnore
    @Column(name = "first_name")
    private String  firstName;
    @DiffIgnore
    @Column(name = "last_name")
    private String  lastName;
    @DiffIgnore
    @Column(name = "is_wallet_setteled")
    private Boolean  isWalletSetteled;

    @Column(name = "remark")
    private String  remark;
}
