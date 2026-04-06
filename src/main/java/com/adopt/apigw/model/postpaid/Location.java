package com.adopt.apigw.model.postpaid;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = "tbllocation")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "locationid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "name", nullable = false, length = 40)
    private String name;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @UpdateTimestamp
    @Column(name = "lastmodified_on", nullable = true, updatable = true)
    private LocalDateTime updatedate;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;
    
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;
}
