package com.adopt.apigw.core.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "schedulerlock")
@Getter
@Setter
@NoArgsConstructor
public class SchedulerLock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "locked")
    private Boolean locked;

    @Column(name = "lockedby")
    private String lockedBy;

}
