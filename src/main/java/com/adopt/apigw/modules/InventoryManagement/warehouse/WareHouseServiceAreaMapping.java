package com.adopt.apigw.modules.InventoryManagement.warehouse;

import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "tblwarehousemanagmentservicearearel")
@Data
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
public class WareHouseServiceAreaMapping {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "warehouse_id", nullable = false, length = 40)
    private Long  warehouseId;

    @Column(name = "serviceareaid", nullable = false, length = 40)
    private  Integer serviceId;

    @Column(name = "created_on", nullable = false, length = 40)
    private LocalDateTime createdOn;

    @Column(name = "lastmodified_on", nullable = false, length = 40)
    private LocalDateTime lastmodifiedOn ;

}
