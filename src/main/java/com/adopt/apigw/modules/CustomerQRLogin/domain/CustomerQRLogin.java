package com.adopt.apigw.modules.CustomerQRLogin.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tblmcustomerqrlogin")
public class CustomerQRLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "username")
    private String username;

    @Column(name ="password")
    private String password;

    @Column(name = "status")
    private String status;

    @Column(name = "MVNOID")
    private Integer mvnoId;

    @Column(name = "createdate")
    private LocalDateTime createdate;

}
