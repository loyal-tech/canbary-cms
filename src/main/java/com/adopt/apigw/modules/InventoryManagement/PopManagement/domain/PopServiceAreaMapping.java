package com.adopt.apigw.modules.InventoryManagement.PopManagement.domain;

import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblpopmanagemengservicearearel")
@Data
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
public class PopServiceAreaMapping {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pop_id", nullable = false, length = 40)
    private Long  popId;

    @Column(name = "servicearea_id", nullable = false, length = 40)
    private  Integer serviceId;

//    @Column(name = "created_on", nullable = false, length = 40)
//    private LocalDateTime createdOn;
//
//    @Column(name = "lastmodified_on", nullable = false, length = 40)
//    private LocalDateTime lastmodifiedOn ;
}
