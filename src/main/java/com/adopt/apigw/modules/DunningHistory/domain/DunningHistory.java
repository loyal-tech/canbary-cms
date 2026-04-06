package com.adopt.apigw.modules.DunningHistory.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbldunninghistory")
public class DunningHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="dunninghistoryid")
    private Long id;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "action")
    private String action;

    @Column(name = "staffid")
    private Long staffid;

    @Column(name = "custid")
    private Integer custid;

    @Column(name = "partnerid")
    private Long partnerid;

    @Column(name = "dunning_message_date")
    private LocalDateTime dunningMessageDate;

    @Column(name = "dunning_message")
    private String dunningMessage;


    @Column(name = "mvnoid")
    private Integer mvnoid;


}
