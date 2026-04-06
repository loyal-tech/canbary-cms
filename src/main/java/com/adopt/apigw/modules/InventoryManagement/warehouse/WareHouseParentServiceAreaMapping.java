package com.adopt.apigw.modules.InventoryManagement.warehouse;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblwarehousemanagmentparentservicearearel")
@Data
@NoArgsConstructor
//@EntityListeners(AuditableListener.class)
public class WareHouseParentServiceAreaMapping {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "warehouse_id", nullable = false, length = 40)
    private Long  warehouseId;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "created_on", nullable = false, length = 40)
    private LocalDateTime createdOn;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "lastmodified_on", nullable = false, length = 40)
    private LocalDateTime lastmodifiedOn ;

    @Column(name = "parentserviceareaid", nullable = false, length = 40)
    private Integer parentServiceAreaId;
}
