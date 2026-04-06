package com.adopt.apigw.model.radius;


import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@ToString
@Table(name = "tblmplanmaster")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plid", nullable = false, length = 40)
    private Integer id;
    @Column(nullable = false, length = 40)
    private String name;
    @Column(nullable = false, length = 40)
    private String planTYpe;

    @Column(nullable = false, length = 40)
    private Integer validity;

    @Column(nullable = false, length = 40)
    private String quota;

    @Column(nullable = false, length = 40)
    private String stml;
    @Column(name = "startdate", nullable = false, updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @Column(name = "enddate", nullable = false, updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;

    public Plan() {
    }

    public Plan(Integer id, String name, String planTYpe, Integer validity, String quota, String stml, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.planTYpe = planTYpe;
        this.validity = validity;
        this.quota = quota;
        this.stml = stml;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
