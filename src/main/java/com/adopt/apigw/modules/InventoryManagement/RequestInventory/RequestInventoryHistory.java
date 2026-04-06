package com.adopt.apigw.modules.InventoryManagement.RequestInventory;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@EntityListeners(AuditableListener.class)
@Table(name = "tblhrequestinventoryhistory")
@AllArgsConstructor
@NoArgsConstructor
public class RequestInventoryHistory extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "request_inventory_id")
    private Long requestInventoryId;

    @Column(name = "request_inventory_name")
    private String requestInventoryName;

    @Column(name = "request_name_id")
    private Long requestNameId;

    @Column(name = "request_to_warehouse_id")
    private Long requestToWarehouseId;

    @Column(name = "remarks")
    private String remarks;
}
