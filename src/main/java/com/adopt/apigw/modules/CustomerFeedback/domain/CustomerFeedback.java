package com.adopt.apigw.modules.CustomerFeedback.domain;


import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tblmcustomerfeedback")
public class CustomerFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private Long id;

    @Column(name = "custid")
    private Long custId;

    @Column(name = "rating")
    private String rating;

    @Column(name = "feedback")
    private String feedback;

    @Column(name = "event")
    private String event;

    @Column(name = "is_delete")
    private Boolean isDelete;

    @Column(name = "MVNOID")
    private Integer mvnoId;

    @Column(name = "BUID")
    private Long buId;

    @Column(name = "createdate")
    private LocalDateTime createDate;


}
